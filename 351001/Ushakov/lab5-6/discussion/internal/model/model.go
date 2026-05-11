package model

type Message struct {
	ID      int64  `db:"id"      json:"id"`
	IssueID int64  `db:"issue_id" json:"tweetId"`
	Content string `db:"content" json:"content"`
}
