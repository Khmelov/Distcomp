package entity

import "time"

type Author struct {
	ID        int64
	Login     string
	Password  string
	Firstname string
	Lastname  string
	Role      string
}

type Marker struct {
	ID   int64
	Name string
}

type Topic struct {
	ID        int64
	AuthorID  int64
	Title     string
	Content   string
	MarkerIDs []int64
	Created   time.Time
	Modified  time.Time
}

type Comment struct {
	ID      int64
	TopicID int64
	Country string
	Content string
	State   string
}
