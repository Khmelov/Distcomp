package models

type Comment struct {
	Country string `json:"Country"`
	NewsID uint   `json:"newsId"`
	ID      uint   `json:"id"`
	Content string `json:"content"`
}

type CommentData struct {
	NewsID int    `json:"newsId"`
	Content string `json:"content"`
	ID      int    `json:"id"`
}
