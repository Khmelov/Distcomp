package dto

import "time"

type NewsRequestTo struct {
	UserID  int64    `json:"userId" validate:"required,min=1" example:"1"`
	Title   string   `json:"title" validate:"required,min=2,max=64" example:"Breaking News"`
	Content string   `json:"content" validate:"required,min=4,max=2048" example:"Some important content here..."`
	Markers []string `json:"markers,omitempty" example:"red,green,blue"`
}

type NewsResponseTo struct {
	ID       int64     `json:"id" example:"1"`
	UserID   int64     `json:"userId" example:"1"`
	Title    string    `json:"title" example:"Breaking News"`
	Content  string    `json:"content" example:"Some important content here..."`
	Created  time.Time `json:"created" example:"2023-01-01T00:00:00Z"`
	Modified time.Time `json:"modified" example:"2023-01-01T00:00:00Z"`
}
