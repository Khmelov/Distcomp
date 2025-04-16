package storage

import (
	"fmt"

	"github.com/Khmelov/Distcomp/251003/truhan/lab3/discussion/internal/storage/cassandra/message"
	"github.com/Khmelov/Distcomp/251003/truhan/lab3/discussion/pkg/cassandra"
	"github.com/gocql/gocql"
)

type repository struct {
	session *gocql.Session

	message.MessageRepo
}

func New(cfg cassandra.Config) (Repository, error) {
	session, err := cassandra.Connect(cfg)
	if err != nil {
		return nil, fmt.Errorf("new repository: %w", err)
	}

	repo := &repository{
		session: session,

		MessageRepo: *message.New(session),
	}

	return repo, nil
}

func (r repository) Close() {
	r.session.Close()
}
