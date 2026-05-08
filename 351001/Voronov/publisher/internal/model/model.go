package model

import "time"

type User struct {
	ID        int64  `json:"id"`
	Login     string `json:"login"`
	Password  string `json:"password"`
	Firstname string `json:"firstname"`
	Lastname  string `json:"lastname"`
}

type Label struct {
	ID   int64  `json:"id"`
	Name string `json:"name"`
}

type IssueLabel struct {
	IssueID int64 `json:"issueId"`
	LabelID int64 `json:"labelId"`
}

type Issue struct {
	ID       int64     `json:"id"`
	UserID   int64     `json:"userId"`
	Title    string    `json:"title"`
	Content  string    `json:"content"`
	Created  time.Time `json:"created"`
	Modified time.Time `json:"modified"`
	Labels   []*Label  `json:"labels"`
	User     *User     `json:"user,omitempty"`
}

type Reaction struct {
	ID      int64  `json:"id"`
	IssueID int64  `json:"issueId"`
	Content string `json:"content"`
}
