package message

import (
	"sync/atomic"

	"github.com/gocql/gocql"
)

type MessageRepo struct {
	session *gocql.Session

	id atomic.Int64
}

func New(session *gocql.Session) *MessageRepo {
	return &MessageRepo{
		session: session,
	}
}

func (n *MessageRepo) nextID() int64 {
	return n.id.Add(1)
}
