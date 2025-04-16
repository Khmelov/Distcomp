package mark

import (
	"context"
	"log"

	"github.com/Khmelov/Distcomp/251003/truhan/lab3/publisher/internal/mapper"
	markModel "github.com/Khmelov/Distcomp/251003/truhan/lab3/publisher/internal/model"
	dbMarkModel "github.com/Khmelov/Distcomp/251003/truhan/lab3/publisher/internal/storage/model"
)

type service struct {
	db MarkDB
}

type MarkDB interface {
	CreateMark(ctx context.Context, mark dbMarkModel.Mark) (dbMarkModel.Mark, error)
	GetMarks(ctx context.Context) ([]dbMarkModel.Mark, error)
	GetMarkByID(ctx context.Context, id int64) (dbMarkModel.Mark, error)
	UpdateMarkByID(ctx context.Context, mark dbMarkModel.Mark) (dbMarkModel.Mark, error)
	DeleteMarkByID(ctx context.Context, id int64) error
}

type MarkService interface {
	CreateMark(ctx context.Context, mark markModel.Mark) (markModel.Mark, error)
	GetMarks(ctx context.Context) ([]markModel.Mark, error)
	GetMarkByID(ctx context.Context, id int64) (markModel.Mark, error)
	UpdateMarkByID(ctx context.Context, mark markModel.Mark) (markModel.Mark, error)
	DeleteMarkByID(ctx context.Context, id int64) error
}

func New(db MarkDB) MarkService {
	return &service{
		db: db,
	}
}

func (s *service) CreateMark(ctx context.Context, mark markModel.Mark) (markModel.Mark, error) {
	m, err := s.db.CreateMark(ctx, mapper.MapMarkToModel(mark))
	if err != nil {
		return markModel.Mark{}, err
	}

	log.Println(m)

	return mapper.MapModelToMark(m), nil
}

func (s *service) GetMarks(ctx context.Context) ([]markModel.Mark, error) {
	var mappedMarks []markModel.Mark

	marks, err := s.db.GetMarks(ctx)
	if err != nil {
		return mappedMarks, err
	}

	for _, m := range marks {
		mappedMarks = append(mappedMarks, mapper.MapModelToMark(m))
	}

	if len(mappedMarks) == 0 {
		return []markModel.Mark{}, nil
	}

	return mappedMarks, nil
}

func (s *service) GetMarkByID(ctx context.Context, id int64) (markModel.Mark, error) {
	mark, err := s.db.GetMarkByID(ctx, id)
	if err != nil {
		return markModel.Mark{}, err
	}

	return mapper.MapModelToMark(mark), nil
}

func (s *service) UpdateMarkByID(ctx context.Context, mark markModel.Mark) (markModel.Mark, error) {
	m, err := s.db.UpdateMarkByID(ctx, mapper.MapMarkToModel(mark))
	if err != nil {
		return markModel.Mark{}, err
	}

	return mapper.MapModelToMark(m), nil
}

func (s *service) DeleteMarkByID(ctx context.Context, id int64) error {
	return s.db.DeleteMarkByID(ctx, id)
}
