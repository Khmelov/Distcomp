package main

import (
	"labs/discussion/internal/app"
	"labs/discussion/internal/config"
	"log"
)

func main() {
	cfg, err := config.Load()
	if err != nil {
		log.Fatalf("Failed to load configuration: %v", err)
	}

	application := app.New(cfg)

	if err := application.Run(); err != nil {
		log.Fatalf("App run error: %v", err)
	}
}
