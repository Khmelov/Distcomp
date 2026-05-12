package model

import (
	"errors"
	"strings"
)

var (
	ErrInvalidMessageContent = errors.New("content must be between 2 and 2048 characters")
)

type Message struct {
	ID      int    `json:"id"`
	IssueID int    `json:"tweetId"`
	Content string `json:"content"`
}

func (m *Message) Validate() error {
	if len(strings.TrimSpace(m.Content)) < 2 || len(m.Content) > 2048 {
		return ErrInvalidMessageContent
	}

	return nil
}
