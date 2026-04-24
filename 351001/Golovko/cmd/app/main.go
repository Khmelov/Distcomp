package main

import (
	"log"

	"distcomp/internal/app"
)

// @title           DistComp API
// @version         1.0
// @description     REST API for Editor, Article, Tag, and Comment management.
// @host            localhost:24110
// @BasePath        /api/v1.0
func main() {
	if err := app.Run(); err != nil {
		log.Fatalf("Failed to run app: %v", err)
	}
}