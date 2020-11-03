// Code generated by piper's step-generator. DO NOT EDIT.

package cmd

import (
	"fmt"
	"os"
	"time"

	"github.com/SAP/jenkins-library/pkg/config"
	"github.com/SAP/jenkins-library/pkg/log"
	"github.com/SAP/jenkins-library/pkg/telemetry"
	"github.com/spf13/cobra"
)

type abapEnvironmentCreateSystemOptions struct {
	CfAPIEndpoint                  string `json:"cfApiEndpoint,omitempty"`
	Username                       string `json:"username,omitempty"`
	Password                       string `json:"password,omitempty"`
	CfOrg                          string `json:"cfOrg,omitempty"`
	CfSpace                        string `json:"cfSpace,omitempty"`
	CfService                      string `json:"cfService,omitempty"`
	CfServicePlan                  string `json:"cfServicePlan,omitempty"`
	CfServiceInstance              string `json:"cfServiceInstance,omitempty"`
	ServiceManifest                string `json:"serviceManifest,omitempty"`
	AbapSystemAdminEmail           string `json:"abapSystemAdminEmail,omitempty"`
	AbapSystemDescription          string `json:"abapSystemDescription,omitempty"`
	AbapSystemIsDevelopmentAllowed bool   `json:"abapSystemIsDevelopmentAllowed,omitempty"`
	AbapSystemID                   string `json:"abapSystemID,omitempty"`
	AbapSystemSizeOfPersistence    int    `json:"abapSystemSizeOfPersistence,omitempty"`
	AbapSystemSizeOfRuntime        int    `json:"abapSystemSizeOfRuntime,omitempty"`
	AddonDescriptorFileName        string `json:"addonDescriptorFileName,omitempty"`
	IncludeAddon                   bool   `json:"includeAddon,omitempty"`
}

// AbapEnvironmentCreateSystemCommand Creates a SAP Cloud Platform ABAP Environment system (aka Steampunk system)
func AbapEnvironmentCreateSystemCommand() *cobra.Command {
	const STEP_NAME = "abapEnvironmentCreateSystem"

	metadata := abapEnvironmentCreateSystemMetadata()
	var stepConfig abapEnvironmentCreateSystemOptions
	var startTime time.Time

	var createAbapEnvironmentCreateSystemCmd = &cobra.Command{
		Use:   STEP_NAME,
		Short: "Creates a SAP Cloud Platform ABAP Environment system (aka Steampunk system)",
		Long:  `creates a SAP Cloud Platform ABAP Environment system (aka Steampunk system)`,
		PreRunE: func(cmd *cobra.Command, _ []string) error {
			startTime = time.Now()
			log.SetStepName(STEP_NAME)
			log.SetVerbose(GeneralConfig.Verbose)

			path, _ := os.Getwd()
			fatalHook := &log.FatalHook{CorrelationID: GeneralConfig.CorrelationID, Path: path}
			log.RegisterHook(fatalHook)

			err := PrepareConfig(cmd, &metadata, STEP_NAME, &stepConfig, config.OpenPiperFile)
			if err != nil {
				log.SetErrorCategory(log.ErrorConfiguration)
				return err
			}
			log.RegisterSecret(stepConfig.Username)
			log.RegisterSecret(stepConfig.Password)

			if len(GeneralConfig.HookConfig.SentryConfig.Dsn) > 0 {
				sentryHook := log.NewSentryHook(GeneralConfig.HookConfig.SentryConfig.Dsn, GeneralConfig.CorrelationID)
				log.RegisterHook(&sentryHook)
			}

			return nil
		},
		Run: func(_ *cobra.Command, _ []string) {
			telemetryData := telemetry.CustomData{}
			telemetryData.ErrorCode = "1"
			handler := func() {
				config.RemoveVaultSecretFiles()
				telemetryData.Duration = fmt.Sprintf("%v", time.Since(startTime).Milliseconds())
				telemetryData.ErrorCategory = log.GetErrorCategory().String()
				telemetry.Send(&telemetryData)
			}
			log.DeferExitHandler(handler)
			defer handler()
			telemetry.Initialize(GeneralConfig.NoTelemetry, STEP_NAME)
			abapEnvironmentCreateSystem(stepConfig, &telemetryData)
			telemetryData.ErrorCode = "0"
			log.Entry().Info("SUCCESS")
		},
	}

	addAbapEnvironmentCreateSystemFlags(createAbapEnvironmentCreateSystemCmd, &stepConfig)
	return createAbapEnvironmentCreateSystemCmd
}

