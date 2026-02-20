package domain

// Issue представляет задачу/проблему
type Issue struct {
	BaseEntity
	Title      string  `json:"title"`
	Content    string  `json:"content"`
	AuthorID   int64   `json:"authorId"`
	MarkerIDs  []int64 `json:"markerIds"`
}

