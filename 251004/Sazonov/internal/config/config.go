package config

import (
	"time"

	"github.com/spf13/viper"
)

type Config struct {
	Log     LogConfig
	Core    CoreConfig
	HTTP    HTTPServerConfig
	API     APIConfig
	Redis   RedisConfig
	Kafka   KafkaConfig
	Storage StorageConfig
}

type CoreConfig struct {
	ShutdownTimeout time.Duration
}

type StorageConfig struct {
	User     string
	Password string
	Host     string
	Port     string
	DBName   string
	SSLMode  string
}

type APIConfig struct {
	NoticeServiceAddr string
}

type RedisConfig struct {
	Addr     string
	User     string
	Password string
	DB       int
}

type LogConfig struct {
	Level    string
	Encoding string
}

type HTTPServerConfig struct {
	Host        string
	Port        string
	Timeout     time.Duration
	IdleTimeout time.Duration
}

type KafkaConfig struct {
	Brokers  []string
	Topic    string
	User     string
	Password string
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
		Redis: RedisConfig{
			Addr:     viper.GetString("redis.addr"),
			User:     viper.GetString("redis.user"),
			Password: viper.GetString("redis.password"),
			DB:       viper.GetInt("redis.db"),
		},
		Storage: StorageConfig{
			User:     viper.GetString("storage.user"),
			Password: viper.GetString("storage.password"),
			Host:     viper.GetString("storage.host"),
			Port:     viper.GetString("storage.port"),
			DBName:   viper.GetString("storage.dbname"),
			SSLMode:  viper.GetString("storage.sslmode"),
		},
		HTTP: HTTPServerConfig{
			Host:        viper.GetString("http.host"),
			Port:        viper.GetString("http.port"),
			Timeout:     viper.GetDuration("http.timeout"),
			IdleTimeout: viper.GetDuration("http.idle_timeout"),
		},
		API: APIConfig{
			NoticeServiceAddr: viper.GetString("notice_service_addr"),
		},
		Kafka: KafkaConfig{
			Brokers:  viper.GetStringSlice("kafka.brokers"),
			Topic:    viper.GetString("kafka.topic"),
			User:     viper.GetString("kafka.user"),
			Password: viper.GetString("kafka.password"),
		},
	}
}
