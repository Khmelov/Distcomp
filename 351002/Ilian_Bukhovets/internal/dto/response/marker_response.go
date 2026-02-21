package response

// MarkerResponseTo представляет DTO для ответа с данными метки
type MarkerResponseTo struct {
	ID       int64  `json:"id"`
	Name     string `json:"name"`
	Created  string `json:"created"`
	Modified string `json:"modified"`
}

