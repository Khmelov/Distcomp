package main

import (
	"context"
	"os"
	"os/signal"
	"syscall"

	"github.com/Khmelov/Distcomp/251004/Sazonov/configs"
	"github.com/Khmelov/Distcomp/251004/Sazonov/internal/app"
	"github.com/Khmelov/Distcomp/251004/Sazonov/internal/config"
	configutil "github.com/Khmelov/Distcomp/251004/Sazonov/pkg/config"
	"github.com/Khmelov/Distcomp/251004/Sazonov/pkg/logger"
	"github.com/spf13/pflag"
	"go.uber.org/zap"
)

const envPrefix = "API"

func main() {
	var configPath string

	cmd := pflag.NewFlagSet("config", pflag.ContinueOnError)
	cmd.StringVarP(&configPath, "config", "c", "", "specify path to config file")

	if err := cmd.Parse(os.Args[1:]); err != nil {
		panic(err)
	}

	err := configutil.LoadViper(
		configPath,
		configs.DefaultConfig,
		configutil.WithConfigType("yaml"),
		configutil.WithEnvs(envPrefix),
	)
	if err != nil {
		panic(err)
	}

	cfg := config.Load()

	err = logger.Configure(
		logger.WithLevel(cfg.Log.Level),
		logger.WithEncoding(cfg.Log.Encoding),
	)
	if err != nil {
		panic(err)
	}

	app, err := app.New(cfg)
	if err != nil {
		panic(err)
	}

	ctx, stop := signal.NotifyContext(
		context.Background(),
		syscall.SIGINT,
		syscall.SIGTERM,
		syscall.SIGQUIT,
	)

	go func() {
		defer stop()

		if err := app.Run(context.Background()); err != nil {
			logger.Error(context.Background(), "failure while running application", zap.Error(err))
		}
	}()

	<-ctx.Done()

	ctx, cancel := context.WithTimeout(context.Background(), cfg.Core.ShutdownTimeout)
	defer cancel()

	if err := app.Shutdown(ctx); err != nil {
		logger.Error(context.Background(), "failure during shutdown", zap.Error(err))
	}
}
