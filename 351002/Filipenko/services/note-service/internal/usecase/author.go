package usecase

import (
	"context"
	"errors"
	"strings"
	"unicode/utf8"

	"golang.org/x/crypto/bcrypt"

	"note-service/internal/entity"
	"note-service/internal/pkg/cache"
	"note-service/internal/repository"
)

type AuthorUsecase struct {
	repo  repository.AuthorRepository
	cache cache.Cache
}

func NewAuthorUsecase(repo repository.AuthorRepository, cache cache.Cache) *AuthorUsecase {
	return &AuthorUsecase{repo: repo, cache: cache}
}

func hashPassword(plain string) (string, error) {
	b, err := bcrypt.GenerateFromPassword([]byte(plain), bcrypt.DefaultCost)
	if err != nil {
		return "", err
	}
	return string(b), nil
}

func (uc *AuthorUsecase) validate(req entity.AuthorRequestTo) error {
	if l := utf8.RuneCountInString(req.Login); l < 2 || l > 64 {
		return entity.NewErr(400, "01", "login must be 2..64 chars")
	}
	if l := utf8.RuneCountInString(req.Password); l < 8 || l > 128 {
		return entity.NewErr(400, "02", "password must be 8..128 chars")
	}
	if l := utf8.RuneCountInString(req.Firstname); l < 2 || l > 64 {
		return entity.NewErr(400, "03", "firstname must be 2..64 chars")
	}
	if l := utf8.RuneCountInString(req.Lastname); l < 2 || l > 64 {
		return entity.NewErr(400, "04", "lastname must be 2..64 chars")
	}
	return nil
}

func (uc *AuthorUsecase) toDTO(a entity.Author) entity.AuthorResponseTo {
	return entity.AuthorResponseTo{
		ID:        a.ID,
		Login:     a.Login,
		Firstname: a.Firstname,
		Lastname:  a.Lastname,
		Role:      a.Role,
	}
}

func (uc *AuthorUsecase) Register(ctx context.Context, req entity.AuthorRegisterRequestTo) (entity.AuthorResponseTo, error) {
	login := strings.TrimSpace(req.Login)
	roleInput := strings.TrimSpace(req.Role)

	existing, err := uc.repo.GetByLogin(ctx, login)
	isNew := false
	if err != nil {
		var ce *entity.CustomError
		if errors.As(err, &ce) && ce.Status == 404 {
			isNew = true
		} else {
			return entity.AuthorResponseTo{}, err
		}
	}

	var role string
	switch {
	case isNew:
		if roleInput == "" {
			role = entity.RoleCustomer
		} else {
			role = roleInput
		}
	default:
		if roleInput == "" {
			role = existing.Role
		} else {
			role = roleInput
		}
	}

	if !entity.ValidRole(role) {
		return entity.AuthorResponseTo{}, entity.NewErr(400, "05", "invalid role")
	}

	ar := entity.AuthorRequestTo{
		Login:     req.Login,
		Password:  req.Password,
		Firstname: strings.TrimSpace(req.FirstName),
		Lastname:  strings.TrimSpace(req.LastName),
		Role:      role,
	}
	if err := uc.validate(ar); err != nil {
		return entity.AuthorResponseTo{}, err
	}

	hashed, err := hashPassword(req.Password)
	if err != nil {
		return entity.AuthorResponseTo{}, entity.NewErr(500, "00", "failed to hash password")
	}

	if !isNew {
		author := entity.Author{
			ID:        existing.ID,
			Login:     login,
			Password:  hashed,
			Firstname: ar.Firstname,
			Lastname:  ar.Lastname,
			Role:      role,
		}
		updated, err := uc.repo.Update(ctx, existing.ID, author)
		if err != nil {
			return entity.AuthorResponseTo{}, err
		}
		dto := uc.toDTO(updated)
		_ = uc.cache.Set(ctx, cache.CacheKey("author", updated.ID), dto, cache.DefaultExpiration)
		_ = uc.cache.Delete(ctx, cache.CacheListKey("author"))
		return dto, nil
	}

	author := entity.Author{
		Login:     login,
		Password:  hashed,
		Firstname: ar.Firstname,
		Lastname:  ar.Lastname,
		Role:      role,
	}
	saved, err := uc.repo.Create(ctx, author)
	if err != nil {
		return entity.AuthorResponseTo{}, err
	}

	dto := uc.toDTO(saved)
	_ = uc.cache.Set(ctx, cache.CacheKey("author", saved.ID), dto, cache.DefaultExpiration)
	_ = uc.cache.Delete(ctx, cache.CacheListKey("author"))

	return dto, nil
}

func (uc *AuthorUsecase) Login(ctx context.Context, req entity.LoginRequestTo) (entity.Author, error) {
	login := strings.TrimSpace(req.Login)
	if login == "" || req.Password == "" {
		return entity.Author{}, entity.NewErr(400, "00", "login and password are required")
	}

	a, err := uc.repo.GetByLogin(ctx, login)
	if err != nil {
		return entity.Author{}, entity.NewErr(401, "01", "invalid login or password")
	}

	if err := bcrypt.CompareHashAndPassword([]byte(a.Password), []byte(req.Password)); err != nil {
		return entity.Author{}, entity.NewErr(401, "01", "invalid login or password")
	}

	return a, nil
}

