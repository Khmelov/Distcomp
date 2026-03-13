package domain

import "time"

// BaseEntity содержит общие поля для всех сущностей
type BaseEntity struct {
	ID       int64     `json:"id"`
	Created  time.Time `json:"created"`
	Modified time.Time `json:"modified"`
}

