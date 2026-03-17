package domain

// Note представляет заметку
type Note struct {
	BaseEntity
	Content string `json:"content"`
	IssueID int64  `json:"issueId"`
}

