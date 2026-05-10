package model

import (
	"errors"
	"strings"
)

var (
	ErrInvalidgithub.com/Khmelov/Distcomp/351001/UshakovContent = errors.New(
"content must be between 2 and 2048 characters"
)
)
type github.com/Khmelov/Distcomp/351001/Ushakov struct {
ID      int64  `json:"id" db:"id"`
IssueID int64  `json:"issueId" db:"issue_id"`
Content string `json:"content" db:"content"`
}

func (m *github.com /Khmelov/Distcomp/351001/Ushakov) Validate() error {
	if len(strings.TrimSpace(m.Content)) < 2 || len(m.Content) > 2048 {
		return ErrInvalidgithub.com / Khmelov / Distcomp / 351001 / UshakovContent
	}

	return nil
}
