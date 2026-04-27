package config

import (
	"log"
	"os"

	"github.com/joho/godotenv"
)

type Config struct {
	DBHost            string
	DBPort            string
	DBUser            string
	DBPassword        string
	DBName            string
	DBSchema          string
	DBSSLMode         string
	DiscussionBaseURL string
}

func Load(path string) *Config {
	err := loadEnv(path)
	if err != nil {
		log.Println("Warning: .env file not found, using environment variables")
	}

	return &Config{
		DBHost:            getEnv("DB_HOST", "localhost"),
		DBPort:            getEnv("DB_PORT", "5432"),
		DBUser:            getEnv("DB_USER", "postgres"),
		DBPassword:        getEnv("DB_PASSWORD", "postgres"),
		DBName:            getEnv("DB_NAME", "postgres"),
		DBSchema:          getEnv("DB_SCHEMA", "distcomp"),
		DBSSLMode:         getEnv("DB_SSLMODE", "disable"),
		DiscussionBaseURL: getEnv("DISCUSSION_BASE_URL", "http://localhost:24130"),
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
