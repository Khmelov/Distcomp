package controllers

type CommentData struct {
	NewsID int    `json:"newsId"`
	Content string `json:"content"`
	ID      int    `json:"id"`
}
