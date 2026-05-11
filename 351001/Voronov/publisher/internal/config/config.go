package config

import (
	"errors"

	"github.com/ilyakaznacheev/cleanenv"
)

type Config struct {
	HTTPport      string `env:"HTTPPORT"`
	PostgresHost  string `env:"POSTGRES_HOST"`
	PostgresPort  uint16 `env:"POSTGRES_PORT"`
	PostgresUser  string `env:"POSTGRES_USER"`
	PostgresPass  string `env:"POSTGRES_PASS"`
	PostgresDB    string `env:"POSTGRES_DB"`
	GooseDBString string `env:"GOOSE_DBSTRING"`
	DiscussionURL string `env:"DISCUSSION_URL"`
	RedisAddr     string `env:"REDIS_ADDR"         env-default:"localhost:6379"`
	KafkaBroker   string `env:"KAFKA_BROKER"       env-default:"localhost:9092"`
	KafkaInTopic  string `env:"KAFKA_IN_TOPIC"     env-default:"reaction-in"`
	KafkaOutTopic string `env:"KAFKA_OUT_TOPIC"    env-default:"reaction-out"`
	KafkaGroupID  string `env:"KAFKA_GROUP_ID"     env-default:"publisher-group"`
	JWTSecret     string `env:"JWT_SECRET"         env-default:"distcomp-jwt-secret-key"`
}

func New() (*Config, error) {
	var cfg Config
	if err := cleanenv.ReadConfig("./.env", &cfg); err != nil {
		return nil, err
	}
	return &cfg, nil
}

func (c *Config) Validate() error {
	if c.HTTPport == "" {
		return errors.New("HTTPPORT is required")
	}
	if c.PostgresHost == "" {
		return errors.New("POSTGRES_HOST is required")
	}
	if c.GooseDBString == "" {
		return errors.New("GOOSE_DBSTRING is required")
	}
	return nil
}
