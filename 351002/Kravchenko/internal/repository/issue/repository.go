package issue

import (
	"context"
	"database/sql"
	"errors"
	"strings"
	"time"

	sq "github.com/Masterminds/squirrel"

	issuemodel "lab1/internal/model/issue"
	stickermodel "lab1/internal/model/sticker"
)

var psql = sq.StatementBuilder.PlaceholderFormat(sq.Dollar)

type issuePgRepository struct {
	db *sql.DB
}

func NewIssuePgRepository(db *sql.DB) Repository {
	return &issuePgRepository{db: db}
}

func (r *issuePgRepository) Create(ctx context.Context, issue *issuemodel.Issue) (*issuemodel.Issue, error) {
	now := time.Now()
	issue.Created = now
	issue.Modified = now

	query, args, err := psql.Insert("tbl_issue").
		Columns("editor_id", "title", "content", "created", "modified").
		Values(issue.EditorID, issue.Title, issue.Content, issue.Created, issue.Modified).
		Suffix("RETURNING id").
		ToSql()
	if err != nil {
		return nil, err
	}

	err = r.db.QueryRowContext(ctx, query, args...).Scan(&issue.ID)
	if err != nil {
		if strings.Contains(err.Error(), "23505") {
			// Убедись, что ErrTitleTaken существует в пакете issuemodel
			return nil, issuemodel.ErrTitleTaken
		}
		return nil, err
	}
	return issue, nil
}

func (r *issuePgRepository) GetByID(ctx context.Context, id int64) (*issuemodel.Issue, error) {
	query, args, err := psql.Select("id", "editor_id", "title", "content", "created", "modified").
		From("tbl_issue").
		Where(sq.Eq{"id": id}).
		ToSql()
	if err != nil {
		return nil, err
	}

	var issue issuemodel.Issue
	err = r.db.QueryRowContext(ctx, query, args...).Scan(&issue.ID, &issue.EditorID, &issue.Title, &issue.Content, &issue.Created, &issue.Modified)
	if err != nil {
		if errors.Is(err, sql.ErrNoRows) {
			return nil, issuemodel.ErrNotFound
		}
		return nil, err
	}
	return &issue, nil
}

func (r *issuePgRepository) Update(ctx context.Context, issue *issuemodel.Issue) error {
	issue.Modified = time.Now()

	query, args, err := psql.Update("tbl_issue").
		Set("editor_id", issue.EditorID).
		Set("title", issue.Title).
		Set("content", issue.Content).
		Set("modified", issue.Modified).
		Where(sq.Eq{"id": issue.ID}).
		ToSql()
	if err != nil {
		return err
	}

	res, err := r.db.ExecContext(ctx, query, args...)
	if err != nil {
		if strings.Contains(err.Error(), "23505") {
			return issuemodel.ErrTitleTaken
		}
		return err
	}

	rows, err := res.RowsAffected()
	if err != nil {
		return err
	}
	if rows == 0 {
		return issuemodel.ErrNotFound
	}
	return nil
}

func (r *issuePgRepository) Delete(ctx context.Context, id int64) error {
	query, args, err := psql.Delete("tbl_issue").
		Where(sq.Eq{"id": id}).
		ToSql()
	if err != nil {
		return err
	}

	res, err := r.db.ExecContext(ctx, query, args...)
	if err != nil {
		return err
	}

	rows, err := res.RowsAffected()
	if err != nil {
		return err
	}
	if rows == 0 {
		return issuemodel.ErrNotFound
	}
	return nil
}

func (r *issuePgRepository) List(ctx context.Context, limit, offset int) ([]*issuemodel.Issue, error) {
	query, args, err := psql.Select("id", "editor_id", "title", "content", "created", "modified").
		From("tbl_issue").
		OrderBy("id ASC").
		Limit(uint64(limit)).
		Offset(uint64(offset)).
		ToSql()
	if err != nil {
		return nil, err
	}

	rows, err := r.db.QueryContext(ctx, query, args...)
	if err != nil {
		return nil, err
	}
	defer rows.Close()

	var result []*issuemodel.Issue
	for rows.Next() {
		issue := &issuemodel.Issue{}
		if err := rows.Scan(&issue.ID, &issue.EditorID, &issue.Title, &issue.Content, &issue.Created, &issue.Modified); err != nil {
			return nil, err
		}
		result = append(result, issue)
	}
	return result, rows.Err()
}

func (r *issuePgRepository) GetStickers(ctx context.Context, issueID int64) ([]*stickermodel.Sticker, error) {
	query, args, err := psql.Select("s.id", "s.name").
		From("tbl_sticker s").
		Join("tbl_issue_sticker is_st ON s.id = is_st.sticker_id").
		Where(sq.Eq{"is_st.issue_id": issueID}).
		ToSql()
	if err != nil {
		return nil, err
	}

	rows, err := r.db.QueryContext(ctx, query, args...)
	if err != nil {
		return nil, err
	}
	defer rows.Close()

	var stickers []*stickermodel.Sticker
	for rows.Next() {
		s := &stickermodel.Sticker{}
		if err := rows.Scan(&s.ID, &s.Name); err != nil {
			return nil, err
		}
		stickers = append(stickers, s)
	}
	return stickers, rows.Err()
}

func (r *issuePgRepository) SetStickers(ctx context.Context, issueID int64, stickerIDs []int64) error {
	_, err := r.GetByID(ctx, issueID)
	if err != nil {
		return err
	}

	tx, err := r.db.BeginTx(ctx, nil)
	if err != nil {
		return err
	}
	defer tx.Rollback()

	delQuery, delArgs, err := psql.Delete("tbl_issue_sticker").Where(sq.Eq{"issue_id": issueID}).ToSql()
	if err != nil {
		return err
	}
	if _, err := tx.ExecContext(ctx, delQuery, delArgs...); err != nil {
		return err
	}

	if len(stickerIDs) > 0 {
		insertBuilder := psql.Insert("tbl_issue_sticker").Columns("issue_id", "sticker_id")
		for _, sID := range stickerIDs {
			insertBuilder = insertBuilder.Values(issueID, sID)
		}
		insQuery, insArgs, err := insertBuilder.ToSql()
		if err != nil {
			return err
		}
		if _, err := tx.ExecContext(ctx, insQuery, insArgs...); err != nil {
			return err
		}
	}
	return tx.Commit()
}
