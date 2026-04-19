package request

type UserRequestTo struct {
	Login     string `json:"login" validate:"required,min=2,max=64"`
	Password  string `json:"password" validate:"required,min=8,max=128"`
	Firstname string `json:"firstname" validate:"required,min=2,max=64"`
	Lastname  string `json:"lastname" validate:"required,min=2,max=64"`
}
