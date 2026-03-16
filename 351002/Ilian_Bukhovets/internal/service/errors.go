package service

import "errors"

// Общие ошибки для всех сервисов
var (
	ErrAuthorNotFound  = errors.New("author not found")
	ErrIssueNotFound   = errors.New("issue not found")
	ErrMarkerNotFound  = errors.New("marker not found")
	ErrNoteNotFound    = errors.New("note not found")
	ErrInvalidAuthorID = errors.New("invalid author id")
	ErrInvalidIssueID  = errors.New("invalid issue id")
)


