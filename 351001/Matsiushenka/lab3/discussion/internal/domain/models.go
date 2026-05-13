package domain

type Note struct {
	ID      int    `json:"id"`
	TopicID int    `json:"topicId"`
	Content string `json:"content"`
}
