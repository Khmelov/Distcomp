package main

import (
	"context"
	"log"
	"os/signal"
	"syscall"

	"discussion/internal/app"

	"go.uber.org/zap"
)

func main() {
	ctx, stop := signal.NotifyContext(context.Background(), syscall.SIGINT, syscall.SIGTERM)
	defer stop()

	logger, err := zap.NewProduction()
	if err != nil {
		log.Fatal(err)
	}
	defer logger.Sync()

	if err := app.Run(ctx, logger); err != nil {
		logger.Fatal("discussion failed", zap.Error(err))
	}
}
