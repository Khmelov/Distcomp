package models

type Comment struct {
	Country string `json:"Country"`
	IssueID uint   `json:"issueId"`
	ID      uint   `json:"id"`
	Content string `json:"content"`
}

type CommentData struct {
	IssueID int    `json:"issueId"`
	Content string `json:"content"`
	ID      int    `json:"id"`
}
