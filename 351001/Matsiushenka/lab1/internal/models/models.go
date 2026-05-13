package models

type EditorRequestTo struct {
	Login     string `json:"login" binding:"required,min=2,max=64"`
	Password  string `json:"password" binding:"required,min=8,max=128"`
	FirstName string `json:"firstname" binding:"required,min=2,max=64"`
	LastName  string `json:"lastname" binding:"required,min=2,max=64"`
}

type EditorResponseTo struct {
	ID        int64  `json:"id"`
	Login     string `json:"login"`
	FirstName string `json:"firstname"`
	LastName  string `json:"lastname"`
}

type TopicRequestTo struct {
	EditorID int64  `json:"editorId" binding:"required"`
	Title    string `json:"title" binding:"required,min=2,max=64"`
	Content  string `json:"content" binding:"required,min=2,max=2048"`
}

type TopicResponseTo struct {
	ID       int64  `json:"id"`
	EditorID int64  `json:"editorId"`
	Title    string `json:"title"`
	Content  string `json:"content"`
}

type MarkerRequestTo struct {
	Name string `json:"name" binding:"required,min=2,max=32"`
}

type MarkerResponseTo struct {
	ID   int64  `json:"id"`
	Name string `json:"name"`
}

type NoteRequestTo struct {
	TopicID int64  `json:"topicId" binding:"required"`
	Content string `json:"content" binding:"required,min=2,max=2048"`
}

type NoteResponseTo struct {
	ID      int64  `json:"id"`
	TopicID int64  `json:"topicId"`
	Content string `json:"content"`
}

type ErrorResponse struct {
	ErrorMessage string `json:"errorMessage"`
	ErrorCode    int    `json:"errorCode"`
}
