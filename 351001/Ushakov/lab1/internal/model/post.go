package model

import (
	"errors"
	"strings"
)

var (
	ErrInvalidPostContent = errors.New("content must be between 2 and 2048 characters")
)

type Post struct {
	ID      int64  `json:"id" db:"id"`
	IssueID int64  `json:"issueId" db:"issue_id"`
	Content string `json:"content" db:"content"`
}

func (m *Post) Validate() error {
	if len(strings.TrimSpace(m.Content)) < 2 || len(m.Content) > 2048 {
		return ErrInvalidPostContent
	}

	return nil
}