func (uc *AuthorUsecase) Create(ctx context.Context, req entity.AuthorRequestTo) (entity.AuthorResponseTo, error) {
	if err := uc.validate(req); err != nil {
		return entity.AuthorResponseTo{}, err
	}

	role := strings.TrimSpace(req.Role)
	if role == "" {
		role = entity.RoleCustomer
	} else if !entity.ValidRole(role) {
		return entity.AuthorResponseTo{}, entity.NewErr(400, "05", "invalid role")
	}

	hashed, err := hashPassword(req.Password)
	if err != nil {
		return entity.AuthorResponseTo{}, entity.NewErr(500, "00", "failed to hash password")
	}

	author := entity.Author{
		Login:     strings.TrimSpace(req.Login),
		Password:  hashed,
		Firstname: req.Firstname,
		Lastname:  req.Lastname,
		Role:      role,
	}

	saved, err := uc.repo.Create(ctx, author)
	if err != nil {
		return entity.AuthorResponseTo{}, err
	}

	dto := uc.toDTO(saved)
	cacheKey := cache.CacheKey("author", saved.ID)
	_ = uc.cache.Set(ctx, cacheKey, dto, cache.DefaultExpiration)

	listKey := cache.CacheListKey("author")
	_ = uc.cache.Delete(ctx, listKey)

	return dto, nil
}

func (uc *AuthorUsecase) GetByID(ctx context.Context, id int64) (entity.AuthorResponseTo, error) {
	cacheKey := cache.CacheKey("author", id)
	var cached entity.AuthorResponseTo

	if err := uc.cache.Get(ctx, cacheKey, &cached); err == nil {
		return cached, nil
	}

	a, err := uc.repo.GetByID(ctx, id)
	if err != nil {
		return entity.AuthorResponseTo{}, err
	}

	dto := uc.toDTO(a)
	_ = uc.cache.Set(ctx, cacheKey, dto, cache.DefaultExpiration)

	return dto, nil
}

func (uc *AuthorUsecase) GetAll(ctx context.Context, limit, offset int) ([]entity.AuthorResponseTo, error) {
	listKey := cache.CacheListKey("author")
	var cached []entity.AuthorResponseTo

	if err := uc.cache.Get(ctx, listKey, &cached); err == nil && len(cached) > 0 {
		if offset >= len(cached) {
			return []entity.AuthorResponseTo{}, nil
		}
		end := offset + limit
		if end > len(cached) {
			end = len(cached)
		}
		return cached[offset:end], nil
	}

	authors, err := uc.repo.GetAll(ctx, limit*10, 0)
	if err != nil {
		return nil, err
	}

	res := make([]entity.AuthorResponseTo, 0, len(authors))
	for _, a := range authors {
		res = append(res, uc.toDTO(a))
	}

	_ = uc.cache.Set(ctx, listKey, res, cache.ShortExpiration)

	if offset >= len(res) {
		return []entity.AuthorResponseTo{}, nil
	}
	end := offset + limit
	if end > len(res) {
		end = len(res)
	}
	return res[offset:end], nil
}

func (uc *AuthorUsecase) Update(ctx context.Context, id int64, req entity.AuthorRequestTo) (entity.AuthorResponseTo, error) {
	if err := uc.validate(req); err != nil {
		return entity.AuthorResponseTo{}, err
	}

	existing, err := uc.repo.GetByID(ctx, id)
	if err != nil {
		return entity.AuthorResponseTo{}, err
	}

	role := existing.Role
	if trimmed := strings.TrimSpace(req.Role); trimmed != "" {
		if !entity.ValidRole(trimmed) {
			return entity.AuthorResponseTo{}, entity.NewErr(400, "05", "invalid role")
		}
		role = trimmed
	}

	hashed, err := hashPassword(req.Password)
	if err != nil {
		return entity.AuthorResponseTo{}, entity.NewErr(500, "00", "failed to hash password")
	}

	author := entity.Author{
		ID:        id,
		Login:     strings.TrimSpace(req.Login),
		Password:  hashed,
		Firstname: req.Firstname,
		Lastname:  req.Lastname,
		Role:      role,
	}

	updated, err := uc.repo.Update(ctx, id, author)
	if err != nil {
		return entity.AuthorResponseTo{}, err
	}

	dto := uc.toDTO(updated)
	cacheKey := cache.CacheKey("author", id)
	_ = uc.cache.Set(ctx, cacheKey, dto, cache.DefaultExpiration)

	listKey := cache.CacheListKey("author")
	_ = uc.cache.Delete(ctx, listKey)

	return dto, nil
}

func (uc *AuthorUsecase) Delete(ctx context.Context, id int64) error {
	cacheKey := cache.CacheKey("author", id)
	_ = uc.cache.Delete(ctx, cacheKey)

	listKey := cache.CacheListKey("author")
	_ = uc.cache.Delete(ctx, listKey)

	return uc.repo.Delete(ctx, id)
}
