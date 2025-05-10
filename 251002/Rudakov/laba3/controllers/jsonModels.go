package controllers

type MessageData struct {
	StoryID int    `json:"storyId"`
	Content string `json:"content"`
	ID      int    `json:"id"`
}
