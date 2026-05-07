package response

import "time"

type IssueResponseTo struct {
	ID       int64             `json:"id"`
	UserID   int64             `json:"userId"`
	User     UserResponseTo    `json:"user"`
	Title    string            `json:"title"`
	Content  string            `json:"content"`
	Created  time.Time         `json:"created"`
	Modified time.Time         `json:"modified"`
	Labels   []LabelResponseTo `json:"labels"`
}
