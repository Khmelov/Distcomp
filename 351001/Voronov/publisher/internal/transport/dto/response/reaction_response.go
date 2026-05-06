package response

type ReactionResponseTo struct {
	ID      int64  `json:"id"`
	IssueID int64  `json:"issueId"`
	Content string `json:"content"`
}
