package note

import "errors"

type Note struct {
	ID      int64
	IssueID int64
	Content string
	State   string
}

func (n *Note) GetID() int64   { return n.ID }
func (n *Note) SetID(id int64) { n.ID = id }

type CreateNoteInput struct {
	ID      int64
	IssueID int64
	Content string
}

type UpdateNoteInput struct {
	ID      int64
	Content *string
}

var (
	ErrNotFound = errors.New("note not found")
)

const (
	StatePending = "PENDING"
	StateApprove = "APPROVE"
	StateDecline = "DECLINE"
)
