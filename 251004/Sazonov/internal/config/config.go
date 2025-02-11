package config

import (
	"time"

	"github.com/spf13/viper"
)

type Config struct {
	Log     LogConfig
	Core    CoreConfig
	HTTP    HTTPServerConfig
	Storage StorageConfig
}

type CoreConfig struct {
	ShutdownTimeout time.Duration
}

type StorageConfig struct {
	Type     string
	User     string
	Password string
	Host     string
	Port     string
	DBName   string
	SSLMode  string
}

type LogConfig struct {
	Level    string
	Encoding string
}

type HTTPServerConfig struct {
	Port        string
	Timeout     time.Duration
	IdleTimeout time.Duration
}

func Load() Config {
	return Config{
		Log: LogConfig{
			Level:    viper.GetString("log.level"),
			Encoding: viper.GetString("log.encoding"),
		},
		Core: CoreConfig{
			ShutdownTimeout: viper.GetDuration("core.shutdown_timeout"),
		},
		Storage: StorageConfig{
			Type:     viper.GetString("storage.type"),
			User:     viper.GetString("storage.user"),
			Password: viper.GetString("storage.password"),
			Host:     viper.GetString("storage.host"),
			Port:     viper.GetString("storage.port"),
			DBName:   viper.GetString("storage.dbname"),
			SSLMode:  viper.GetString("storage.sslmode"),
		},
		HTTP: HTTPServerConfig{
			Port:        viper.GetString("http.port"),
			Timeout:     viper.GetDuration("http.timeout"),
			IdleTimeout: viper.GetDuration("http.idle_timeout"),
		},
	}
}
