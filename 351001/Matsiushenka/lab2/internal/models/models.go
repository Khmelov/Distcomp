package models

type Editor struct {
	ID        int64  `gorm:"primaryKey;autoIncrement"`
	Login     string `gorm:"unique;not null"`
	Password  string `gorm:"not null"`
	FirstName string `gorm:"column:firstname;not null"`
	LastName  string `gorm:"column:lastname;not null"`
}

func (Editor) TableName() string { return "tbl_editor" }

type Topic struct {
	ID       int64    `gorm:"primaryKey;autoIncrement"`
	EditorID int64    `gorm:"column:editor_id;not null"`
	Title    string   `gorm:"unique;not null"`
	Content  string   `gorm:"not null"`
	Markers  []Marker `gorm:"many2many:tbl_topic_marker;constraint:OnDelete:CASCADE"`
}

func (Topic) TableName() string { return "tbl_topic" }

type Marker struct {
	ID   int64  `gorm:"primaryKey;autoIncrement"`
	Name string `gorm:"unique;not null"`
}

func (Marker) TableName() string { return "tbl_marker" }

type Note struct {
	ID      int64  `gorm:"primaryKey;autoIncrement"`
	TopicID int64  `gorm:"column:topic_id;not null"`
	Content string `gorm:"not null"`
}

func (Note) TableName() string { return "tbl_note" }

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
	EditorID int64    `json:"editorId" binding:"required"`
	Title    string   `json:"title" binding:"required,min=2,max=64"`
	Content  string   `json:"content" binding:"required,min=2,max=2048"`
	Markers  []string `json:"markers"`
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
