package config

import (
	"log"
	"os"

	"github.com/joho/godotenv"
)

type Config struct {
	AppPort           string
	DBHost            string
	DBPort            string
	DBUser            string
	DBPassword        string
	DBName            string
	DBSchema          string
	DBSSLMode         string
	DiscussionBaseURL string
	KafkaBrokers      string
	KafkaGroup        string
	RedisAddr         string
	JWTSecret         string
}

func Load(path string) *Config {
	err := loadEnv(path)
	if err != nil {
		log.Println("Warning: .env file not found, using environment variables")
	}

	return &Config{
		AppPort:           getEnv("APP_PORT", "24110"),
		DBHost:            getEnv("DB_HOST", "localhost"),
		DBPort:            getEnv("DB_PORT", "5432"),
		DBUser:            getEnv("DB_USER", "postgres"),
		DBPassword:        getEnv("DB_PASSWORD", "postgres"),
		DBName:            getEnv("DB_NAME", "postgres"),
		DBSchema:          getEnv("DB_SCHEMA", "distcomp"),
		DBSSLMode:         getEnv("DB_SSLMODE", "disable"),
		DiscussionBaseURL: getEnv("DISCUSSION_BASE_URL", "http://localhost:24130"),
		KafkaBrokers:      getEnv("KAFKA_BROKERS", "localhost:9092"),
		KafkaGroup:        getEnv("KAFKA_GROUP", "publisher-group"),
		RedisAddr:         getEnv("REDIS_ADDR", "localhost:6379"),
		JWTSecret:         getEnv("JWT_SECRET", "secret-key"),
	}
}

func loadEnv(path string) error {
	candidates := []string{
		path,
		"../infra/env/.env",
		"infra/env/.env",
	}

	for _, candidate := range candidates {
		if err := godotenv.Load(candidate); err == nil {
			return nil
		}
	}

	return os.ErrNotExist
}

func getEnv(key, fallback string) string {
	if val := os.Getenv(key); val != "" {
		return val
	}
	return fallback
}
