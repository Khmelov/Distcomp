package repository

import (
	"discussion/internal/domain"

	"github.com/gocql/gocql"
)

type NoteRepository struct {
	session *gocql.Session
}

func NewNoteRepository() *NoteRepository {
	cluster := gocql.NewCluster("localhost")
	cluster.Keyspace = "distcomp"
	cluster.Consistency = gocql.Quorum

	setupSession := gocql.NewCluster("localhost")
	s, _ := setupSession.CreateSession()
	s.Query("CREATE KEYSPACE IF NOT EXISTS distcomp WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1}").Exec()
	s.Query("CREATE TABLE IF NOT EXISTS distcomp.tbl_note (id int PRIMARY KEY, topic_id int, content text)").Exec()
	s.Close()

	session, err := cluster.CreateSession()
	if err != nil {
		panic(err)
	}
	return &NoteRepository{session: session}
}

func (r *NoteRepository) Create(n domain.Note) error {
	return r.session.Query("INSERT INTO tbl_note (id, topic_id, content) VALUES (?, ?, ?)",
		n.ID, n.TopicID, n.Content).Exec()
}

func (r *NoteRepository) GetByID(id int) (domain.Note, error) {
	var n domain.Note
	err := r.session.Query("SELECT id, topic_id, content FROM tbl_note WHERE id = ?", id).Scan(&n.ID, &n.TopicID, &n.Content)
	return n, err
}

func (r *NoteRepository) GetAll() ([]domain.Note, error) {
	var notes []domain.Note
	iter := r.session.Query("SELECT id, topic_id, content FROM tbl_note").Iter()
	var n domain.Note
	for iter.Scan(&n.ID, &n.TopicID, &n.Content) {
		notes = append(notes, n)
	}
	return notes, iter.Close()
}

func (r *NoteRepository) Update(n domain.Note) error {
	return r.session.Query("UPDATE tbl_note SET content = ? WHERE id = ?", n.Content, n.ID).Exec()
}

func (r *NoteRepository) Delete(id int) error {
	return r.session.Query("DELETE FROM tbl_note WHERE id = ?", id).Exec()
}
