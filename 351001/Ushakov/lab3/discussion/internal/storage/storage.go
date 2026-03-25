package storage

import (
	"fmt"

	"github.com/Khmelov/Distcomp/351001/Ushakov/lab3/discussion/internal/storage/cassandra/post"
	"github.com/Khmelov/Distcomp/351001/Ushakov/lab3/discussion/pkg/cassandra"
	"github.com/gocql/gocql"
)

type repository struct {
	session *gocql.Session

	post.PostRepo
}

func New(cfg cassandra.Config) (Repository, error) {
	session, err := cassandra.Connect(cfg)
	if err != nil {
		return nil, fmt.Errorf("new repository: %w", err)
	}

	repo := &repository{
		session: session,

		PostRepo: *post.New(session),
	}

	return repo, nil
}

func (r repository) Close() {
	r.session.Close()
}
