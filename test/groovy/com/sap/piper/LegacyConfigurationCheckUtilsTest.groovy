package com.sap.piper

import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import org.junit.rules.RuleChain
import util.BasePiperTest
import util.Rules

import static org.junit.Assert.assertEquals


class LegacyConfigurationCheckUtilsTest extends BasePiperTest {
    String echoOutput = ""

    @Rule
    public RuleChain ruleChain = Rules
        .getCommonRules(this)
        .around(ExpectedException.none())


    @Before
    void init() {
        DefaultValueCache.createInstance([
            steps: [
                customStep: [
                    param: 'test'
                ]
            ]
        ])
        helper.registerAllowedMethod('addBadge', [Map], {return})
        helper.registerAllowedMethod('createSummary', [Map], {return})
        helper.registerAllowedMethod("echo", [String.class], { s ->
            if (echoOutput) {
                echoOutput += "\n"
            }
            echoOutput += s
        })
        helper.registerAllowedMethod('findFiles', [Map], {m ->
            if(m.glob == '**/package.json') {
                return [new File("package.json")].toArray()
            } else {
                return []
            }
        })
        helper.registerAllowedMethod('readJSON', [Map], { m ->
            if (m.file.contains('package.json')) {
                return [scripts: [oldNpmScriptName: "echo test",
                                  npmScript2: "echo test"]]
            } else {
                return [:]
            }
        })
    }

    @Test
    void testCheckForRemovedConfigKeys() {
        nullScript.commonPipelineEnvironment.configuration = [steps: [someStep: [oldConfigKey: false]]]
        Map configChanges = [oldConfigKey: [steps: ['someStep'], customMessage: "test"]]

        List errors = LegacyConfigurationCheckUtils.checkForRemovedOrReplacedConfigKeys(nullScript, configChanges)

        assertEquals(errors, ["Your pipeline configuration contains the configuration key oldConfigKey for the step someStep. " +
            "This configuration option was removed. test"])
    }

    @Test
    void testCheckForReplacedConfigKeys() {
        nullScript.commonPipelineEnvironment.configuration = [steps: [someStep: [oldConfigKey: false]]]
        Map configChanges = [oldConfigKey: [steps: ['someStep'], newConfigKey: "newConfigKey", customMessage: "test"]]

        List errors = LegacyConfigurationCheckUtils.checkForRemovedOrReplacedConfigKeys(nullScript, configChanges)

        assertEquals(errors, ["Your pipeline configuration contains the configuration key oldConfigKey for the step someStep. " +
            "This configuration option was removed. Please use the parameter newConfigKey instead. test"])
    }

    @Test
    void testCheckForRemovedConfigKeysWithWarning() {
        String expectedWarning = "[WARNING] Your pipeline configuration contains the configuration key oldConfigKey for the step someStep. " +
            "This configuration option was removed. test"

        nullScript.commonPipelineEnvironment.configuration = [steps: [someStep: [oldConfigKey: false]]]
        Map configChanges = [oldConfigKey: [steps: ['someStep'], warnInsteadOfError: true, customMessage: "test"]]

        List errors = LegacyConfigurationCheckUtils.checkForRemovedOrReplacedConfigKeys(nullScript, configChanges)

        assertEquals(expectedWarning, echoOutput)
        assertEquals(errors, [])
    }

    @Test
    void testCheckForRemovedStageConfigKeys() {
        nullScript.commonPipelineEnvironment.configuration = [stages: [someStage: [oldConfigKey: false]]]
        Map configChanges = [oldConfigKey: [stages: ['someStage']]]

        List errors = LegacyConfigurationCheckUtils.checkForRemovedOrReplacedConfigKeys(nullScript, configChanges)

        assertEquals(errors, ["Your pipeline configuration contains the configuration key oldConfigKey for the stage someStage. " +
            "This configuration option was removed. "])
    }

    @Test
    void testCheckForRemovedGeneralConfigKeys() {
        nullScript.commonPipelineEnvironment.configuration = [general: [oldConfigKey: false]]
        Map configChanges = [oldConfigKey: [general: true]]

        List errors = LegacyConfigurationCheckUtils.checkForRemovedOrReplacedConfigKeys(nullScript, configChanges)

        assertEquals(errors, ["Your pipeline configuration contains the configuration key oldConfigKey in the general section. " +
            "This configuration option was removed. "])
    }

