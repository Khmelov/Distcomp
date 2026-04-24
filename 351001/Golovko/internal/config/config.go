package config

import (
	"log"
	"os"

	"github.com/joho/godotenv"
)

type Config struct {
	Host string
	Port string
	Env  string
}

func Load() *Config {
	if err := godotenv.Load(); err != nil {
		log.Println("No .env file found, reading configuration from environment")
	}

	return &Config{
		Host: getEnv("HTTP_HOST", "localhost"),
		Port: getEnv("HTTP_PORT", "24110"),
		Env:  getEnv("ENV", "local"),
	}
}

func getEnv(key, fallback string) string {
	if value, exists := os.LookupEnv(key); exists {
		return value
	}
	return fallback
}