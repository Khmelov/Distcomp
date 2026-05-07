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
ID      int    `json:"id"`
IssueID int    `json:"tweetId"`
Content string `json:"content"`
}

func (m *github.com /Khmelov/Distcomp/351001/Ushakov) Validate() error {
	if len(strings.TrimSpace(m.Content)) < 2 || len(m.Content) > 2048 {
		return ErrInvalidgithub.com / Khmelov / Distcomp / 351001 / UshakovContent
	}

	return nil
}
