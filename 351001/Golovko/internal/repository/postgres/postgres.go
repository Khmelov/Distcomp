package postgres

import (
	"context"
	"database/sql"
	"errors"
	"fmt"

	"distcomp/internal/domain"
	"distcomp/internal/repository"

	_ "github.com/lib/pq"
)

var ErrNotFound = errors.New("entity not found")

type store struct {
	db *sql.DB
}

func NewStorage(db *sql.DB) repository.Storage {
	return &store{db: db}
}

func applyPagination(query string, params repository.ListParams) string {
	if params.SortBy != "" {
		order := "ASC"
		if params.Order == "desc" || params.Order == "DESC" {
			order = "DESC"
		}
		query = fmt.Sprintf("%s ORDER BY %s %s", query, params.SortBy, order)
	} else {
		query = fmt.Sprintf("%s ORDER BY id ASC", query)
	}

	if params.Limit > 0 {
		query = fmt.Sprintf("%s LIMIT %d", query, params.Limit)
	}
	if params.Offset > 0 {
		query = fmt.Sprintf("%s OFFSET %d", query, params.Offset)
	}
	return query
}

// Очистка тегов, на которые больше нет ссылок (orphan removal)
func (s *store) cleanOrphanedTags(ctx context.Context) {
	_, _ = s.db.ExecContext(ctx, `DELETE FROM distcomp.tbl_tag WHERE id NOT IN (SELECT tag_id FROM distcomp.tbl_article_tag)`)
}

// --- EDITOR ---

func (s *store) CreateEditor(ctx context.Context, editor *domain.Editor) error {
	query := `INSERT INTO distcomp.tbl_editor (login, password, firstname, lastname) VALUES ($1, $2, $3, $4) RETURNING id`
	return s.db.QueryRowContext(ctx, query, editor.Login, editor.Password, editor.FirstName, editor.LastName).Scan(&editor.ID)
}

func (s *store) GetEditorByID(ctx context.Context, id int64) (*domain.Editor, error) {
	query := `SELECT id, login, password, firstname, lastname FROM distcomp.tbl_editor WHERE id = $1`
	var e domain.Editor
	err := s.db.QueryRowContext(ctx, query, id).Scan(&e.ID, &e.Login, &e.Password, &e.FirstName, &e.LastName)
	if errors.Is(err, sql.ErrNoRows) {
		return nil, ErrNotFound
	}
	return &e, err
}

func (s *store) GetAllEditors(ctx context.Context, params repository.ListParams) ([]*domain.Editor, error) {
	query := applyPagination(`SELECT id, login, password, firstname, lastname FROM distcomp.tbl_editor`, params)
	rows, err := s.db.QueryContext(ctx, query)
	if err != nil {
		return nil, err
	}
	defer rows.Close()

	var editors []*domain.Editor
	for rows.Next() {
		var e domain.Editor
		if err := rows.Scan(&e.ID, &e.Login, &e.Password, &e.FirstName, &e.LastName); err != nil {
			return nil, err
		}
		editors = append(editors, &e)
	}
	return editors, nil
}

func (s *store) UpdateEditor(ctx context.Context, editor *domain.Editor) error {
	query := `UPDATE distcomp.tbl_editor SET login = $1, password = $2, firstname = $3, lastname = $4 WHERE id = $5`
	res, err := s.db.ExecContext(ctx, query, editor.Login, editor.Password, editor.FirstName, editor.LastName, editor.ID)
	if err != nil {
		return err
	}
	rows, err := res.RowsAffected()
	if err != nil || rows == 0 {
		return ErrNotFound
	}
	return nil
}

func (s *store) DeleteEditor(ctx context.Context, id int64) error {
	query := `DELETE FROM distcomp.tbl_editor WHERE id = $1`
	res, err := s.db.ExecContext(ctx, query, id)
	if err != nil {
		return err
	}
	rows, err := res.RowsAffected()
	if err != nil || rows == 0 {
		return ErrNotFound
	}

	// Вычищаем сирот
	s.cleanOrphanedTags(ctx)

	return nil
}

// --- ARTICLE ---

func (s *store) CreateArticle(ctx context.Context, article *domain.Article) error {
	tx, err := s.db.BeginTx(ctx, nil)
	if err != nil {
		return err
	}
	defer tx.Rollback()

	query := `INSERT INTO distcomp.tbl_article (editor_id, title, content, created, modified) VALUES ($1, $2, $3, $4, $5) RETURNING id`
	err = tx.QueryRowContext(ctx, query, article.EditorID, article.Title, article.Content, article.Created, article.Modified).Scan(&article.ID)
	if err != nil {
		return err
	}

	for i, t := range article.Tags {
		tagQuery := `INSERT INTO distcomp.tbl_tag (name) VALUES ($1) ON CONFLICT (name) DO UPDATE SET name = EXCLUDED.name RETURNING id`
		if err = tx.QueryRowContext(ctx, tagQuery, t.Name).Scan(&article.Tags[i].ID); err != nil {
			return err
		}
		linkQuery := `INSERT INTO distcomp.tbl_article_tag (article_id, tag_id) VALUES ($1, $2)`
		if _, err = tx.ExecContext(ctx, linkQuery, article.ID, article.Tags[i].ID); err != nil {
			return err
		}
	}

	return tx.Commit()
}

