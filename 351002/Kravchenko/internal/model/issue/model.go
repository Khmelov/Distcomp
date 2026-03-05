package issue

import (
	"errors"
	"lab1/internal/model/sticker"
	"time"
)

type Issue struct {
	ID       int64
	EditorID int64
	Title    string
	Content  string
	Created  time.Time
	Modified time.Time
	Stickers []sticker.Sticker
}

func (i *Issue) GetID() int64   { return i.ID }
func (i *Issue) SetID(id int64) { i.ID = id }

type CreateIssueInput struct {
	EditorID int64
	Title    string
	Content  string
	Stickers []int64
}

type UpdateIssueInput struct {
	Title    *string
	Content  *string
	Stickers []int64
}

var (
	ErrNotFound   = errors.New("issue not found")
	ErrTitleTaken = errors.New("title taken")
)
