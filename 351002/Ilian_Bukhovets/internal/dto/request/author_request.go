package request

// AuthorRequestTo представляет DTO для запроса создания/обновления автора
type AuthorRequestTo struct {
	Login     string `json:"login" binding:"required"`
	Password  string `json:"password" binding:"required"`
	Firstname string `json:"firstname" binding:"required"`
	Lastname  string `json:"lastname" binding:"required"`
}

