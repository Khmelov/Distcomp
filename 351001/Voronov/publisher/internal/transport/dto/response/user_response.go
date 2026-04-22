package response

type UserResponseTo struct {
	ID        int64  `json:"id"`
	Login     string `json:"login"`
	Firstname string `json:"firstname"`
	Lastname  string `json:"lastname"`
}
