package request

// NoteRequestTo представляет DTO для запроса создания/обновления заметки
type NoteRequestTo struct {
	Content string `json:"content" binding:"required"`
	IssueID int64  `json:"issueId" binding:"required"`
}

