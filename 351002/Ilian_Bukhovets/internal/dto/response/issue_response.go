package response

// IssueResponseTo представляет DTO для ответа с данными задачи
type IssueResponseTo struct {
	ID        int64    `json:"id"`
	Title     string   `json:"title"`
	Content   string   `json:"content"`
	AuthorID  int64    `json:"authorId"`
	MarkerIDs []int64  `json:"markerIds"`
	Created   string   `json:"created"`
	Modified  string   `json:"modified"`
}

