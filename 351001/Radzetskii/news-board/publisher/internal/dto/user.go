package dto

type UserRequestTo struct {
	Login     string `json:"login" validate:"required,min=2,max=64" example:"john_doe"`
	Password  string `json:"password" validate:"required,min=8,max=128" example:"securePass123"`
	Firstname string `json:"firstname" validate:"required,min=2,max=64" example:"John"`
	Lastname  string `json:"lastname" validate:"required,min=2,max=64" example:"Doe"`
	Role      string `json:"role,omitempty" validate:"omitempty,oneof=ADMIN CUSTOMER" example:"CUSTOMER"`
}

type UserResponseTo struct {
	ID        int64  `json:"id" example:"1"`
	Login     string `json:"login" example:"john_doe"`
	Firstname string `json:"firstname" example:"John"`
	Lastname  string `json:"lastname" example:"Doe"`
	Role      string `json:"role,omitempty" example:"CUSTOMER"`
}

type LoginRequestTo struct {
	Login    string `json:"login" validate:"required,min=2,max=64" example:"john_doe"`
	Password string `json:"password" validate:"required,min=8,max=128" example:"securePass123"`
}

type LoginResponseTo struct {
	AccessToken string `json:"access_token" example:"eyJhb..."`
	TokenType   string `json:"token_type,omitempty" example:"Bearer"`
}
