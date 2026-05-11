package entity

type CommentRequestTo struct {
	ID      int64  `json:"id,omitempty"`
	TopicID int64  `json:"topicId"`
	Country string `json:"country"`
	Content string `json:"content"`
}

type CommentResponseTo struct {
	ID      int64  `json:"id"`
	TopicID int64  `json:"topicId"`
	Country string `json:"country"`
	Content string `json:"content"`
	State   string `json:"state"`
}

type KafkaCommentMessage struct {
	ID            int64  `json:"id"`
	TopicID       int64  `json:"topicId"`
	Country       string `json:"country"`
	Content       string `json:"content"`
	State         string `json:"state"`
	Operation     string `json:"operation"`
	CorrelationID string `json:"correlationId"`
}