func addAbapEnvironmentCreateSystemFlags(cmd *cobra.Command, stepConfig *abapEnvironmentCreateSystemOptions) {
	cmd.Flags().StringVar(&stepConfig.CfAPIEndpoint, "cfApiEndpoint", `https://api.cf.eu10.hana.ondemand.com`, "Cloud Foundry API endpoint")
	cmd.Flags().StringVar(&stepConfig.Username, "username", os.Getenv("PIPER_username"), "User or E-Mail for CF")
	cmd.Flags().StringVar(&stepConfig.Password, "password", os.Getenv("PIPER_password"), "Password for Cloud Foundry User")
	cmd.Flags().StringVar(&stepConfig.CfOrg, "cfOrg", os.Getenv("PIPER_cfOrg"), "Cloud Foundry org")
	cmd.Flags().StringVar(&stepConfig.CfSpace, "cfSpace", os.Getenv("PIPER_cfSpace"), "Cloud Foundry Space")
	cmd.Flags().StringVar(&stepConfig.CfService, "cfService", os.Getenv("PIPER_cfService"), "Parameter for Cloud Foundry Service to be used for creating Cloud Foundry Service")
	cmd.Flags().StringVar(&stepConfig.CfServicePlan, "cfServicePlan", os.Getenv("PIPER_cfServicePlan"), "Parameter for Cloud Foundry Service Plan to be used when creating a Cloud Foundry Service")
	cmd.Flags().StringVar(&stepConfig.CfServiceInstance, "cfServiceInstance", os.Getenv("PIPER_cfServiceInstance"), "Parameter for naming the Service Instance when creating a Cloud Foundry Service")
	cmd.Flags().StringVar(&stepConfig.ServiceManifest, "serviceManifest", os.Getenv("PIPER_serviceManifest"), "Path to Cloud Foundry Service Manifest in YAML format for multiple service creations that are being passed to a Create-Service-Push Cloud Foundry cli plugin")
	cmd.Flags().StringVar(&stepConfig.AbapSystemAdminEmail, "abapSystemAdminEmail", os.Getenv("PIPER_abapSystemAdminEmail"), "Admin E-Mail address for the initial administrator of the system")
	cmd.Flags().StringVar(&stepConfig.AbapSystemDescription, "abapSystemDescription", `Test system created by an automated pipeline`, "Description for the ABAP Environment system")
	cmd.Flags().BoolVar(&stepConfig.AbapSystemIsDevelopmentAllowed, "abapSystemIsDevelopmentAllowed", true, "This parameter determines, if development is allowed on the system")
	cmd.Flags().StringVar(&stepConfig.AbapSystemID, "abapSystemID", `H02`, "The three character name of the system - maps to 'sapSystemName'")
	cmd.Flags().IntVar(&stepConfig.AbapSystemSizeOfPersistence, "abapSystemSizeOfPersistence", 0, "The size of the persistence")
	cmd.Flags().IntVar(&stepConfig.AbapSystemSizeOfRuntime, "abapSystemSizeOfRuntime", 0, "The size of the runtime")
	cmd.Flags().StringVar(&stepConfig.AddonDescriptorFileName, "addonDescriptorFileName", os.Getenv("PIPER_addonDescriptorFileName"), "The file name of the addonDescriptor")
	cmd.Flags().BoolVar(&stepConfig.IncludeAddon, "includeAddon", false, "Must be set to true to install the addon provided via 'addonDescriptorFileName'")

	cmd.MarkFlagRequired("cfApiEndpoint")
	cmd.MarkFlagRequired("username")
	cmd.MarkFlagRequired("password")
	cmd.MarkFlagRequired("cfOrg")
	cmd.MarkFlagRequired("cfSpace")
}

