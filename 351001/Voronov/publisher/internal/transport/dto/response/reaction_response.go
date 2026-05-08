package response

import "publisher/internal/model"

type ReactionResponseTo struct {
	ID      int64             `json:"id"`
	IssueID int64             `json:"issueId"`
	Content string            `json:"content"`
	State   model.ReactionState `json:"state"`
}
