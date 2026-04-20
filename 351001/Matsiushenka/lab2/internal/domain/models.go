package domain

import (
	"time"
)

type Editor struct {
	ID        uint      `gorm:"primaryKey" json:"id"`
	Login     string    `gorm:"unique;not null;size:50" json:"login"`
	Password  string    `gorm:"not null" json:"password"`
	Firstname string    `gorm:"not null" json:"firstname"`
	Lastname  string    `gorm:"not null" json:"lastname"`
	CreatedAt time.Time `json:"-"`
	UpdatedAt time.Time `json:"-"`
}

func (Editor) TableName() string { return "tbl_editor" }

type Topic struct {
	ID        uint      `gorm:"primaryKey" json:"id"`
	EditorID  uint      `gorm:"not null" json:"editorId"`
	Editor    Editor    `gorm:"foreignKey:EditorID;constraint:OnDelete:CASCADE;" json:"-"`
	Title     string    `gorm:"unique;not null;size:100" json:"title"`
	Content   string    `gorm:"not null" json:"content"`
	Markers   []Marker  `gorm:"many2many:tbl_topic_marker;constraint:OnDelete:CASCADE;" json:"markers,omitempty"`
	CreatedAt time.Time `json:"-"`
	UpdatedAt time.Time `json:"-"`
}

func (Topic) TableName() string { return "tbl_topic" }

type Marker struct {
	ID        uint      `gorm:"primaryKey" json:"id"`
	Name      string    `gorm:"unique;not null;size:50" json:"name"`
	CreatedAt time.Time `json:"-"`
	UpdatedAt time.Time `json:"-"`
}

func (Marker) TableName() string { return "tbl_marker" }

type Note struct {
	ID        uint      `gorm:"primaryKey" json:"id"`
	TopicID   uint      `gorm:"not null" json:"topicId"`
	Topic     Topic     `gorm:"foreignKey:TopicID;constraint:OnDelete:CASCADE;" json:"-"`
	Content   string    `gorm:"not null" json:"content"`
	CreatedAt time.Time `json:"-"`
	UpdatedAt time.Time `json:"-"`
}

func (Note) TableName() string { return "tbl_note" }

type EditorDTO struct {
	ID        *uint  `json:"id,omitempty"`
	Login     string `json:"login" binding:"required,min=3,max=50"`
	Password  string `json:"password" binding:"required,min=8"`
	Firstname string `json:"firstname" binding:"required,min=2"`
	Lastname  string `json:"lastname" binding:"required,min=2"`
}

type TopicDTO struct {
	ID       *uint    `json:"id,omitempty"`
	EditorID uint     `json:"editorId" binding:"required"`
	Title    string   `json:"title" binding:"required,min=3,max=50"`
	Content  string   `json:"content" binding:"required"`
	Markers  []string `json:"markers,omitempty"`
}

type MarkerDTO struct {
	ID   *uint  `json:"id,omitempty"`
	Name string `json:"name" binding:"required,min=2,max=30"`
}

type NoteDTO struct {
	ID      *uint  `json:"id,omitempty"`
	TopicID uint   `json:"topicId" binding:"required"`
	Content string `json:"content" binding:"required,min=2"`
}
