package model

import "time"

type UserRole string

const (
	RoleAdmin    UserRole = "ADMIN"
	RoleCustomer UserRole = "CUSTOMER"
)

type User struct {
	ID        int64    `json:"id"`
	Login     string   `json:"login"`
	Password  string   `json:"password"`
	Firstname string   `json:"firstname"`
	Lastname  string   `json:"lastname"`
	Role      UserRole `json:"role"`
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

type ReactionState string

const (
	ReactionStatePending ReactionState = "PENDING"
	ReactionStateApprove ReactionState = "APPROVE"
	ReactionStateDecline ReactionState = "DECLINE"
)

type Reaction struct {
	ID      int64         `json:"id"`
	IssueID int64         `json:"issueId"`
	Content string        `json:"content"`
	State   ReactionState `json:"state"`
}
