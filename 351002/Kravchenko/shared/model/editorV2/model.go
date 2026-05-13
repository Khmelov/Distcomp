package editor

import "errors"

type Editor struct {
	ID        int64
	Login     string
	Password  string // Здесь будет храниться BCrypt хеш
	Firstname string
	Lastname  string
	Role      string // Добавлено: ADMIN или CUSTOMER
}

func (e *Editor) GetID() int64   { return e.ID }
func (e *Editor) SetID(id int64) { e.ID = id }

type CreateEditorInput struct {
	Login     string
	Password  string
	Firstname string
	Lastname  string
	Role      string // Добавлено для v2.0
}

type UpdateEditorInput struct {
	Login     *string
	Password  *string
	Firstname *string
	Lastname  *string
	Role      *string // Добавлено
}

var (
	ErrNotFound           = errors.New("editor not found")
	ErrLoginTaken         = errors.New("editor login is already taken")
	ErrInvalidCredentials = errors.New("invalid login or password") // Для логина
)
