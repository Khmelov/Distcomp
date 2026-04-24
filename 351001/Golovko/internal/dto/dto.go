package dto

// --- EDITOR ---
type EditorRequestTo struct {
	ID        int64  `json:"id,omitempty"`
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

// --- ARTICLE ---
type ArticleRequestTo struct {
	ID       int64    `json:"id,omitempty"`
	EditorID int64    `json:"editorId" binding:"required"`
	Title    string   `json:"title" binding:"required,min=2,max=64"`
	Content  string   `json:"content" binding:"required,min=4,max=2048"`
	Tags     []string `json:"tags,omitempty"` // <--- ИЗМЕНИЛИ ЗДЕСЬ
}

type ArticleResponseTo struct {
	ID       int64           `json:"id"`
	EditorID int64           `json:"editorId"`
	Title    string          `json:"title"`
	Content  string          `json:"content"`
	Created  string          `json:"created"`
	Modified string          `json:"modified"`
	Tags     []TagResponseTo `json:"tags,omitempty"`
}

// --- TAG ---
type TagRequestTo struct {
	ID   int64  `json:"id,omitempty"`
	Name string `json:"name" binding:"required,min=2,max=32"`
}

type TagResponseTo struct {
	ID   int64  `json:"id"`
	Name string `json:"name"`
}

// --- COMMENT ---
type CommentRequestTo struct {
	ID        int64  `json:"id,omitempty"`
	ArticleID int64  `json:"articleId" binding:"required"`
	Content   string `json:"content" binding:"required,min=2,max=2048"`
}

type CommentResponseTo struct {
	ID        int64  `json:"id"`
	ArticleID int64  `json:"articleId"`
	Content   string `json:"content"`
}