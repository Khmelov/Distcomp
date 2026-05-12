package domain

type Note struct {
	ID      int    `json:"id"`
	TopicID int    `json:"topicId"`
	Content string `json:"content"`
	State   string `json:"state"` // PENDING, APPROVE, DECLINE
}
