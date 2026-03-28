package config

import (
	"os"
	"strconv"
	"strings"

	"github.com/joho/godotenv"
)

type Config struct {
	ServerPort        int
	CassandraHosts    []string
	CassandraPort     int
	CassandraKeyspace string
	JWTSecret         string
}

func Load() (*Config, error) {
	_ = godotenv.Load(".env.example")

	hostsEnv := getEnv("CASSANDRA_HOSTS", "localhost")
	hosts := strings.Split(hostsEnv, ",")
	for i := range hosts {
		hosts[i] = strings.TrimSpace(hosts[i])
	}

	return &Config{
		ServerPort:        getEnvAsInt("SERVER_PORT", 24130),
		CassandraHosts:    hosts,
		CassandraPort:     getEnvAsInt("CASSANDRA_PORT", 9042),
		CassandraKeyspace: getEnv("CASSANDRA_KEYSPACE", "distcomp"),
		JWTSecret:         getEnv("JWT_SECRET", "your-secret-key"),
	}, nil
}

func getEnv(key, defaultValue string) string {
	if value, exists := os.LookupEnv(key); exists {
		return value
	}
	return defaultValue
}

func getEnvAsInt(key string, defaultValue int) int {
	valueStr := getEnv(key, "")
	value, err := strconv.Atoi(valueStr)
	if err != nil {
		return defaultValue
	}
	return value
}
