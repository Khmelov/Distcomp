package sticker

import (
	"context"
	"errors"
	"labs/publisher/internal/repository"
	stickermodel "labs/shared/model/sticker"
)

type Service interface {
	CreateSticker(ctx context.Context, input *stickermodel.CreateStickerInput) (*stickermodel.Sticker, error)
	GetSticker(ctx context.Context, id int64) (*stickermodel.Sticker, error)
	UpdateSticker(ctx context.Context, id int64, input *stickermodel.UpdateStickerInput) (*stickermodel.Sticker, error)
	DeleteSticker(ctx context.Context, id int64) error
	ListStickers(ctx context.Context, limit, offset int) ([]*stickermodel.Sticker, error)
}
type stickerServiceImpl struct {
	repos repository.AppRepository
}

func New(repos repository.AppRepository) Service {
	return &stickerServiceImpl{repos: repos}
}

func (s *stickerServiceImpl) CreateSticker(ctx context.Context, input *stickermodel.CreateStickerInput) (*stickermodel.Sticker, error) {
	_, err := s.repos.StickerRepo().GetByName(ctx, input.Name)
	if err == nil {
		return nil, stickermodel.ErrNameTaken
	}
	if !errors.Is(err, stickermodel.ErrNotFound) {
		return nil, err
	}

	sticker := &stickermodel.Sticker{
		Name: input.Name,
	}

	return s.repos.StickerRepo().Create(ctx, sticker)
}

func (s *stickerServiceImpl) GetSticker(ctx context.Context, id int64) (*stickermodel.Sticker, error) {
	return s.repos.StickerRepo().GetByID(ctx, id)
}

func (s *stickerServiceImpl) UpdateSticker(ctx context.Context, id int64, input *stickermodel.UpdateStickerInput) (*stickermodel.Sticker, error) {
	sticker, err := s.repos.StickerRepo().GetByID(ctx, id)
	if err != nil {
		return nil, err
	}

	if input.Name != nil {
		sticker.Name = *input.Name
	}

	err = s.repos.StickerRepo().Update(ctx, sticker)
	if err != nil {
		return nil, err
	}

	return sticker, nil
}

func (s *stickerServiceImpl) DeleteSticker(ctx context.Context, id int64) error {
	return s.repos.StickerRepo().Delete(ctx, id)
}

func (s *stickerServiceImpl) ListStickers(ctx context.Context, limit, offset int) ([]*stickermodel.Sticker, error) {
	return s.repos.StickerRepo().List(ctx, limit, offset)
}
