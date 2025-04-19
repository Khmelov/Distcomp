package model

import (
	"time"
)

type Writer struct {
	ID        int64  `db:"id"        json:"id"`
	Login     string `db:"login"     json:"login"`
	Password  string `db:"password"  json:"-"`
	FirstName string `db:"firstname" json:"firstname"`
	LastName  string `db:"lastname"  json:"lastname"`
}

type News struct {
	ID       int64     `db:"id"        json:"id"`
	WriterID int64     `db:"writer_id" json:"writerId"`
	Title    string    `db:"title"     json:"title"`
	Content  string    `db:"content"   json:"content"`
	Labels   []string  `               json:"labels"`
	Created  time.Time `db:"created"   json:"created"`
	Modified time.Time `db:"modified"  json:"modified"`
}

type Notice struct {
	ID      int64  `db:"id"      json:"id,omitempty"      redis:"id"`
	NewsID  int64  `db:"news_id" json:"newsId,omitempty"  redis:"news_id"`
	Content string `db:"content" json:"content,omitempty" redis:"content"`
}

type Label struct {
	ID   int64  `db:"id"   json:"id"`
	Name string `db:"name" json:"name"`
}
