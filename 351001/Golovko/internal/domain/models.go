package domain

import "time"

type Editor struct {
	ID        int64
	Login     string
	Password  string
	FirstName string
	LastName  string
	Role      string
}

type Article struct {
	ID       int64
	EditorID int64
	Title    string
	Content  string
	Created  time.Time
	Modified time.Time
	Tags     []Tag
}

type Tag struct {
	ID   int64
	Name string
}

type Comment struct {
	ID        int64
	ArticleID int64
	EditorID  int64
	Content   string
	State     string
}
