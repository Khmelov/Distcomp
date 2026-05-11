package config

import (
	"errors"

	"github.com/ilyakaznacheev/cleanenv"
)

type Config struct {
	HTTPPort      string `env:"DISCUSSION_PORT"`
	CassandraHost string `env:"CASSANDRA_HOST"  env-default:"localhost"`
	CassandraPort int    `env:"CASSANDRA_PORT"  env-default:"9042"`
	CassandraDB   string `env:"CASSANDRA_DB"    env-default:"distcomp"`
	KafkaBroker   string `env:"KAFKA_BROKER"    env-default:"localhost:9092"`
	KafkaInTopic  string `env:"KAFKA_IN_TOPIC"  env-default:"reaction-in"`
	KafkaOutTopic string `env:"KAFKA_OUT_TOPIC" env-default:"reaction-out"`
	KafkaGroupID  string `env:"KAFKA_GROUP_ID"  env-default:"discussion-group"`
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
