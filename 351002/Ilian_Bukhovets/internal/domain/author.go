package domain

// Author представляет автора
type Author struct {
	BaseEntity
	Login     string `json:"login"`
	Password  string `json:"password"`
	Firstname string `json:"firstname"`
	Lastname  string `json:"lastname"`
}

