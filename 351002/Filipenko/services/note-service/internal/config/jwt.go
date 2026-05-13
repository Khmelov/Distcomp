package config

type JWTConfig struct {
	Secret        string `envconfig:"JWT_SECRET" default:"distcomp-lab6-dev-secret-change-me-please"`
	ExpiryMinutes int    `envconfig:"JWT_EXPIRY_MINUTES" default:"120"`
}
