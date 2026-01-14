package config

import (
	"log"
	"os"
	"strconv"
	"time"

	"github.com/gocql/gocql"
	"github.com/joho/godotenv"
)

var Session *gocql.Session

func Connect() {
	// Создаем новый кластерный объект

	err := godotenv.Load()
	if err != nil {
		log.Fatal("Error loading .env file", err)
	}

	// Connect to cluster
	cluster := setupCluster()

	// Create session
	Session = createSession(cluster)

}

func setupCluster() *gocql.ClusterConfig {
	cluster := gocql.NewCluster(os.Getenv("CLUSTER_HOST"))

	clusterPort, err := strconv.Atoi(os.Getenv("CLUSTER_PORT"))
	if err != nil {
		log.Fatal("Error with converting CLUSTER_PORT from .env to int")
	}

	cluster.Port = clusterPort

	cluster.Keyspace = os.Getenv("CLUSTER_KEYSPACE")
	connectTimeoutNum, err := strconv.Atoi(os.Getenv("CONNECT_TIMEOUT"))
	if err != nil {
		log.Fatal("Error with converting CONNECT_TIMEOUT from .env to int")
	}
	// Замените на ваш keyspace
	cluster.ConnectTimeout = time.Duration(connectTimeoutNum) * time.Second // Increase connection timeout

	clusterTimeoutNum, err := strconv.Atoi(os.Getenv("CLUSTER_TIMEOUT"))
	if err != nil {
		log.Fatal("Error with converting CLUSTER_TIMEOUT from .env to int")
	}
	cluster.Timeout = time.Duration(clusterTimeoutNum) * time.Second // Increase general timeout

	return cluster
}

func createSession(cluster *gocql.ClusterConfig) *gocql.Session {
	session, err := cluster.CreateSession()
	if err != nil {
		log.Fatal(err)
	}
	log.Println("Connceted")
	return session
}
