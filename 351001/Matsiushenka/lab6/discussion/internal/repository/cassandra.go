package repository

import (
	"discussion/internal/domain"
	"fmt"
	"log"
	"time"

	"github.com/gocql/gocql"
)

type NoteRepository struct {
	session *gocql.Session
}

func NewNoteRepository() *NoteRepository {
	cluster := gocql.NewCluster("localhost")
	cluster.Consistency = gocql.Quorum
	cluster.Timeout = 15 * time.Second // Увеличили таймаут

	var session *gocql.Session
	var err error

	log.Println(">>> Starting Cassandra connection loop...")

	// Пытаемся подключиться 30 раз по 5 секунд (2.5 минуты ожидания)
	for i := 0; i < 30; i++ {
		session, err = cluster.CreateSession()
		if err == nil && session != nil {
			log.Println(">>> Cassandra connection SUCCESS!")
			break
		}

		if session != nil {
			session.Close()
		}

		log.Printf(">>> Cassandra NOT READY (Attempt %d/30). Waiting 5s...", i+1)
		time.Sleep(5 * time.Second)
	}

	if err != nil || session == nil {
		log.Fatal(">>> FATAL: Could not connect to Cassandra after 30 attempts.")
	}

	// Инициализация схемы
	log.Println(">>> Initializing distcomp keyspace...")
	_ = session.Query("CREATE KEYSPACE IF NOT EXISTS distcomp WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1}").Exec()

	log.Println(">>> Initializing tbl_note table...")
	err = session.Query("CREATE TABLE IF NOT EXISTS distcomp.tbl_note (id int PRIMARY KEY, topic_id int, content text, state text)").Exec()
	if err != nil {
		log.Printf(">>> Table init warning: %v", err)
	}

	return &NoteRepository{session: session}
}

func (r *NoteRepository) Create(n domain.Note) error {
	if r.session == nil {
		return fmt.Errorf("no session")
	}
	return r.session.Query("INSERT INTO distcomp.tbl_note (id, topic_id, content, state) VALUES (?, ?, ?, ?)",
		n.ID, n.TopicID, n.Content, n.State).Exec()
}

func (r *NoteRepository) GetByID(id int) (domain.Note, error) {
	var n domain.Note
	if r.session == nil {
		return n, fmt.Errorf("no session")
	}
	err := r.session.Query("SELECT id, topic_id, content, state FROM distcomp.tbl_note WHERE id = ?", id).
		Scan(&n.ID, &n.TopicID, &n.Content, &n.State)
	return n, err
}

func (r *NoteRepository) GetAll() ([]domain.Note, error) {
	var notes []domain.Note
	if r.session == nil {
		return nil, fmt.Errorf("no session")
	}
	iter := r.session.Query("SELECT id, topic_id, content, state FROM distcomp.tbl_note").Iter()
	var n domain.Note
	for iter.Scan(&n.ID, &n.TopicID, &n.Content, &n.State) {
		notes = append(notes, n)
	}
	_ = iter.Close()
	return notes, nil
}

func (r *NoteRepository) Update(n domain.Note) error {
	if r.session == nil {
		return fmt.Errorf("no session")
	}
	if n.State == "" {
		n.State = "APPROVE"
	}
	return r.session.Query("UPDATE distcomp.tbl_note SET content = ?, state = ? WHERE id = ?",
		n.Content, n.State, n.ID).Exec()
}

func (r *NoteRepository) Delete(id int) error {
	if r.session == nil {
		return fmt.Errorf("no session")
	}
	return r.session.Query("DELETE FROM distcomp.tbl_note WHERE id = ?", id).Exec()
}
