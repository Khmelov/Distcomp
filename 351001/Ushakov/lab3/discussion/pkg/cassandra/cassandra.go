package cassandra

import (
	"fmt"
	"time"

	"github.com/gocql/gocql"
)

const (
	defaultRetryCount = 3

	defaultConnectTimeout = 5 * time.Second
)

type Config struct {
	Addrs    []string
	Keyspace string
	User     string
	Password string
}

func Connect(cfg Config) (*gocql.Session, error) {
	cluster := gocql.NewCluster(cfg.Addrs...)
	cluster.RetryPolicy = &gocql.SimpleRetryPolicy{NumRetries: defaultRetryCount}
	cluster.Consistency = gocql.Quorum
	cluster.Keyspace = cfg.Keyspace
	cluster.ConnectTimeout = defaultConnectTimeout
	cluster.ProtoVersion = 4
	cluster.Authenticator = gocql.PasswordAuthenticator{
		Username: cfg.User,
		Password: cfg.Password,
	}

	session, err := cluster.CreateSession()
	if err != nil {
		return nil, fmt.Errorf("connect cassandra: %w", err)
	}

	return session, nil
}
