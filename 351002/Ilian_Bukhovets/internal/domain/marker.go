package domain

// Marker представляет метку/тег
type Marker struct {
	BaseEntity
	Name string `json:"name"`
}

