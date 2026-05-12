package config

type PGConfig struct {
	URL string `envconfig:"PG_URL" default:"postgres://postgres:postgres@localhost:5432/distcomp?sslmode=disable&search_path=public"`
}
