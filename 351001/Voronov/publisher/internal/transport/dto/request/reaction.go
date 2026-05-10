package request

type ReactionRequestTo struct {
	IssueID int64  `json:"issueId" validate:"required"`
	Content string `json:"content" validate:"required,min=2,max=2048"`
}
