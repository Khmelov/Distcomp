package dto

type NoticeRequestTo struct {
	Country string `json:"country" validate:"omitempty,min=2,max=64" example:"BY"`
	NewsID  int64  `json:"newsId" validate:"required,min=1" example:"1"`
	Content string `json:"content" validate:"required,min=2,max=2048" example:"This is a notice."`
}

type NoticeResponseTo struct {
	Country string `json:"country" example:"BY"`
	ID      int64  `json:"id" example:"1"`
	NewsID  int64  `json:"newsId" example:"1"`
	Content string `json:"content" example:"This is a notice."`
}
