package main

import (
	"distcomp/internal/controller"
	"distcomp/internal/repository"
	"distcomp/internal/router"
	"distcomp/internal/service"
	"distcomp/pkg/database"
)

func main() {
	db := database.InitDB()

	repo := repository.NewRepository(db)
	svc := service.NewService(repo)
	handler := controller.NewHandler(svc)

	r := router.SetupRouter(handler)

	r.Run(":24110")
}
