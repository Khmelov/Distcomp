package config

import (
	"fmt"
	"log"
	"os"

	"github.com/joho/godotenv"
)

type Config struct {
	Host         string
	Port         int
	Keyspace     string
	Address      string
	KafkaBrokers string
	KafkaGroup   string
}

func Load(path string) *Config {
	if err := loadEnv(path); err != nil {
		log.Println("Warning: discussion .env file not found, using environment variables")
	}

	return &Config{
		Host:         getEnv("CASSANDRA_HOST", "localhost"),
		Port:         getEnvInt("CASSANDRA_PORT", 9042),
		Keyspace:     getEnv("CASSANDRA_KEYSPACE", "distcomp"),
		Address:      getEnv("DISCUSSION_LISTEN_ADDR", ":24130"),
		KafkaBrokers: getEnv("KAFKA_BROKERS", "localhost:9092"),
		KafkaGroup:   getEnv("KAFKA_GROUP", "discussion-group"),
	}
}

func loadEnv(path string) error {
	candidates := []string{
		path,
		"infra/env/discussion.env",
		"../infra/env/discussion.env",
	}

	for _, candidate := range candidates {
		if err := godotenv.Load(candidate); err == nil {
			return nil
		}
	}

	return os.ErrNotExist
}

func getEnv(key, fallback string) string {
	if value := os.Getenv(key); value != "" {
		return value
	}
	return fallback
}

func getEnvInt(key string, fallback int) int {
	value := os.Getenv(key)
	if value == "" {
		return fallback
	}

	var parsed int
	if _, err := fmt.Sscanf(value, "%d", &parsed); err != nil {
		return fallback
	}
	return parsed
}