func (s *store) GetArticleByID(ctx context.Context, id int64) (*domain.Article, error) {
	query := `SELECT id, editor_id, title, content, created, modified FROM distcomp.tbl_article WHERE id = $1`
	var a domain.Article
	err := s.db.QueryRowContext(ctx, query, id).Scan(&a.ID, &a.EditorID, &a.Title, &a.Content, &a.Created, &a.Modified)
	if errors.Is(err, sql.ErrNoRows) {
		return nil, ErrNotFound
	} else if err != nil {
		return nil, err
	}

	tagQuery := `
		SELECT t.id, t.name 
		FROM distcomp.tbl_tag t 
		JOIN distcomp.tbl_article_tag at ON t.id = at.tag_id 
		WHERE at.article_id = $1`
	rows, err := s.db.QueryContext(ctx, tagQuery, id)
	if err == nil {
		defer rows.Close()
		for rows.Next() {
			var t domain.Tag
			if err := rows.Scan(&t.ID, &t.Name); err == nil {
				a.Tags = append(a.Tags, t)
			}
		}
	}

	return &a, nil
}

func (s *store) GetAllArticles(ctx context.Context, params repository.ListParams) ([]*domain.Article, error) {
	query := applyPagination(`SELECT id, editor_id, title, content, created, modified FROM distcomp.tbl_article`, params)
	rows, err := s.db.QueryContext(ctx, query)
	if err != nil {
		return nil, err
	}
	defer rows.Close()

	var articles []*domain.Article
	for rows.Next() {
		var a domain.Article
		if err := rows.Scan(&a.ID, &a.EditorID, &a.Title, &a.Content, &a.Created, &a.Modified); err != nil {
			return nil, err
		}
		articles = append(articles, &a)
	}

	for _, a := range articles {
		tagQuery := `SELECT t.id, t.name FROM distcomp.tbl_tag t JOIN distcomp.tbl_article_tag at ON t.id = at.tag_id WHERE at.article_id = $1`
		tagRows, err := s.db.QueryContext(ctx, tagQuery, a.ID)
		if err == nil {
			for tagRows.Next() {
				var t domain.Tag
				if err := tagRows.Scan(&t.ID, &t.Name); err == nil {
					a.Tags = append(a.Tags, t)
				}
			}
			tagRows.Close()
		}
	}

	return articles, nil
}

func (s *store) UpdateArticle(ctx context.Context, article *domain.Article) error {
	tx, err := s.db.BeginTx(ctx, nil)
	if err != nil {
		return err
	}
	defer tx.Rollback()

	query := `UPDATE distcomp.tbl_article SET editor_id = $1, title = $2, content = $3, modified = $4 WHERE id = $5`
	res, err := tx.ExecContext(ctx, query, article.EditorID, article.Title, article.Content, article.Modified, article.ID)
	if err != nil {
		return err
	}
	rowsAff, _ := res.RowsAffected()
	if rowsAff == 0 {
		return ErrNotFound
	}

	if _, err = tx.ExecContext(ctx, `DELETE FROM distcomp.tbl_article_tag WHERE article_id = $1`, article.ID); err != nil {
		return err
	}

	for i, t := range article.Tags {
		tagQuery := `INSERT INTO distcomp.tbl_tag (name) VALUES ($1) ON CONFLICT (name) DO UPDATE SET name = EXCLUDED.name RETURNING id`
		if err = tx.QueryRowContext(ctx, tagQuery, t.Name).Scan(&article.Tags[i].ID); err != nil {
			return err
		}
		linkQuery := `INSERT INTO distcomp.tbl_article_tag (article_id, tag_id) VALUES ($1, $2)`
		if _, err = tx.ExecContext(ctx, linkQuery, article.ID, article.Tags[i].ID); err != nil {
			return err
		}
	}

	// Вычищаем сирот (если удалили тег из статьи при апдейте)
	if _, err = tx.ExecContext(ctx, `DELETE FROM distcomp.tbl_tag WHERE id NOT IN (SELECT tag_id FROM distcomp.tbl_article_tag)`); err != nil {
		return err
	}

	return tx.Commit()
}

