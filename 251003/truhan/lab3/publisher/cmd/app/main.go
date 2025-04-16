package main

import (
	"context"
	"log"

	"github.com/Khmelov/Distcomp/251003/truhan/lab3/publisher/internal/app"
	srv "github.com/Khmelov/Distcomp/251003/truhan/lab3/publisher/internal/service"
	db "github.com/Khmelov/Distcomp/251003/truhan/lab3/publisher/internal/storage"

	"github.com/joho/godotenv"
)

func init() {
	if err := godotenv.Load(); err != nil {
		log.Fatalf("couldn't read .env file: %v", err)
	}
}

func main() {
	db, err := db.New()
	if err != nil {
		log.Fatalf("couldn't connect to db, err: %v", err)
	}
	defer db.DB.Close()

	srv := srv.New(*db)

	app := app.New()

	if err := app.Start(context.Background(), srv); err != nil {
		log.Fatalf("couldn't start server")
	}
}
