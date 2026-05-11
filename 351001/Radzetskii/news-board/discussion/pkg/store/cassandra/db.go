package cassandra

import (
	"fmt"
	"log"

	"github.com/gocql/gocql"
)

type Config struct {
	Host     string
	Port     int
	Keyspace string
}

func NewSession(cfg *Config) (*gocql.Session, error) {
	cluster := gocql.NewCluster(cfg.Host)
	cluster.Port = cfg.Port
	cluster.Consistency = gocql.Quorum

	systemSession, err := cluster.CreateSession()
	if err != nil {
		return nil, fmt.Errorf("failed to connect to cassandra: %w", err)
	}
	defer systemSession.Close()

	if err := systemSession.Query(
		fmt.Sprintf("CREATE KEYSPACE IF NOT EXISTS %s WITH replication = {'class':'SimpleStrategy','replication_factor':1}", cfg.Keyspace),
	).Exec(); err != nil {
		return nil, fmt.Errorf("failed to ensure keyspace: %w", err)
	}

	cluster.Keyspace = cfg.Keyspace
	session, err := cluster.CreateSession()
	if err != nil {
		return nil, fmt.Errorf("failed to connect to keyspace: %w", err)
	}

	if err := session.Query(`CREATE TABLE IF NOT EXISTS tbl_notice (
		news_id bigint,
		id bigint,
		content text,
		PRIMARY KEY (news_id, id)
	)`).Exec(); err != nil {
		session.Close()
		return nil, fmt.Errorf("failed to ensure tbl_notice table: %w", err)
	}

	log.Println("Connected to Cassandra and ensured schema")
	return session, nil
}
