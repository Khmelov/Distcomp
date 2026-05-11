package model

import "time"

type Creator struct {
	ID        int64  `db:"id"`
	Login     string `db:"login"`
	Password  string `db:"password"`
	FirstName string `db:"firstname"`
	LastName  string `db:"lastname"`
}

type Issue struct {
	ID        int64      `db:"id"`
	CreatorID int64      `db:"creator_id"`
	Title     string     `db:"title"`
	Content   string     `db:"content"`
	Created   time.Time  `db:"created"`
	Modified  *time.Time `db:"modified"`
	Marks     []string
}

type Message struct {
	ID      int64  `db:"id"`
	IssueID int64  `db:"issueid"`
	Content string `db:"content"`
}

type Mark struct {
	ID   int64  `db:"id"`
	Name string `db:"name"`
}
