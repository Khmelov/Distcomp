package repository

import (
	"context"

	"github.com/Khmelov/Distcomp/251004/Sazonov/internal/config"
	"github.com/Khmelov/Distcomp/251004/Sazonov/internal/repository/psql/label"
	"github.com/Khmelov/Distcomp/251004/Sazonov/internal/repository/psql/news"
	"github.com/Khmelov/Distcomp/251004/Sazonov/internal/repository/psql/notice"
	"github.com/Khmelov/Distcomp/251004/Sazonov/internal/repository/psql/writer"
	noticecache "github.com/Khmelov/Distcomp/251004/Sazonov/internal/repository/redis/notice"
	"github.com/Khmelov/Distcomp/251004/Sazonov/pkg/postgres"
	redispkg "github.com/Khmelov/Distcomp/251004/Sazonov/pkg/redis"
	"github.com/jmoiron/sqlx"
	"github.com/redis/go-redis/v9"
)

type repository struct {
	db     *sqlx.DB
	client *redis.Client

	NoticeCache
	WriterRepo
	NewsRepo
	NoticeRepo
	LabelRepo
}

func New(storageCfg config.StorageConfig, redisCfg config.RedisConfig) (Repository, error) {
	db, err := postgres.Connect(
		context.Background(),
		postgres.Config{
			User:     storageCfg.User,
			Password: storageCfg.Password,
			Host:     storageCfg.Host,
			Port:     storageCfg.Port,
			DBName:   storageCfg.DBName,
			SSLMode:  storageCfg.SSLMode,
		},
	)
	if err != nil {
		return nil, err
	}

	client, err := redispkg.Connect(
		context.TODO(),
		redispkg.Config{
			Addr:     redisCfg.Addr,
			User:     redisCfg.User,
			Password: redisCfg.Password,
			DB:       redisCfg.DB,
		},
	)
	if err != nil {
		return nil, err
	}

	repo := &repository{
		db:     db,
		client: client,

		NoticeCache: noticecache.New(client),
		WriterRepo:  writer.New(db),
		NewsRepo:    news.New(db),
		NoticeRepo:  notice.New(db),
		LabelRepo:   label.New(db),
	}

	return repo, nil
}

func (r *repository) Close() {
	if r.db != nil {
		r.db.Close()
	}

	if r.client != nil {
		r.client.Close()
	}
}
