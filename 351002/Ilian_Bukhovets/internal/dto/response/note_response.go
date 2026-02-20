package response

// NoteResponseTo представляет DTO для ответа с данными заметки
type NoteResponseTo struct {
	ID       int64  `json:"id"`
	Content  string `json:"content"`
	IssueID  int64  `json:"issueId"`
	Created  string `json:"created"`
	Modified string `json:"modified"`
}

