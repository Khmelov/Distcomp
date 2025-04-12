package model

import (
	"errors"
	"strings"
)

var (
	ErrInvalidCommentContent = errors.New("content must be between 2 and 2048 characters")
)

type Comment struct {
	ID      int64  `json:"id" db:"id"`
	TweetID int64  `json:"tweetId" db:"tweet_id"`
	Content string `json:"content" db:"content"`
}

func (m *Comment) Validate() error {
	if len(strings.TrimSpace(m.Content)) < 2 || len(m.Content) > 2048 {
		return ErrInvalidCommentContent
	}

	return nil
}
