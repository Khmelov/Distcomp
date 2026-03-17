package main

import (
	"log"

	"lab1/internal/app"
	"lab1/internal/config"
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
