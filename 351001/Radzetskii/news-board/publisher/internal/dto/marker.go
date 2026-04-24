package dto

type MarkerRequestTo struct {
	Name string `json:"name" validate:"required,min=2,max=32" example:"urgent"`
}

type MarkerResponseTo struct {
	ID   int64  `json:"id" example:"1"`
	Name string `json:"name" example:"urgent"`
}
