package server

import (
	"time"

	"github.com/spf13/viper"
)

type Config struct {
	Port         int
	ShutdownTime time.Duration
	ReadTime     time.Duration
}

func NewDefaultConfig() Config {
	return Config{
		Port:         viper.GetInt("server.port"),
		ShutdownTime: viper.GetDuration("server.shutdown_time"),
		ReadTime:     viper.GetDuration("server.read_time"),
	}
}
