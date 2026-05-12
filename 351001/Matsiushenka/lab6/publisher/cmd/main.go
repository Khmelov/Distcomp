package main

import (
	"publisher/internal/controller"
	"publisher/internal/repository"
	"publisher/internal/router"
	"publisher/internal/service"
	"publisher/pkg/database"
)

func main() {
	db := database.InitDB()
	service.InitRedis()
	repo := &repository.Repository{DB: db}
	h := &controller.Handler{Repo: repo}
	r := router.SetupRouter(h)
	r.Run(":24110")
}
