package domain

import "time"

type Editor struct {
	ID        uint      `gorm:"primaryKey" json:"id"`
	Login     string    `gorm:"unique;not null" json:"login"`
	Password  string    `gorm:"not null" json:"password"`
	Firstname string    `gorm:"not null" json:"firstname"`
	Lastname  string    `gorm:"not null" json:"lastname"`
	CreatedAt time.Time `json:"-"`
}

func (Editor) TableName() string { return "tbl_editor" }

type Topic struct {
	ID       uint     `gorm:"primaryKey" json:"id"`
	EditorID uint     `gorm:"not null" json:"editorId"`
	Title    string   `gorm:"unique;not null" json:"title"`
	Content  string   `gorm:"not null" json:"content"`
	Markers  []Marker `gorm:"many2many:tbl_topic_marker;constraint:OnDelete:CASCADE" json:"markers,omitempty"`
}

func (Topic) TableName() string { return "tbl_topic" }

type Marker struct {
	ID   uint   `gorm:"primaryKey" json:"id"`
	Name string `gorm:"unique;not null" json:"name"`
}

func (Marker) TableName() string { return "tbl_marker" }

type Note struct {
	ID      int    `json:"id"`
	TopicID int    `json:"topicId"`
	Content string `json:"content"`
	State   string `json:"state"` // PENDING, APPROVE, DECLINE
}

// DTO для входящих данных с жесткой валидацией
type EditorDTO struct {
	ID        *uint  `json:"id,omitempty"`
	Login     string `json:"login" binding:"required,min=4,max=50"`    // min=4 отсечет "x"
	Password  string `json:"password" binding:"required,min=8,max=50"` // min=8 отсечет "z234567"
	Firstname string `json:"firstname" binding:"required,min=2,max=50"`
	Lastname  string `json:"lastname" binding:"required,min=2,max=50"`
}
