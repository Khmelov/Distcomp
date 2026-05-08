package main

import (
	"context"
	"log"
	"os/signal"
	"syscall"

	"publisher/internal/app"
	"publisher/pkg/logger"

	"go.uber.org/zap"
)

func main() {
	ctx, stop := signal.NotifyContext(context.Background(), syscall.SIGINT, syscall.SIGTERM)
	defer stop()

	l, err := logger.New()
	if err != nil {
		log.Fatal(err)
	}
	defer l.Sync()

	if err := app.Run(ctx, l); err != nil {
		l.Fatal("publisher failed", zap.Error(err))
	}
}
