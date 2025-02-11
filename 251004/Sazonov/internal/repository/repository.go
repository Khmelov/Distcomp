package repository

import (
	"context"

	"github.com/Khmelov/Distcomp/251004/Sazonov/internal/config"
	"github.com/Khmelov/Distcomp/251004/Sazonov/internal/repository/psql"
	"github.com/Khmelov/Distcomp/251004/Sazonov/pkg/postgres"
	"github.com/jmoiron/sqlx"
)

const (
	MemoryStorageType string = "memory"

	PostgresStorageType string = "postgres"
)

type Repository struct {
	db *sqlx.DB

	WriterRepo
	NewsRepo
	NoticeRepo
	LabelRepo
}

func New(cfg config.StorageConfig) (*Repository, error) {
	var repo Repository

	switch cfg.Type {
	case MemoryStorageType:
	case PostgresStorageType:
		db, err := postgres.Connect(
			context.Background(),
			postgres.Config{
				User:     cfg.User,
				Password: cfg.Password,
				Host:     cfg.Host,
				Port:     cfg.Port,
				DBName:   cfg.DBName,
				SSLMode:  cfg.SSLMode,
			},
		)
		if err != nil {
			return nil, err
		}

		psqlRepo := psql.New(db)

		repo = Repository{
			db: db,

			WriterRepo: psqlRepo.Writer(),
			NewsRepo:   psqlRepo.News(),
			NoticeRepo: psqlRepo.Notice(),
			LabelRepo:  psqlRepo.Label(),
		}
	}

	return &repo, nil
}

func (r *Repository) Close() {
	if r.db != nil {
		r.db.Close()
	}
}
