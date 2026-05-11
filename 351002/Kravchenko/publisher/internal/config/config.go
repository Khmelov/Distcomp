package config

import (
	"fmt"
	"os"
	"strings"

	"github.com/joho/godotenv"
)

type Config struct {
	ServerPort        string
	DBHost            string
	DBPort            string
	DBUser            string
	DBPassword        string
	DBName            string
	JWTSecret         string
	DiscussionBaseURL string
	Brokers           []string
	RedisHost         string
	RedisPort         string
	RedisDB           string
	CacheTTL          string
}

func Load() (*Config, error) {
	_ = godotenv.Load(".env.example")

	return &Config{
		ServerPort:        getEnv("SERVER_PORT", "24110"),
		DBHost:            getEnv("DB_HOST", "localhost"),
		DBPort:            getEnv("DB_PORT", "5432"),
		DBUser:            getEnv("DB_USER", "postgres"),
		DBPassword:        getEnv("DB_PASSWORD", "postgres"),
		DBName:            getEnv("DB_NAME", "distcomp"),
		JWTSecret:         getEnv("JWT_SECRET", "your-secret-key"),
		Brokers:           strings.Split(getEnv("BROKERS", "localhost"), ","),
		DiscussionBaseURL: getEnv("DISCUSSION_BASE_URL", "http://localhost:24130"),
		RedisHost:         getEnv("REDIS_HOST", "localhost"),
		RedisPort:         getEnv("REDIS_PORT", "6379"),
		RedisDB:           getEnv("REDIS_DB", ""),
		CacheTTL:          getEnv("CACHE_TTL", "3600"),
	}, nil
}

func getEnv(key, defaultValue string) string {
	if value, exists := os.LookupEnv(key); exists {
		return value
	}
	return defaultValue
}

func (c *Config) DSN() string {
	return fmt.Sprintf("postgres://%s:%s@%s:%s/%s?sslmode=disable",
		c.DBUser, c.DBPassword, c.DBHost, c.DBPort, c.DBName)
}
