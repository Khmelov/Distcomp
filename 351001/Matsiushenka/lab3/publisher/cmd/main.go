package main

import (
	"publisher/internal/controller"
	"publisher/internal/repository"
	"publisher/internal/router"
	"publisher/pkg/database"
)

func main() {
	db := database.InitDB()
	repo := &repository.Repository{DB: db}
	h := &controller.Handler{Repo: repo}

	r := router.SetupRouter(h)
	r.Run(":24110")
}
