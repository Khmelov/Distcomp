package model

import "time"

type Issue struct {
	ID       int64     `db:"id" json:"id"`
	AuthorID int64     `db:"author_id" json:"authorId"`
	Title    string    `db:"title" json:"title"`
	Markers  []string  `db:"name" json:"markers"`
	Content  string    `db:"content" json:"content"`
	Created  time.Time `db:"created" json:"created"`
	Modified time.Time `db:"modified" json:"modified"`
}
