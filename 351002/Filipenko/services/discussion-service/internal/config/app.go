package config

type AppConfig struct {
	AppName    string `envconfig:"APP_NAME" default:"NoteService"`
	AppAddress string `envconfig:"APP_ADDRESS" default:":24110"`
}
