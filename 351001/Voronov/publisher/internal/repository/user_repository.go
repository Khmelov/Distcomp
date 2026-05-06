package repository

import (
	"context"
	"errors"
	"fmt"

	apperrors "publisher/internal/errors"
	"publisher/internal/model"

	"github.com/jackc/pgx/v5"
	"github.com/jackc/pgx/v5/pgconn"
	"github.com/jackc/pgx/v5/pgxpool"
)

type pgUserRepository struct {
	pool *pgxpool.Pool
}

func NewUserRepository(pool *pgxpool.Pool) UserRepository {
	return &pgUserRepository{pool: pool}
}

func (r *pgUserRepository) FindByID(ctx context.Context, id int64) (*model.User, error) {
	query := "SELECT id, login, password, firstname, lastname, COALESCE(role,'CUSTOMER') FROM distcomp.tbl_user WHERE id = $1"
	var u model.User
	var role string
	err := r.pool.QueryRow(ctx, query, id).Scan(&u.ID, &u.Login, &u.Password, &u.Firstname, &u.Lastname, &role)
	if errors.Is(err, pgx.ErrNoRows) {
		return nil, apperrors.ErrNotFound
	}
	if err != nil {
		return nil, err
	}
	u.Role = model.UserRole(role)
	return &u, nil
}

func (r *pgUserRepository) FindByLogin(ctx context.Context, login string) (*model.User, error) {
	query := "SELECT id, login, password, firstname, lastname, COALESCE(role,'CUSTOMER') FROM distcomp.tbl_user WHERE login = $1"
	var u model.User
	var role string
	err := r.pool.QueryRow(ctx, query, login).Scan(&u.ID, &u.Login, &u.Password, &u.Firstname, &u.Lastname, &role)
	if errors.Is(err, pgx.ErrNoRows) {
		return nil, apperrors.ErrNotFound
	}
	if err != nil {
		return nil, err
	}
	u.Role = model.UserRole(role)
	return &u, nil
}

func (r *pgUserRepository) FindAll(ctx context.Context, opts *QueryOptions) ([]*model.User, int64, error) {
	if opts == nil {
		opts = NewQueryOptions()
	}

	var total int64
	if err := r.pool.QueryRow(ctx, "SELECT COUNT(*) FROM distcomp.tbl_user").Scan(&total); err != nil {
		return nil, 0, err
	}

	orderField, orderDir := sortParams(opts.Sort)
	offset := (opts.Pagination.Page - 1) * opts.Pagination.PageSize
	query := fmt.Sprintf(
		"SELECT id, login, password, firstname, lastname, COALESCE(role,'CUSTOMER') FROM distcomp.tbl_user ORDER BY %s %s LIMIT $1 OFFSET $2",
		orderField, orderDir,
	)

	rows, err := r.pool.Query(ctx, query, opts.Pagination.PageSize, offset)
	if err != nil {
		return nil, 0, err
	}
	defer rows.Close()

	items := make([]*model.User, 0)
	for rows.Next() {
		var u model.User
		var role string
		if err := rows.Scan(&u.ID, &u.Login, &u.Password, &u.Firstname, &u.Lastname, &role); err != nil {
			return nil, 0, err
		}
		u.Role = model.UserRole(role)
		items = append(items, &u)
	}
	return items, total, nil
}

func (r *pgUserRepository) Create(ctx context.Context, user *model.User) (*model.User, error) {
	role := string(user.Role)
	if role == "" {
		role = string(model.RoleCustomer)
	}
	query := "INSERT INTO distcomp.tbl_user (login, password, firstname, lastname, role) VALUES ($1, $2, $3, $4, $5) RETURNING id"
	var id int64
	err := r.pool.QueryRow(ctx, query, user.Login, user.Password, user.Firstname, user.Lastname, role).Scan(&id)
	if err != nil {
		var pgErr *pgconn.PgError
		if errors.As(err, &pgErr) && pgErr.Code == "23505" {
			return nil, apperrors.ErrDuplicate
		}
		return nil, apperrors.FromDBError(err)
	}
	user.ID = id
	user.Role = model.UserRole(role)
	return user, nil
}

func (r *pgUserRepository) Update(ctx context.Context, id int64, user *model.User) (*model.User, error) {
	role := string(user.Role)
	if role == "" {
		role = string(model.RoleCustomer)
	}
	query := "UPDATE distcomp.tbl_user SET login = $1, password = $2, firstname = $3, lastname = $4, role = $5 WHERE id = $6"
	result, err := r.pool.Exec(ctx, query, user.Login, user.Password, user.Firstname, user.Lastname, role, id)
	if err != nil {
		var pgErr *pgconn.PgError
		if errors.As(err, &pgErr) && pgErr.Code == "23505" {
			return nil, apperrors.ErrDuplicate
		}
		return nil, apperrors.FromDBError(err)
	}
	if result.RowsAffected() == 0 {
		return nil, apperrors.ErrNotFound
	}
	return r.FindByID(ctx, id)
}

func (r *pgUserRepository) Delete(ctx context.Context, id int64) error {
	result, err := r.pool.Exec(ctx, "DELETE FROM distcomp.tbl_user WHERE id = $1", id)
	if err != nil {
		return apperrors.FromDBError(err)
	}
	if result.RowsAffected() == 0 {
		return apperrors.ErrNotFound
	}
	return nil
}
