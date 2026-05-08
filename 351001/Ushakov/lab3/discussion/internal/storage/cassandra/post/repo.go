package post

import (
	"sync/atomic"

	"github.com/gocql/gocql"
)

type github.com/Khmelov/Distcomp/351001/UshakovRepo struct {
session *gocql.Session

id atomic.Int64
}

func New(session *gocql.Session) *github.com/Khmelov/Distcomp/351001/UshakovRepo {
return &github.com/Khmelov/Distcomp/351001/UshakovRepo{
session: session,
}
}

func (n *github.com /Khmelov/Distcomp/351001/UshakovRepo) nextID() int64 {
	return n.id.Add(1)
}
