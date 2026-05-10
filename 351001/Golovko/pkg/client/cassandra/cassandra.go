package cassandra

import (
	"time"

	"github.com/gocql/gocql"
)

type Config struct {
	Host     string
	Keyspace string
}

func NewClient(cfg Config) (*gocql.Session, error) {
	cluster := gocql.NewCluster(cfg.Host)
	cluster.Keyspace = cfg.Keyspace
	cluster.Timeout = 10 * time.Second
	cluster.ConnectTimeout = 10 * time.Second

	var session *gocql.Session
	var err error

	maxRetries := 10
	for i := 0; i < maxRetries; i++ {
		session, err = cluster.CreateSession()
		if err == nil {
			return session, nil
		}
		time.Sleep(5 * time.Second)
	}

	return nil, err
}