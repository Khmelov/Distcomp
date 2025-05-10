package models

type Message struct {
	Country string `json:"Country"`
	StoryID uint   `json:"storyId"`
	ID      uint   `json:"id"`
	Content string `json:"content"`
}

type MessageData struct {
	StoryID int    `json:"storyId"`
	Content string `json:"content"`
	ID      int    `json:"id"`
}