func (s *store) DeleteArticle(ctx context.Context, id int64) error {
	query := `DELETE FROM distcomp.tbl_article WHERE id = $1`
	res, err := s.db.ExecContext(ctx, query, id)
	if err != nil {
		return err
	}
	rows, err := res.RowsAffected()
	if err != nil || rows == 0 {
		return ErrNotFound
	}

	// Вычищаем сирот
	s.cleanOrphanedTags(ctx)

	return nil
}

// --- TAG ---

func (s *store) CreateTag(ctx context.Context, tag *domain.Tag) error {
	query := `INSERT INTO distcomp.tbl_tag (name) VALUES ($1) RETURNING id`
	return s.db.QueryRowContext(ctx, query, tag.Name).Scan(&tag.ID)
}

func (s *store) GetTagByID(ctx context.Context, id int64) (*domain.Tag, error) {
	query := `SELECT id, name FROM distcomp.tbl_tag WHERE id = $1`
	var t domain.Tag
	err := s.db.QueryRowContext(ctx, query, id).Scan(&t.ID, &t.Name)
	if errors.Is(err, sql.ErrNoRows) {
		return nil, ErrNotFound
	}
	return &t, err
}

func (s *store) GetAllTags(ctx context.Context, params repository.ListParams) ([]*domain.Tag, error) {
	query := applyPagination(`SELECT id, name FROM distcomp.tbl_tag`, params)
	rows, err := s.db.QueryContext(ctx, query)
	if err != nil {
		return nil, err
	}
	defer rows.Close()

	var tags []*domain.Tag
	for rows.Next() {
		var t domain.Tag
		if err := rows.Scan(&t.ID, &t.Name); err != nil {
			return nil, err
		}
		tags = append(tags, &t)
	}
	return tags, nil
}

func (s *store) UpdateTag(ctx context.Context, tag *domain.Tag) error {
	query := `UPDATE distcomp.tbl_tag SET name = $1 WHERE id = $2`
	res, err := s.db.ExecContext(ctx, query, tag.Name, tag.ID)
	if err != nil {
		return err
	}
	rows, err := res.RowsAffected()
	if err != nil || rows == 0 {
		return ErrNotFound
	}
	return nil
}

func (s *store) DeleteTag(ctx context.Context, id int64) error {
	query := `DELETE FROM distcomp.tbl_tag WHERE id = $1`
	res, err := s.db.ExecContext(ctx, query, id)
	if err != nil {
		return err
	}
	rows, err := res.RowsAffected()
	if err != nil || rows == 0 {
		return ErrNotFound
	}
	return nil
}

// --- COMMENT ---

func (s *store) CreateComment(ctx context.Context, comment *domain.Comment) error {
	query := `INSERT INTO distcomp.tbl_comment (article_id, content) VALUES ($1, $2) RETURNING id`
	return s.db.QueryRowContext(ctx, query, comment.ArticleID, comment.Content).Scan(&comment.ID)
}

func (s *store) GetCommentByID(ctx context.Context, id int64) (*domain.Comment, error) {
	query := `SELECT id, article_id, content FROM distcomp.tbl_comment WHERE id = $1`
	var c domain.Comment
	err := s.db.QueryRowContext(ctx, query, id).Scan(&c.ID, &c.ArticleID, &c.Content)
	if errors.Is(err, sql.ErrNoRows) {
		return nil, ErrNotFound
	}
	return &c, err
}

func (s *store) GetAllComments(ctx context.Context, params repository.ListParams) ([]*domain.Comment, error) {
	query := applyPagination(`SELECT id, article_id, content FROM distcomp.tbl_comment`, params)
	rows, err := s.db.QueryContext(ctx, query)
	if err != nil {
		return nil, err
	}
	defer rows.Close()

	var comments []*domain.Comment
	for rows.Next() {
		var c domain.Comment
		if err := rows.Scan(&c.ID, &c.ArticleID, &c.Content); err != nil {
			return nil, err
		}
		comments = append(comments, &c)
	}
	return comments, nil
}

func (s *store) UpdateComment(ctx context.Context, comment *domain.Comment) error {
	query := `UPDATE distcomp.tbl_comment SET article_id = $1, content = $2 WHERE id = $3`
	res, err := s.db.ExecContext(ctx, query, comment.ArticleID, comment.Content, comment.ID)
	if err != nil {
		return err
	}
	rows, err := res.RowsAffected()
	if err != nil || rows == 0 {
		return ErrNotFound
	}
	return nil
}

func (s *store) DeleteComment(ctx context.Context, id int64) error {
	query := `DELETE FROM distcomp.tbl_comment WHERE id = $1`
	res, err := s.db.ExecContext(ctx, query, id)
	if err != nil {
		return err
	}
	rows, err := res.RowsAffected()
	if err != nil || rows == 0 {
		return ErrNotFound
	}
	return nil
}