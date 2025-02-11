package http

import (
	"context"
	"errors"
	"fmt"
	"net/http"
	"time"

	"golang.org/x/net/http2"
)

type Config struct {
	Port        string
	Timeout     time.Duration
	IdleTimeout time.Duration
}

type httpServer struct {
	srv *http.Server
}

func New(cfg Config, handler http.Handler) *httpServer {
	server := &http.Server{
		Addr:         fmt.Sprintf(":%s", cfg.Port),
		Handler:      handler,
		ReadTimeout:  cfg.Timeout,
		WriteTimeout: cfg.Timeout,
		IdleTimeout:  cfg.IdleTimeout,
	}

	_ = http2.ConfigureServer(server, &http2.Server{})

	return &httpServer{
		srv: server,
	}
}

func (hs *httpServer) Run(ctx context.Context) error {
	if err := hs.srv.ListenAndServe(); err != nil && !errors.Is(err, http.ErrServerClosed) {
		return err
	}

	return nil
}

func (hs *httpServer) Shutdown(ctx context.Context) error {
	return hs.srv.Shutdown(ctx)
}
