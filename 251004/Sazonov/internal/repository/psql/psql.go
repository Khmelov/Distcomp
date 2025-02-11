package psql

import (
	"github.com/Khmelov/Distcomp/251004/Sazonov/internal/repository/psql/label"
	"github.com/Khmelov/Distcomp/251004/Sazonov/internal/repository/psql/news"
	"github.com/Khmelov/Distcomp/251004/Sazonov/internal/repository/psql/notice"
	"github.com/Khmelov/Distcomp/251004/Sazonov/internal/repository/psql/writer"
	"github.com/jmoiron/sqlx"
)

type psqlRepo struct {
	writer *writer.WriterRepo
	news   *news.NewsRepo
	notice *notice.NoticeRepo
	label  *label.LabelRepo
}

func New(db *sqlx.DB) *psqlRepo {
	return &psqlRepo{
		writer: writer.New(db),
		news:   news.New(db),
		notice: notice.New(db),
		label:  label.New(db),
	}
}

func (s *psqlRepo) Writer() *writer.WriterRepo {
	return s.writer
}

func (s *psqlRepo) News() *news.NewsRepo {
	return s.news
}

func (s *psqlRepo) Notice() *notice.NoticeRepo {
	return s.notice
}

func (s *psqlRepo) Label() *label.LabelRepo {
	return s.label
}
