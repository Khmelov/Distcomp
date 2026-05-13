package config

type CassandraConfig struct {
	Hosts    []string `envconfig:"CASSANDRA_HOSTS" default:"127.0.0.1:9042"`
	Keyspace string   `envconfig:"CASSANDRA_KEYSPACE" default:"distcomp"`
}
