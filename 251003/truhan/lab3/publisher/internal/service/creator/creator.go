package creator

import (
	"context"

	"github.com/Khmelov/Distcomp/251003/truhan/lab3/publisher/internal/mapper"
	creator "github.com/Khmelov/Distcomp/251003/truhan/lab3/publisher/internal/model"
	creatorModel "github.com/Khmelov/Distcomp/251003/truhan/lab3/publisher/internal/model"
	dbCreatorModel "github.com/Khmelov/Distcomp/251003/truhan/lab3/publisher/internal/storage/model"
)

type service struct {
	db CreatorDB
}

type CreatorDB interface {
	CreateCreator(ctx context.Context, cr dbCreatorModel.Creator) (dbCreatorModel.Creator, error)
	GetCreators(ctx context.Context) ([]dbCreatorModel.Creator, error)
	GetCreatorByID(ctx context.Context, id int64) (dbCreatorModel.Creator, error)
	UpdateCreatorByID(ctx context.Context, cr dbCreatorModel.Creator) (dbCreatorModel.Creator, error)
	DeleteCreatorByID(ctx context.Context, id int64) error
}

type CreatorService interface {
	CreateCreator(ctx context.Context, cr creatorModel.Creator) (creator.Creator, error)
	GetCreators(ctx context.Context) ([]creatorModel.Creator, error)
	GetCreatorByID(ctx context.Context, id int) (creatorModel.Creator, error)
	UpdateCreatorByID(ctx context.Context, cr creatorModel.Creator) (creatorModel.Creator, error)
	DeleteCreatorByID(ctx context.Context, id int) error
}

func New(db CreatorDB) CreatorService {
	return &service{
		db: db,
	}
}

func (s *service) CreateCreator(ctx context.Context, cr creatorModel.Creator) (creator.Creator, error) {
	mappedData, err := s.db.CreateCreator(ctx, mapper.MapCreatorToModel(cr))
	if err != nil {
		return creator.Creator{}, err
	}

	return mapper.MapModelToCreator(mappedData), nil
}

func (s *service) GetCreators(ctx context.Context) ([]creatorModel.Creator, error) {
	creators, err := s.db.GetCreators(ctx)
	if err != nil {
		return nil, err
	}

	var mappedCreators []creatorModel.Creator

	for _, creator := range creators {
		mappedCreators = append(mappedCreators, mapper.MapModelToCreator(creator))
	}

	return mappedCreators, nil
}

func (s *service) GetCreatorByID(ctx context.Context, id int) (creatorModel.Creator, error) {
	creator, err := s.db.GetCreatorByID(ctx, int64(id))
	if err != nil {
		return creatorModel.Creator{}, err
	}

	return mapper.MapModelToCreator(creator), nil
}

func (s *service) UpdateCreatorByID(ctx context.Context, cr creatorModel.Creator) (creatorModel.Creator, error) {
	creator, err := s.db.UpdateCreatorByID(ctx, mapper.MapCreatorToModel(cr))
	if err != nil {
		return creatorModel.Creator{}, err
	}

	return mapper.MapModelToCreator(creator), nil
}

func (s *service) DeleteCreatorByID(ctx context.Context, id int) error {
	return s.db.DeleteCreatorByID(ctx, int64(id))
}
