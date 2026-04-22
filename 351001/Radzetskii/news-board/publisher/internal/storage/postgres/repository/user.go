package repository

import (
	"context"
	"errors"
	"news-board/publisher/internal/domain/models"

	sq "github.com/Masterminds/squirrel"
	"github.com/jackc/pgx/v5"
	"github.com/jackc/pgx/v5/pgxpool"
)

type userRepo struct {
	db   *pgxpool.Pool
	psql sq.StatementBuilderType
}

func NewUserRepository(db *pgxpool.Pool) *userRepo {
	return &userRepo{
		db:   db,
		psql: sq.StatementBuilder.PlaceholderFormat(sq.Dollar),
	}
}

func (r *userRepo) Create(ctx context.Context, user *models.User) error {
	query, args, err := r.psql.
		Insert("tbl_user").
		Columns("login", "password", "firstname", "lastname").
		Values(user.Login, user.Password, user.Firstname, user.Lastname).
		Suffix("RETURNING id").
		ToSql()
	if err != nil {
		return err
	}
	return r.db.QueryRow(ctx, query, args...).Scan(&user.ID)
}

func (r *userRepo) GetByID(ctx context.Context, id int64) (*models.User, error) {
	query, args, err := r.psql.
		Select("id", "login", "password", "firstname", "lastname").
		From("tbl_user").
		Where(sq.Eq{"id": id}).
		ToSql()
	if err != nil {
		return nil, err
	}
	row := r.db.QueryRow(ctx, query, args...)
	user := &models.User{}
	err = row.Scan(&user.ID, &user.Login, &user.Password, &user.Firstname, &user.Lastname)
	if errors.Is(err, pgx.ErrNoRows) {
		return nil, nil
	}
	return user, err
}

func (r *userRepo) GetByLogin(ctx context.Context, login string) (*models.User, error) {
	query, args, err := r.psql.
		Select("id", "login", "password", "firstname", "lastname").
		From("tbl_user").
		Where(sq.Eq{"login": login}).
		ToSql()
	if err != nil {
		return nil, err
	}
	row := r.db.QueryRow(ctx, query, args...)
	user := &models.User{}
	err = row.Scan(&user.ID, &user.Login, &user.Password, &user.Firstname, &user.Lastname)
	if errors.Is(err, pgx.ErrNoRows) {
		return nil, nil
	}
	return user, err
}

func (r *userRepo) GetAll(ctx context.Context, limit, offset int) ([]models.User, error) {
	query, args, err := r.psql.
		Select("id", "login", "password", "firstname", "lastname").
		From("tbl_user").
		OrderBy("id").
		Limit(uint64(limit)).
		Offset(uint64(offset)).
		ToSql()
	if err != nil {
		return nil, err
	}
	rows, err := r.db.Query(ctx, query, args...)
	if err != nil {
		return nil, err
	}
	defer rows.Close()
	var users []models.User
	for rows.Next() {
		var u models.User
		if err := rows.Scan(&u.ID, &u.Login, &u.Password, &u.Firstname, &u.Lastname); err != nil {
			return nil, err
		}
		users = append(users, u)
	}
	return users, rows.Err()
}

func (r *userRepo) Update(ctx context.Context, user *models.User) (bool, error) {
	query, args, err := r.psql.
		Update("tbl_user").
		Set("login", user.Login).
		Set("password", user.Password).
		Set("firstname", user.Firstname).
		Set("lastname", user.Lastname).
		Where(sq.Eq{"id": user.ID}).
		ToSql()
	if err != nil {
		return false, err
	}
	tag, err := r.db.Exec(ctx, query, args...)
	if err != nil {
		return false, err
	}
	return tag.RowsAffected() > 0, nil
}

func (r *userRepo) Delete(ctx context.Context, id int64) (bool, error) {
	query, args, err := r.psql.
		Delete("tbl_user").
		Where(sq.Eq{"id": id}).
		ToSql()
	if err != nil {
		return false, err
	}
	tag, err := r.db.Exec(ctx, query, args...)
	if err != nil {
		return false, err
	}
	return tag.RowsAffected() > 0, nil
}

func (r *userRepo) GetByNewsID(ctx context.Context, newsID int64) (*models.User, error) {
	query, args, err := r.psql.
		Select("u.id", "u.login", "u.password", "u.firstname", "u.lastname").
		From("tbl_user u").
		Join("tbl_news n ON n.user_id = u.id").
		Where(sq.Eq{"n.id": newsID}).
		ToSql()
	if err != nil {
		return nil, err
	}
	row := r.db.QueryRow(ctx, query, args...)
	user := &models.User{}
	err = row.Scan(&user.ID, &user.Login, &user.Password, &user.Firstname, &user.Lastname)
	if errors.Is(err, pgx.ErrNoRows) {
		return nil, nil
	}
	return user, err
}
