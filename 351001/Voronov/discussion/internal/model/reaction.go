package model

type Reaction struct {
	ID      int64  `json:"id"`
	IssueID int64  `json:"issueId"`
	Content string `json:"content"`
}
