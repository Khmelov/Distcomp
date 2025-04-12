package main

import (
	"context"
	"log"
	"os/signal"
	"syscall"

	"github.com/Khmelov/Distcomp/251003/truhan/lab2/internal/app"
	"github.com/spf13/viper"
)

func init() {
	viper.AddConfigPath("config")
	viper.SetConfigName("config")

	viper.SetConfigType("yaml")
	if err := viper.ReadInConfig(); err != nil {
		log.Fatal(err)
	}
}

func main() {
	ctx, cancel := signal.NotifyContext(context.Background(), syscall.SIGINT, syscall.SIGTERM)
	defer cancel()

	application, err := app.New()
	if err != nil {
		log.Fatal(err)
	}

	if err := application.Start(ctx); err != nil {
		log.Fatal(err)
	}
}
