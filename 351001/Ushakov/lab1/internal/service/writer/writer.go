package writer

import (
	"context"
	db "github.com/Khmelov/Distcomp/351001/Ushakov/lab1/internal/storage/writer"

	"github.com/Khmelov/Distcomp/351001/Ushakov/lab1/internal/model"
)

type service struct {
	db db.Repo
}

type Service interface {
	Create(ctx context.Context, req model.Writer) (model.Writer, error)
	GetList(ctx context.Context) ([]model.Writer, error)
	Get(ctx context.Context, id int) (model.Writer, error)
	Update(ctx context.Context, req model.Writer) (model.Writer, error)
	Delete(ctx context.Context, id int) error
}

func New(db db.Repo) Service {
	return service{
		db: db,
	}
}

func (s service) Create(ctx context.Context, req model.Writer) (model.Writer, error) {
	return s.db.Create(ctx, req)
}

func (s service) GetList(ctx context.Context) ([]model.Writer, error) {
	return s.db.GetList(ctx)
}

func (s service) Get(ctx context.Context, id int) (model.Writer, error) {
	return s.db.Get(ctx, int64(id))
}

func (s service) Update(ctx context.Context, req model.Writer) (model.Writer, error) {
	return s.db.Update(ctx, req)
}

func (s service) Delete(ctx context.Context, id int) error {
	return s.db.Delete(ctx, int64(id))
}
