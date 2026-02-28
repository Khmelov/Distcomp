package request

// MarkerRequestTo представляет DTO для запроса создания/обновления метки
type MarkerRequestTo struct {
	Name string `json:"name" binding:"required"`
}

