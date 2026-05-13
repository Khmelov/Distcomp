package domain

type Editor struct {
	ID        uint   `gorm:"primaryKey" json:"id"`
	Login     string `gorm:"unique;not null" json:"login"`
	Password  string `gorm:"not null" json:"password"`
	Firstname string `gorm:"not null" json:"firstname"`
	Lastname  string `gorm:"not null" json:"lastname"`
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

type EditorDTO struct {
	ID        *uint  `json:"id,omitempty"`
	Login     string `json:"login" binding:"required,min=3"`
	Password  string `json:"password" binding:"required,min=8"`
	Firstname string `json:"firstname" binding:"required"`
	Lastname  string `json:"lastname" binding:"required"`
}

type TopicDTO struct {
	ID       *uint  `json:"id,omitempty"`
	EditorID uint   `json:"editorId" binding:"required"`
	Title    string `json:"title" binding:"required,min=3"`
	Content  string `json:"content" binding:"required"`
}
