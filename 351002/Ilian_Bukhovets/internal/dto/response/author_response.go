package response

// AuthorResponseTo представляет DTO для ответа с данными автора
type AuthorResponseTo struct {
	ID        int64  `json:"id"`
	Login     string `json:"login"`
	Password  string `json:"password"`
	Firstname string `json:"firstname"`
	Lastname  string `json:"lastname"`
	Created   string `json:"created"`
	Modified  string `json:"modified"`
}

