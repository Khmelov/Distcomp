package appbase

import (
	"context"

	"golang.org/x/sync/errgroup"
)

type Service interface {
	Run(ctx context.Context) error
	Shutdown(ctx context.Context) error
}

type CleanupFunc func()

type App struct {
	services []Service
	cleanups []CleanupFunc

	cancel context.CancelFunc
}

func New(services []Service, cleanups []CleanupFunc) *App {
	return &App{
		services: services,
		cleanups: cleanups,
	}
}

func (a *App) Run(ctx context.Context) error {
	ctx, a.cancel = context.WithCancel(ctx)

	wg, ctx := errgroup.WithContext(ctx)
	for _, svc := range a.services {
		wg.Go(func() error {
			return svc.Run(ctx)
		})
	}

	return wg.Wait()
}

func (a *App) Shutdown(ctx context.Context) error {
	if a.cancel != nil {
		a.cancel()
	}

	wg, ctx := errgroup.WithContext(ctx)
	for _, svc := range a.services {
		wg.Go(func() error {
			return svc.Shutdown(ctx)
		})
	}
	defer a.cleanup()

	return wg.Wait()
}

func (a *App) cleanup() {
	for _, cleanup := range a.cleanups {
		cleanup()
	}
}