    @Test
    void testCheckForRemovedPostActionConfigKeys() {
        nullScript.commonPipelineEnvironment.configuration = [postActions: [oldConfigKey: false]]
        Map configChanges = [oldConfigKey: [postAction: true]]

        List errors = LegacyConfigurationCheckUtils.checkForRemovedOrReplacedConfigKeys(nullScript, configChanges)

        assertEquals(errors, ["Your pipeline configuration contains the configuration key oldConfigKey in the postActions section. " +
            "This configuration option was removed. "])
    }

    @Test
    void testCheckForReplacedStep() {
        String oldStep = "oldStep"
        nullScript.commonPipelineEnvironment.configuration = [steps: [oldStep: [configKey: false]]]
        Map configChanges = [oldStep: [newStepName: "newStep", customMessage: "test"]]

        List errors = LegacyConfigurationCheckUtils.checkForRemovedOrReplacedSteps(nullScript, configChanges)

        assertEquals(errors, ["Your pipeline configuration contains configuration for the step $oldStep. " +
            "This step has been removed. Please configure the step newStep instead. test"])
    }

    @Test
    void testCheckForRemovedStep() {
        String oldStep = "oldStep"
        nullScript.commonPipelineEnvironment.configuration = [steps: [oldStep: [configKey: false]]]
        Map configChanges = [oldStep: [customMessage: "test"]]

        List errors = LegacyConfigurationCheckUtils.checkForRemovedOrReplacedSteps(nullScript, configChanges)

        assertEquals(errors, ["Your pipeline configuration contains configuration for the step $oldStep. " +
          "This step has been removed. test"])
    }

    @Test
    void testCheckForRemovedStepOnlyProjectConfig() {
        DefaultValueCache.createInstance([
            steps: [
                oldStep: [
                    configKey: false
                ]
            ]
        ])
        nullScript.commonPipelineEnvironment.configuration = [steps: [newStep: [configKey: false]]]
        Map configChanges = [oldStep: [onlyCheckProjectConfig: true]]

        List errors = LegacyConfigurationCheckUtils.checkForRemovedOrReplacedSteps(nullScript, configChanges)

        assertEquals(errors, [])
    }

    @Test
    void testCheckForReplacedStage() {
        String oldStage = "oldStage"
        nullScript.commonPipelineEnvironment.configuration = [stages: [oldStage: [configKey: false]]]
        Map configChanges = [oldStage: [newStageName: 'newStage', customMessage: "test"]]

        List errors = LegacyConfigurationCheckUtils.checkForRemovedOrReplacedStages(nullScript, configChanges)

        assertEquals(errors, ["Your pipeline configuration contains configuration for the stage $oldStage. " +
            "This stage has been removed. Please configure the stage newStage instead. test"])
    }

    @Test
    void testCheckForRemovedStage() {
        String oldStage = "oldStage"
        nullScript.commonPipelineEnvironment.configuration = [stages: [oldStage: [configKey: false]]]
        Map configChanges = [oldStage: []]

        List errors = LegacyConfigurationCheckUtils.checkForRemovedOrReplacedStages(nullScript, configChanges)

        assertEquals(errors, ["Your pipeline configuration contains configuration for the stage $oldStage. " +
            "This stage has been removed. "])
    }

    @Test
    void testCheckForStageParameterTypeChanged() {
        String stageName = "productionDeployment"
        nullScript.commonPipelineEnvironment.configuration = [stages: [productionDeployment: [configKeyOldType: "string"]]]
        Map configChanges = [configKeyOldType: [oldType: "String", newType: "List", stages: ["productionDeployment", "endToEndTests"], customMessage: "test"]]

        List errors = LegacyConfigurationCheckUtils.checkForParameterTypeChanged(nullScript, configChanges)

        assertEquals(errors, ["Your pipeline configuration contains the configuration key configKeyOldType for the stage $stageName. " +
            "The type of this configuration parameter was changed from String to List. test"])
    }

