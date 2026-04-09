package model

import (
	"errors"
	"strings"
)

var (
	ErrInvalidTitle   = errors.New("title must be between 2 and 64 characters")
	ErrInvalidContent = errors.New("content must be between 4 and 2048 characters")
)

type Issue struct {
	ID        int64    `json:"id"`
	CreatorID int64    `json:"authorId"`
	Title     string   `json:"title"`
	Content   string   `json:"content"`
	Created   string   `json:"created"`
	Modified  string   `json:"modified"`
	Marks     []string `json:"labels"`
}

func (i *Issue) Validate() error {
	if len(strings.TrimSpace(i.Title)) < 2 || len(i.Title) > 64 {
		return ErrInvalidTitle
	}
	if len(strings.TrimSpace(i.Content)) < 4 || len(i.Content) > 2048 {
		return ErrInvalidContent
	}
	return nil
}
