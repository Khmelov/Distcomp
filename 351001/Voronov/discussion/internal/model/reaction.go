package model

type ReactionState string

const (
	ReactionStatePending ReactionState = "PENDING"
	ReactionStateApprove ReactionState = "APPROVE"
	ReactionStateDecline ReactionState = "DECLINE"
)

type Reaction struct {
	ID      int64         `json:"id"`
	IssueID int64         `json:"issueId"`
	Content string        `json:"content"`
	State   ReactionState `json:"state"`
}
