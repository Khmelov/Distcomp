package server

import (
	"context"
	"fmt"
	"log"
	"net/http"
)

type Server interface {
	Serve(ctx context.Context) error
}

type server struct {
	srv    *http.Server
	config Config
}

func New(handler http.Handler, config Config) Server {
	server := server{
		srv: &http.Server{
			Addr:        fmt.Sprintf(":%d", config.Port),
			Handler:     handler,
			ReadTimeout: config.ReadTime,
		},

		config: config,
	}

	return server
}

func (s server) Serve(ctx context.Context) error {
	errCh := make(chan error)

	go func() {
		errCh <- s.srv.ListenAndServe()
	}()

	log.Printf("server started on  http://localhost:%d\n", s.config.Port)

	select {
	case err := <-errCh:
		log.Printf("server stopped with error: %v\n", err.Error())

		return err

	case <-ctx.Done():
		log.Println("server shutting down...")

		ctx, cancel := context.WithTimeout(context.Background(), s.config.ShutdownTime)
		defer cancel()

		if err := s.srv.Shutdown(ctx); err != nil {
			log.Printf("server shutdown failed: %v", err.Error())

			return err
		}

		log.Println("server stopped normally")
	}

	return nil
}
