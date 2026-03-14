package post

import (
	"sync/atomic"

	"github.com/gocql/gocql"
)

type PostRepo struct {
	session *gocql.Session

	id atomic.Int64
}

func New(session *gocql.Session) *PostRepo {
	return &PostRepo{
		session: session,
	}
}

func (n *PostRepo) nextID() int64 {
	return n.id.Add(1)
}
