package http

import (
	"context"
	"fmt"
	"log"
	"net/http"

	"github.com/Khmelov/Distcomp/251003/truhan/lab3/publisher/internal/handler"
	"github.com/Khmelov/Distcomp/251003/truhan/lab3/publisher/internal/service"

	"time"
)

type HttpServer interface {
	Serve(ctx context.Context) error
}

type server struct {
	srv *http.Server
}

func New(srv service.Service) HttpServer {
	return &server{
		srv: &http.Server{
			Addr:    fmt.Sprintf(":%s", "24110"),
			Handler: handler.New(srv).HTTP,
		},
	}
}

func (s server) Serve(ctx context.Context) error {
	errCh := make(chan error)

	go func() {
		errCh <- s.srv.ListenAndServe()
	}()

	log.Printf("server started on port: %v", s.srv.Addr)

	select {
	case err := <-errCh:
		return fmt.Errorf("server error: %v", err)

	case <-ctx.Done():
		ctx, cancel := context.WithTimeout(context.Background(), time.Second*10)
		defer cancel()

		if err := s.srv.Shutdown(ctx); err != nil {
			log.Printf("server stopped with error: %v", err)
		}
	}

	return nil
}
