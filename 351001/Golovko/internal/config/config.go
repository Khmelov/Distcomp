package config

import (
	"log"
	"os"

	"github.com/joho/godotenv"
)

type Config struct {
	Host      string
	Port      string
	Env       string
	DBHost    string
	DBPort    string
	DBUser    string
	DBPass    string
	DBName    string
	DBSchema  string
}

func Load() *Config {
	if err := godotenv.Load(); err != nil {
		log.Println("No .env file found, reading configuration from environment")
	}

	return &Config{
		Host:     getEnv("HTTP_HOST", "0.0.0.0"),
		Port:     getEnv("HTTP_PORT", "24110"),
		Env:      getEnv("ENV", "local"),
		DBHost:   getEnv("DB_HOST", "localhost"),
		DBPort:   getEnv("DB_PORT", "5432"),
		DBUser:   getEnv("DB_USER", "postgres"),
		DBPass:   getEnv("DB_PASSWORD", "postgres"),
		DBName:   getEnv("DB_NAME", "postgres"),
		DBSchema: getEnv("DB_SCHEMA", "distcomp"),
	}
}

func getEnv(key, fallback string) string {
	if value, exists := os.LookupEnv(key); exists {
		return value
	}
	return fallback
}