// retrieve step metadata
func abapEnvironmentCreateSystemMetadata() config.StepData {
	var theMetaData = config.StepData{
		Metadata: config.StepMetadata{
			Name:    "abapEnvironmentCreateSystem",
			Aliases: []config.Alias{},
		},
		Spec: config.StepSpec{
			Inputs: config.StepInputs{
				Parameters: []config.StepParameters{
					{
						Name:        "cfApiEndpoint",
						ResourceRef: []config.ResourceReference{},
						Scope:       []string{"PARAMETERS", "STAGES", "STEPS", "GENERAL"},
						Type:        "string",
						Mandatory:   true,
						Aliases:     []config.Alias{{Name: "cloudFoundry/apiEndpoint"}},
					},
					{
						Name: "username",
						ResourceRef: []config.ResourceReference{
							{
								Name:  "cfCredentialsId",
								Param: "username",
								Type:  "secret",
							},

							{
								Name:  "",
								Paths: []string{"$(vaultPath)/cloudfoundry-$(cfOrg)-$(cfSpace)", "$(vaultBasePath)/$(vaultPipelineName)/cloudfoundry-$(cfOrg)-$(cfSpace)", "$(vaultBasePath)/GROUP-SECRETS/cloudfoundry-$(cfOrg)-$(cfSpace)"},
								Type:  "vaultSecret",
							},
						},
						Scope:     []string{"PARAMETERS", "STAGES", "STEPS"},
						Type:      "string",
						Mandatory: true,
						Aliases:   []config.Alias{},
					},
					{
						Name: "password",
						ResourceRef: []config.ResourceReference{
							{
								Name:  "cfCredentialsId",
								Param: "password",
								Type:  "secret",
							},

							{
								Name:  "",
								Paths: []string{"$(vaultPath)/cloudfoundry-$(cfOrg)-$(cfSpace)", "$(vaultBasePath)/$(vaultPipelineName)/cloudfoundry-$(cfOrg)-$(cfSpace)", "$(vaultBasePath)/GROUP-SECRETS/cloudfoundry-$(cfOrg)-$(cfSpace)"},
								Type:  "vaultSecret",
							},
						},
						Scope:     []string{"PARAMETERS", "STAGES", "STEPS"},
						Type:      "string",
						Mandatory: true,
						Aliases:   []config.Alias{},
					},
					{
						Name:        "cfOrg",
						ResourceRef: []config.ResourceReference{},
						Scope:       []string{"PARAMETERS", "STAGES", "STEPS", "GENERAL"},
						Type:        "string",
						Mandatory:   true,
						Aliases:     []config.Alias{{Name: "cloudFoundry/org"}},
					},
					{
						Name:        "cfSpace",
						ResourceRef: []config.ResourceReference{},
						Scope:       []string{"PARAMETERS", "STAGES", "STEPS", "GENERAL"},
						Type:        "string",
						Mandatory:   true,
						Aliases:     []config.Alias{{Name: "cloudFoundry/space"}},
					},
					{
						Name:        "cfService",
						ResourceRef: []config.ResourceReference{},
						Scope:       []string{"PARAMETERS", "STAGES", "STEPS", "GENERAL"},
						Type:        "string",
						Mandatory:   false,
						Aliases:     []config.Alias{{Name: "cloudFoundry/service"}},
					},
					{
						Name:        "cfServicePlan",
						ResourceRef: []config.ResourceReference{},
						Scope:       []string{"PARAMETERS", "STAGES", "STEPS", "GENERAL"},
						Type:        "string",
						Mandatory:   false,
						Aliases:     []config.Alias{{Name: "cloudFoundry/servicePlan"}},
					},
					{
						Name:        "cfServiceInstance",
						ResourceRef: []config.ResourceReference{},
						Scope:       []string{"PARAMETERS", "STAGES", "STEPS", "GENERAL"},
						Type:        "string",
						Mandatory:   false,
						Aliases:     []config.Alias{{Name: "cloudFoundry/serviceInstance"}},
					},
					{
						Name:        "serviceManifest",
						ResourceRef: []config.ResourceReference{},
						Scope:       []string{"PARAMETERS", "STAGES", "STEPS", "GENERAL"},
						Type:        "string",
						Mandatory:   false,
						Aliases:     []config.Alias{{Name: "cloudFoundry/serviceManifest"}, {Name: "cfServiceManifest"}},
					},
					{
						Name:        "abapSystemAdminEmail",
						ResourceRef: []config.ResourceReference{},
						Scope:       []string{"PARAMETERS", "STAGES", "STEPS", "GENERAL"},
						Type:        "string",
						Mandatory:   false,
						Aliases:     []config.Alias{},
					},
					{
						Name:        "abapSystemDescription",
						ResourceRef: []config.ResourceReference{},
						Scope:       []string{"PARAMETERS", "STAGES", "STEPS", "GENERAL"},
						Type:        "string",
						Mandatory:   false,
						Aliases:     []config.Alias{},
					},
					{
						Name:        "abapSystemIsDevelopmentAllowed",
						ResourceRef: []config.ResourceReference{},
						Scope:       []string{"PARAMETERS", "STAGES", "STEPS", "GENERAL"},
						Type:        "bool",
						Mandatory:   false,
						Aliases:     []config.Alias{},
					},
					{
						Name:        "abapSystemID",
						ResourceRef: []config.ResourceReference{},
						Scope:       []string{"PARAMETERS", "STAGES", "STEPS", "GENERAL"},
						Type:        "string",
						Mandatory:   false,
						Aliases:     []config.Alias{},
					},
					{
						Name:        "abapSystemSizeOfPersistence",
						ResourceRef: []config.ResourceReference{},
						Scope:       []string{"PARAMETERS", "STAGES", "STEPS", "GENERAL"},
						Type:        "int",
						Mandatory:   false,
						Aliases:     []config.Alias{},
					},
					{
						Name:        "abapSystemSizeOfRuntime",
						ResourceRef: []config.ResourceReference{},
						Scope:       []string{"PARAMETERS", "STAGES", "STEPS", "GENERAL"},
						Type:        "int",
						Mandatory:   false,
						Aliases:     []config.Alias{},
					},
					{
						Name:        "addonDescriptorFileName",
						ResourceRef: []config.ResourceReference{},
						Scope:       []string{"PARAMETERS", "STAGES", "STEPS", "GENERAL"},
						Type:        "string",
						Mandatory:   false,
						Aliases:     []config.Alias{},
					},
					{
						Name:        "includeAddon",
						ResourceRef: []config.ResourceReference{},
						Scope:       []string{"PARAMETERS", "STAGES"},
						Type:        "bool",
						Mandatory:   false,
						Aliases:     []config.Alias{},
					},
				},
			},
		},
	}
	return theMetaData
}
