package model

type Comment struct {
	ID      int64  `db:"id" json:"id"`
	IssueID int64  `db:"issue_id" json:"issueId"`
	Content string `db:"content" json:"content"`
	State   string `db:"-" json:"state"`
}