    @Test
    void testCheckForStepParameterTypeChanged() {
        String stepName = "testStep"
        nullScript.commonPipelineEnvironment.configuration = [steps: [testStep: [configKeyOldType: "string"]]]
        Map configChanges = [configKeyOldType: [oldType: "String", newType: "List", steps: ["testStep"], customMessage: "test"]]

        List errors = LegacyConfigurationCheckUtils.checkForParameterTypeChanged(nullScript, configChanges)

        assertEquals(errors, ["Your pipeline configuration contains the configuration key configKeyOldType for the step $stepName. " +
            "The type of this configuration parameter was changed from String to List. test"])
    }

    @Test
    void testCheckForGeneralParameterTypeChanged() {
        String key = "configKeyOldType"
        nullScript.commonPipelineEnvironment.configuration = [general: [configKeyOldType: "string"]]
        Map configChanges = [configKeyOldType: [oldType: "String", newType: "List", general: true, customMessage: "test"]]

        List errors = LegacyConfigurationCheckUtils.checkForParameterTypeChanged(nullScript, configChanges)

        assertEquals(errors, ["Your pipeline configuration contains the configuration key $key in the general section. " +
            "The type of this configuration parameter was changed from String to List. test"])
    }

    @Test
    void testCheckForUnsupportedParameterTypeChanged() {
        String expectedWarning = "Your legacy config settings contain an entry for parameterTypeChanged with the key configKeyOldType with the unsupported type Map. " +
            "Currently only the type 'String' is supported."
        nullScript.commonPipelineEnvironment.configuration = [steps: [testStep: [configKeyOldType: [test: true]]]]
        Map configChanges = [configKeyOldType: [oldType: "Map", newType: "List", steps: ["testStep"], customMessage: "test"]]

        LegacyConfigurationCheckUtils.checkForParameterTypeChanged(nullScript, configChanges)

        assertEquals(expectedWarning, echoOutput)
    }

    @Test
    void testCheckForRenamedNpmScripts() {
        Map configChanges = [oldNpmScriptName: [newScriptName: "newNpmScriptName", customMessage: "test"]]

        List errors = LegacyConfigurationCheckUtils.checkForRenamedNpmScripts(nullScript, configChanges)
        assertEquals(errors, ["Your package.json file package.json contains an npm script using the deprecated name oldNpmScriptName. " +
            "Please rename the script to newNpmScriptName, since the script oldNpmScriptName will not be executed by the pipeline anymore. test"])
    }

    @Test
    void testCheckForRenamedNpmScriptsWithWarning() {
        Map configChanges = [oldNpmScriptName: [newScriptName: "newNpmScriptName", warnInsteadOfError: true, customMessage: "test"]]
        String expectedWarning = "[WARNING] Your package.json file package.json contains an npm script using the deprecated name oldNpmScriptName. " +
            "Please rename the script to newNpmScriptName, since the script oldNpmScriptName will not be executed by the pipeline anymore. test"

        LegacyConfigurationCheckUtils.checkForRenamedNpmScripts(nullScript, configChanges)

        assertEquals(expectedWarning, echoOutput)
    }

    @Test
    void testCheckConfigurationRemovedOrReplacedConfigKeys() {
        nullScript.commonPipelineEnvironment.configuration = [steps: [someStep: [oldConfigKey: false]]]
        Map configChanges = [
            removedOrReplacedConfigKeys: [
                oldConfigKey: [
                    steps: ['someStep'],
                    customMessage: "test"
                ]
            ]
        ]

        String exception = "Failing pipeline due to configuration errors. Please see log output above."
        String output = "Your pipeline configuration contains the configuration key oldConfigKey for the step someStep. " +
            "This configuration option was removed. test"

        assertExceptionAndOutput(exception, output) {
            LegacyConfigurationCheckUtils.checkConfiguration(nullScript, configChanges)
        }
    }

