package sticker

import "errors"

type Sticker struct {
	ID   int64
	Name string
}

func (s *Sticker) GetID() int64   { return s.ID }
func (s *Sticker) SetID(id int64) { s.ID = id }

type CreateStickerInput struct {
	Name string
}

type UpdateStickerInput struct {
	Name *string
}

var (
	ErrNotFound  = errors.New("sticker not found")
	ErrNameTaken = errors.New("sticker name is already taken")
)
