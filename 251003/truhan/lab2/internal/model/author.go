package model

import (
	"errors"
	"strings"
)

var (
	ErrInvalidLogin     = errors.New("login must be between 2 and 64 characters")
	ErrInvalidPassword  = errors.New("password must be between 8 and 124 characters")
	ErrInvalidFirstName = errors.New("firstname must be between 2 and 64 characters")
	ErrInvalidLastName  = errors.New("lastname must be between 2 and 64 characters")
)

type Author struct {
	ID        int64  `json:"id" db:"id"`
	Login     string `json:"login" db:"login"`
	Password  string `json:"password" db:"password"`
	FirstName string `json:"firstname" db:"firstname"`
	LastName  string `json:"lastname" db:"lastname"`
}

func (c *Author) Validate() error {
	if len(strings.TrimSpace(c.Login)) < 2 || len(c.Login) > 64 {
		return ErrInvalidLogin
	}
	if len(c.Password) < 8 || len(c.Password) > 124 {
		return ErrInvalidPassword
	}
	if len(strings.TrimSpace(c.FirstName)) < 2 || len(c.FirstName) > 64 {
		return ErrInvalidFirstName
	}
	if len(strings.TrimSpace(c.LastName)) < 2 || len(c.LastName) > 64 {
		return ErrInvalidLastName
	}
	return nil
}