    @Test
    void testCheckConfigurationRemovedOrReplacedSteps() {
        nullScript.commonPipelineEnvironment.configuration = [steps: [oldStep: [configKey: false]]]
        Map configChanges = [
            removedOrReplacedSteps: [
                oldStep: [
                    newStepName: 'newStep',
                    customMessage: "test"
                ]
            ]
        ]

        String exception = "Failing pipeline due to configuration errors. Please see log output above."
        String output = "Your pipeline configuration contains configuration for the step oldStep. " +
            "This step has been removed. Please configure the step newStep instead. test"

        assertExceptionAndOutput(exception, output) {
            LegacyConfigurationCheckUtils.checkConfiguration(nullScript, configChanges)
        }
    }

    @Test
    void testCheckConfigurationRemovedOrReplacedStages() {
        nullScript.commonPipelineEnvironment.configuration = [stages: [oldStage: [configKey: false]]]
        Map configChanges = [
            removedOrReplacedStages: [
                oldStage: []
            ]
        ]

        String exception = "Failing pipeline due to configuration errors. Please see log output above."
        String output = "Your pipeline configuration contains configuration for the stage oldStage. " +
            "This stage has been removed. "

        assertExceptionAndOutput(exception, output) {
            LegacyConfigurationCheckUtils.checkConfiguration(nullScript, configChanges)
        }
    }

    @Test
    void testCheckConfigurationParameterTypeChanged() {
        nullScript.commonPipelineEnvironment.configuration = [steps: [testStep: [configKeyOldType: "string"]]]
        Map configChanges = [
            parameterTypeChanged: [
                configKeyOldType: [
                    oldType: "String",
                    newType: "List",
                    steps: ["testStep"],
                    customMessage: "test"]
            ]
        ]

        String exception = "Failing pipeline due to configuration errors. Please see log output above."
        String output = "Your pipeline configuration contains the configuration key configKeyOldType for the step testStep. " +
            "The type of this configuration parameter was changed from String to List. test"

        assertExceptionAndOutput(exception, output) {
            LegacyConfigurationCheckUtils.checkConfiguration(nullScript, configChanges)
        }
    }

    @Test
    void testCheckConfigurationRenamedNpmScript() {
        Map configChanges = [
            renamedNpmScript: [
                oldNpmScriptName: [
                    newScriptName: "newNpmScriptName",
                    customMessage: "test"]
            ]
        ]

        String exception = "Failing pipeline due to configuration errors. Please see log output above."
        String output = "Your package.json file package.json contains an npm script using the deprecated name oldNpmScriptName. " +
            "Please rename the script to newNpmScriptName, since the script oldNpmScriptName will not be executed by the pipeline anymore. test"

        assertExceptionAndOutput(exception, output) {
            LegacyConfigurationCheckUtils.checkConfiguration(nullScript, configChanges)
        }
    }

    @Test
    void testCheckConfigurationMultipleErrors() {
        nullScript.commonPipelineEnvironment.configuration = [steps: [testStep: [configKeyOldType: "string"]]]
        Map configChanges = [
            renamedNpmScript: [
                oldNpmScriptName: [
                    newScriptName: "newNpmScriptName",
                    customMessage: "test"]
            ],
            parameterTypeChanged: [
                configKeyOldType: [
                    oldType: "String",
                    newType: "List",
                    steps: ["testStep"],
                    customMessage: "test"]
            ]
        ]

        String exception = "Failing pipeline due to configuration errors. Please see log output above."
        String output = "Your pipeline configuration file contains the following errors:\n" +
            "Your pipeline configuration contains the configuration key configKeyOldType for the step testStep. " +
            "The type of this configuration parameter was changed from String to List. test\n" +
            "Your package.json file package.json contains an npm script using the deprecated name oldNpmScriptName. " +
            "Please rename the script to newNpmScriptName, since the script oldNpmScriptName will not be executed by the pipeline anymore. test"

        assertExceptionAndOutput(exception, output) {
            LegacyConfigurationCheckUtils.checkConfiguration(nullScript, configChanges)
        }
    }

    private void assertExceptionAndOutput(String exception, String output, Closure body) {
        String actualException = ""
        try {
            body()
        } catch (Exception e) {
            actualException = e.getMessage()
        }
        assertEquals(exception, actualException)
        assertEquals(output, echoOutput)
    }
}
