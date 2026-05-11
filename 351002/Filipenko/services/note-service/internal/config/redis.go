package config

type RedisConfig struct {
	Host     string `envconfig:"REDIS_HOST" default:"127.0.0.1:6379"`
	Password string `envconfig:"REDIS_PASSWORD" default:""`
	DB       int    `envconfig:"REDIS_DB" default:"0"`
}
