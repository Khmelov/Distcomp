package repository

import (
	"context"
	"errors"

	"github.com/jackc/pgx/v5"
	"github.com/jackc/pgx/v5/pgxpool"

	"note-service/internal/entity"
)

// AuthorRepository расширяет CRUD автора методом поиска по login (аутентификация).
type AuthorRepository interface {
	NoteRepository[entity.Author]
	GetByLogin(ctx context.Context, login string) (entity.Author, error)
}

type pgAuthorRepo struct {
	db *pgxpool.Pool
}

func NewAuthorRepository(db *pgxpool.Pool) AuthorRepository {
	return &pgAuthorRepo{db: db}
}

func (r *pgAuthorRepo) Create(ctx context.Context, a entity.Author) (entity.Author, error) {
	query := `INSERT INTO tbl_author (login, password, firstname, lastname, role) VALUES ($1, $2, $3, $4, $5) RETURNING id`
	err := r.db.QueryRow(ctx, query, a.Login, a.Password, a.Firstname, a.Lastname, a.Role).Scan(&a.ID)
	return a, err
}

func (r *pgAuthorRepo) GetByID(ctx context.Context, id int64) (entity.Author, error) {
	var a entity.Author
	query := `SELECT id, login, password, firstname, lastname, role FROM tbl_author WHERE id = $1`
	err := r.db.QueryRow(ctx, query, id).Scan(&a.ID, &a.Login, &a.Password, &a.Firstname, &a.Lastname, &a.Role)
	if errors.Is(err, pgx.ErrNoRows) {
		return a, entity.NewErr(404, "01", "Author not found")
	}
	return a, err
}

func (r *pgAuthorRepo) GetByLogin(ctx context.Context, login string) (entity.Author, error) {
	var a entity.Author
	query := `SELECT id, login, password, firstname, lastname, role FROM tbl_author WHERE login = $1`
	err := r.db.QueryRow(ctx, query, login).Scan(&a.ID, &a.Login, &a.Password, &a.Firstname, &a.Lastname, &a.Role)
	if errors.Is(err, pgx.ErrNoRows) {
		return a, entity.NewErr(404, "01", "Author not found")
	}
	return a, err
}

func (r *pgAuthorRepo) Update(ctx context.Context, id int64, a entity.Author) (entity.Author, error) {
	query := `UPDATE tbl_author SET login=$1, password=$2, firstname=$3, lastname=$4, role=$5 WHERE id=$6 RETURNING id`
	err := r.db.QueryRow(ctx, query, a.Login, a.Password, a.Firstname, a.Lastname, a.Role, id).Scan(&a.ID)
	if errors.Is(err, pgx.ErrNoRows) {
		return a, entity.NewErr(404, "01", "Author not found")
	}
	return a, err
}

func (r *pgAuthorRepo) Delete(ctx context.Context, id int64) error {
	tag, err := r.db.Exec(ctx, `DELETE FROM tbl_author WHERE id = $1`, id)
	if tag.RowsAffected() == 0 {
		return entity.NewErr(404, "01", "Author not found")
	}
	return err
}

func (r *pgAuthorRepo) GetAll(ctx context.Context, limit, offset int) ([]entity.Author, error) {
	query := `SELECT id, login, password, firstname, lastname, role FROM tbl_author ORDER BY id LIMIT $1 OFFSET $2`
	rows, err := r.db.Query(ctx, query, limit, offset)
	if err != nil {
		return nil, err
	}
	defer rows.Close()

	res := make([]entity.Author, 0)
	for rows.Next() {
		var a entity.Author
		if err := rows.Scan(&a.ID, &a.Login, &a.Password, &a.Firstname, &a.Lastname, &a.Role); err != nil {
			return nil, err
		}
		res = append(res, a)
	}
	return res, nil
}
