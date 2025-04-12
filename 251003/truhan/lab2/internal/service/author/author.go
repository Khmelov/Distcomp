package author

import (
	"context"
	db "github.com/Khmelov/Distcomp/251003/truhan/lab2/internal/storage/author"

	"github.com/Khmelov/Distcomp/251003/truhan/lab2/internal/model"
)

type service struct {
	db db.Repo
}

type Service interface {
	Create(ctx context.Context, req model.Author) (model.Author, error)
	GetList(ctx context.Context) ([]model.Author, error)
	Get(ctx context.Context, id int) (model.Author, error)
	Update(ctx context.Context, req model.Author) (model.Author, error)
	Delete(ctx context.Context, id int) error
}

func New(db db.Repo) Service {
	return service{
		db: db,
	}
}

func (s service) Create(ctx context.Context, req model.Author) (model.Author, error) {
	return s.db.Create(ctx, req)
}

func (s service) GetList(ctx context.Context) ([]model.Author, error) {
	return s.db.GetList(ctx)
}

func (s service) Get(ctx context.Context, id int) (model.Author, error) {
	return s.db.Get(ctx, int64(id))
}

func (s service) Update(ctx context.Context, req model.Author) (model.Author, error) {
	return s.db.Update(ctx, req)
}

func (s service) Delete(ctx context.Context, id int) error {
	return s.db.Delete(ctx, int64(id))
}
