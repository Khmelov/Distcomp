package editor

import "errors"

type Editor struct {
	ID        int64
	Login     string
	Password  string
	Firstname string
	Lastname  string
}

func (e *Editor) GetID() int64   { return e.ID }
func (e *Editor) SetID(id int64) { e.ID = id }

type CreateEditorInput struct {
	Login     string
	Password  string
	Firstname string
	Lastname  string
}

type UpdateEditorInput struct {
	Login     *string
	Password  *string
	Firstname *string
	Lastname  *string
}

var (
	ErrNotFound   = errors.New("editor not found")
	ErrLoginTaken = errors.New("editor login is already taken")
)
