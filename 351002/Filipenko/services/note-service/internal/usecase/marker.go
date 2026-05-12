package usecase

import (
	"context"
	"note-service/internal/entity"
	"note-service/internal/pkg/cache"
	"note-service/internal/repository"
	"unicode/utf8"
)

type MarkerUsecase struct {
	repo  repository.NoteRepository[entity.Marker]
	cache cache.Cache
}

func NewMarkerUsecase(repo repository.NoteRepository[entity.Marker], cache cache.Cache) *MarkerUsecase {
	return &MarkerUsecase{repo: repo, cache: cache}
}

func (uc *MarkerUsecase) validate(req entity.MarkerRequestTo) error {
	if l := utf8.RuneCountInString(req.Name); l < 2 || l > 32 {
		return entity.NewErr(400, "07", "name must be 2..32 chars")
	}
	return nil
}

func (uc *MarkerUsecase) toDTO(m entity.Marker) entity.MarkerResponseTo {
	return entity.MarkerResponseTo{ID: m.ID, Name: m.Name}
}

func (uc *MarkerUsecase) Create(ctx context.Context, req entity.MarkerRequestTo) (entity.MarkerResponseTo, error) {
	if err := uc.validate(req); err != nil {
		return entity.MarkerResponseTo{}, err
	}
	marker := entity.Marker{Name: req.Name}
	saved, err := uc.repo.Create(ctx, marker)
	if err != nil {
		return entity.MarkerResponseTo{}, err
	}

	dto := uc.toDTO(saved)
	cacheKey := cache.CacheKey("marker", saved.ID)
	_ = uc.cache.Set(ctx, cacheKey, dto, cache.DefaultExpiration)

	listKey := cache.CacheListKey("marker")
	_ = uc.cache.Delete(ctx, listKey)

	return dto, nil
}

func (uc *MarkerUsecase) GetByID(ctx context.Context, id int64) (entity.MarkerResponseTo, error) {
	cacheKey := cache.CacheKey("marker", id)
	var cached entity.MarkerResponseTo

	if err := uc.cache.Get(ctx, cacheKey, &cached); err == nil {
		return cached, nil
	}

	m, err := uc.repo.GetByID(ctx, id)
	if err != nil {
		return entity.MarkerResponseTo{}, err
	}

	dto := uc.toDTO(m)
	_ = uc.cache.Set(ctx, cacheKey, dto, cache.DefaultExpiration)

	return dto, nil
}

func (uc *MarkerUsecase) GetAll(ctx context.Context, limit, offset int) ([]entity.MarkerResponseTo, error) {
	listKey := cache.CacheListKey("marker")
	var cached []entity.MarkerResponseTo

	if err := uc.cache.Get(ctx, listKey, &cached); err == nil && len(cached) > 0 {
		if offset >= len(cached) {
			return []entity.MarkerResponseTo{}, nil
		}
		end := offset + limit
		if end > len(cached) {
			end = len(cached)
		}
		return cached[offset:end], nil
	}

	markers, err := uc.repo.GetAll(ctx, limit*10, 0)
	if err != nil {
		return nil, err
	}

	res := make([]entity.MarkerResponseTo, 0, len(markers))
	for _, m := range markers {
		res = append(res, uc.toDTO(m))
	}

	_ = uc.cache.Set(ctx, listKey, res, cache.ShortExpiration)

	if offset >= len(res) {
		return []entity.MarkerResponseTo{}, nil
	}
	end := offset + limit
	if end > len(res) {
		end = len(res)
	}
	return res[offset:end], nil
}

func (uc *MarkerUsecase) Update(ctx context.Context, id int64, req entity.MarkerRequestTo) (entity.MarkerResponseTo, error) {
	if err := uc.validate(req); err != nil {
		return entity.MarkerResponseTo{}, err
	}
	if _, err := uc.repo.GetByID(ctx, id); err != nil {
		return entity.MarkerResponseTo{}, err
	}
	marker := entity.Marker{ID: id, Name: req.Name}
	updated, err := uc.repo.Update(ctx, id, marker)
	if err != nil {
		return entity.MarkerResponseTo{}, err
	}

	dto := uc.toDTO(updated)
	cacheKey := cache.CacheKey("marker", id)
	_ = uc.cache.Set(ctx, cacheKey, dto, cache.DefaultExpiration)

	listKey := cache.CacheListKey("marker")
	_ = uc.cache.Delete(ctx, listKey)

	return dto, nil
}

func (uc *MarkerUsecase) Delete(ctx context.Context, id int64) error {
	cacheKey := cache.CacheKey("marker", id)
	_ = uc.cache.Delete(ctx, cacheKey)

	listKey := cache.CacheListKey("marker")
	_ = uc.cache.Delete(ctx, listKey)

	return uc.repo.Delete(ctx, id)
}
