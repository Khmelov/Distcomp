package config

import (
	"fmt"
	"github.com/kelseyhightower/envconfig"
)

type Config struct {
	AppConfig   AppConfig
	Cassandra   CassandraConfig
	KafkaConfig KafkaConfig
}

func GetConfig() (Config, error) {
	var conf Config
	if err := envconfig.Process("", &conf); err != nil {
		return Config{}, fmt.Errorf("read config from env vars: %w", err)
	}
	return conf, nil
}
