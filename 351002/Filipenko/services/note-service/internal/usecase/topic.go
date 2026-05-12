package usecase

import (
	"context"
	"note-service/internal/entity"
	"note-service/internal/pkg/cache"
	"note-service/internal/repository"
	"time"
	"unicode/utf8"
)

type TopicUsecase struct {
	repo  repository.NoteRepository[entity.Topic]
	cache cache.Cache
}

func NewTopicUsecase(repo repository.NoteRepository[entity.Topic], cache cache.Cache) *TopicUsecase {
	return &TopicUsecase{repo: repo, cache: cache}
}

func (uc *TopicUsecase) validate(req entity.TopicRequestTo) error {
	if l := utf8.RuneCountInString(req.Title); l < 2 || l > 64 {
		return entity.NewErr(400, "05", "title must be 2..64 chars")
	}
	if l := utf8.RuneCountInString(req.Content); l < 4 || l > 2048 {
		return entity.NewErr(400, "06", "content must be 4..2048 chars")
	}
	return nil
}

func (uc *TopicUsecase) toDTO(t entity.Topic) entity.TopicResponseTo {
	return entity.TopicResponseTo{
		ID: t.ID, AuthorID: t.AuthorID, Title: t.Title,
		Content: t.Content, MarkerIDs: t.MarkerIDs,
		Created: t.Created, Modified: t.Modified,
	}
}

func (uc *TopicUsecase) Create(ctx context.Context, req entity.TopicRequestTo) (entity.TopicResponseTo, error) {
	if err := uc.validate(req); err != nil {
		return entity.TopicResponseTo{}, err
	}
	now := time.Now()
	topic := entity.Topic{
		AuthorID: req.AuthorID, Title: req.Title, Content: req.Content,
		MarkerIDs: req.MarkerIDs, Created: now, Modified: now,
	}
	saved, err := uc.repo.Create(ctx, topic)
	if err != nil {
		return entity.TopicResponseTo{}, err
	}

	dto := uc.toDTO(saved)
	cacheKey := cache.CacheKey("topic", saved.ID)
	_ = uc.cache.Set(ctx, cacheKey, dto, cache.DefaultExpiration)

	listKey := cache.CacheListKey("topic")
	_ = uc.cache.Delete(ctx, listKey)

	return dto, nil
}

func (uc *TopicUsecase) GetByID(ctx context.Context, id int64) (entity.TopicResponseTo, error) {
	cacheKey := cache.CacheKey("topic", id)
	var cached entity.TopicResponseTo

	if err := uc.cache.Get(ctx, cacheKey, &cached); err == nil {
		return cached, nil
	}

	t, err := uc.repo.GetByID(ctx, id)
	if err != nil {
		return entity.TopicResponseTo{}, err
	}

	dto := uc.toDTO(t)
	_ = uc.cache.Set(ctx, cacheKey, dto, cache.DefaultExpiration)

	return dto, nil
}

func (uc *TopicUsecase) GetAll(ctx context.Context, limit, offset int) ([]entity.TopicResponseTo, error) {
	listKey := cache.CacheListKey("topic")
	var cached []entity.TopicResponseTo

	if err := uc.cache.Get(ctx, listKey, &cached); err == nil && len(cached) > 0 {
		if offset >= len(cached) {
			return []entity.TopicResponseTo{}, nil
		}
		end := offset + limit
		if end > len(cached) {
			end = len(cached)
		}
		return cached[offset:end], nil
	}

	topics, err := uc.repo.GetAll(ctx, limit*10, 0)
	if err != nil {
		return nil, err
	}

	res := make([]entity.TopicResponseTo, 0, len(topics))
	for _, t := range topics {
		res = append(res, uc.toDTO(t))
	}

	_ = uc.cache.Set(ctx, listKey, res, cache.ShortExpiration)

	if offset >= len(res) {
		return []entity.TopicResponseTo{}, nil
	}
	end := offset + limit
	if end > len(res) {
		end = len(res)
	}
	return res[offset:end], nil
}

func (uc *TopicUsecase) Update(ctx context.Context, id int64, req entity.TopicRequestTo) (entity.TopicResponseTo, error) {
	if err := uc.validate(req); err != nil {
		return entity.TopicResponseTo{}, err
	}
	existing, err := uc.repo.GetByID(ctx, id)
	if err != nil {
		return entity.TopicResponseTo{}, err
	}
	topic := entity.Topic{
		ID: id, AuthorID: req.AuthorID, Title: req.Title,
		Content: req.Content, MarkerIDs: req.MarkerIDs,
		Created: existing.Created, Modified: time.Now(),
	}
	updated, err := uc.repo.Update(ctx, id, topic)
	if err != nil {
		return entity.TopicResponseTo{}, err
	}

	dto := uc.toDTO(updated)
	cacheKey := cache.CacheKey("topic", id)
	_ = uc.cache.Set(ctx, cacheKey, dto, cache.DefaultExpiration)

	listKey := cache.CacheListKey("topic")
	_ = uc.cache.Delete(ctx, listKey)

	return dto, nil
}

func (uc *TopicUsecase) Delete(ctx context.Context, id int64) error {
	cacheKey := cache.CacheKey("topic", id)
	_ = uc.cache.Delete(ctx, cacheKey)

	listKey := cache.CacheListKey("topic")
	_ = uc.cache.Delete(ctx, listKey)

	return uc.repo.Delete(ctx, id)
}
