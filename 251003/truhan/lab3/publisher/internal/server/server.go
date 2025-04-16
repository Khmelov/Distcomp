package server

import (
	"context"
	"log"
	"sync"

	"github.com/Khmelov/Distcomp/251003/truhan/lab3/publisher/internal/server/http"
	"github.com/Khmelov/Distcomp/251003/truhan/lab3/publisher/internal/service"

	"golang.org/x/sync/errgroup"
)

type Server interface {
	Serve(ctx context.Context) error
}

type server struct {
	servers []Server
}

func New(srv service.Service) Server {
	result := &server{
		servers: []Server{
			http.New(srv),
		},
	}

	return result
}

func (s server) Serve(ctx context.Context) error {
	gr, grCtx := errgroup.WithContext(ctx)

	gr.Go(func() error {
		return s.serve(grCtx)
	})

	var err error

	if err = gr.Wait(); err != nil {
		log.Fatalf("server error: %v", err)
	}

	log.Println("app: shutting down the server...")

	return nil
}

func (s *server) serve(ctx context.Context) error {
	var wg sync.WaitGroup
	wg.Add(len(s.servers))

	gr, grCtx := errgroup.WithContext(ctx)

	for _, s := range s.servers {
		s := s

		gr.Go(func() error {
			defer wg.Done()

			return s.Serve(grCtx)
		})
	}

	wg.Wait()

	return gr.Wait()
}
