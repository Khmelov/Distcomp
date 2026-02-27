package request

// IssueRequestTo представляет DTO для запроса создания/обновления задачи
type IssueRequestTo struct {
	Title     string  `json:"title" binding:"required"`
	Content   string  `json:"content" binding:"required"`
	AuthorID  int64   `json:"authorId" binding:"required"`
	MarkerIDs []int64 `json:"markerIds"`
}

