package model

type Author struct {
	ID        int64  `db:"id" json:"id"`
	Login     string `db:"login" json:"login"`
	Password  string `db:"password" json:"password"`
	FirstName string `db:"firstname" json:"firstname"`
	LastName  string `db:"lastname" json:"lastname"`
}
