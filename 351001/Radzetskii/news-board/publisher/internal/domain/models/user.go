package models

import "context"

type User struct {
	ID        int64
	Login     string
	Password  string
	Firstname string
	Lastname  string
}

type UserRepository interface {
	Create(ctx context.Context, user *User) error
	GetByID(ctx context.Context, id int64) (*User, error)
	GetByLogin(ctx context.Context, login string) (*User, error)
	GetAll(ctx context.Context, limit, offset int) ([]User, error)
	Update(ctx context.Context, user *User) (bool, error)
	Delete(ctx context.Context, id int64) (bool, error)
	GetByNewsID(ctx context.Context, newsID int64) (*User, error)
}
