package kafka

import "distcomp/internal/dto"

const (
	OpCreate = "CREATE"
	OpGet    = "GET"
	OpGetAll = "GET_ALL"
	OpUpdate = "UPDATE"
	OpDelete = "DELETE"
)

type Message struct {
	CorrelationID string                  `json:"correlationId"`
	Operation     string                  `json:"operation"`
	CommentID     int64                   `json:"commentId,omitempty"`
	ArticleID     int64                   `json:"articleId,omitempty"`
	Request       *dto.CommentRequestTo   `json:"request,omitempty"`
	Response      *dto.CommentResponseTo  `json:"response,omitempty"`
	Responses     []dto.CommentResponseTo `json:"responses,omitempty"`
	Error         string                  `json:"error,omitempty"`
}