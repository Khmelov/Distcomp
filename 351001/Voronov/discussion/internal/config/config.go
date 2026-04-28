package config

import (
	"errors"

	"github.com/ilyakaznacheev/cleanenv"
)

type Config struct {
	HTTPPort      string `env:"DISCUSSION_PORT"`
	CassandraHost string `env:"CASSANDRA_HOST" env-default:"localhost"`
	CassandraPort int    `env:"CASSANDRA_PORT" env-default:"9042"`
	CassandraDB   string `env:"CASSANDRA_DB"   env-default:"distcomp"`
}

func New() (*Config, error) {
	var cfg Config
	if err := cleanenv.ReadConfig("./.env", &cfg); err != nil {
		return nil, err
	}
	return &cfg, nil
}

func (c *Config) Validate() error {
	if c.HTTPPort == "" {
		return errors.New("DISCUSSION_PORT is required")
	}
	return nil
}
