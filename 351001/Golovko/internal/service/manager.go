package service

import (
	"context"
	"time"

	"distcomp/internal/domain"
	"distcomp/internal/dto"
	"distcomp/internal/repository"
)

type Manager struct {
	Editor  Editor
	Article Article
	Tag     Tag
	Comment Comment
}

func NewManager(repo repository.Storage) *Manager {
	return &Manager{
		Editor:  &editorService{repo: repo},
		Article: &articleService{repo: repo},
		Tag:     &tagService{repo: repo},
		Comment: &commentService{repo: repo},
	}
}

type editorService struct {
	repo repository.Storage
}

func (s *editorService) Create(ctx context.Context, req dto.EditorRequestTo) (dto.EditorResponseTo, error) {
	e := &domain.Editor{
		Login:     req.Login,
		Password:  req.Password,
		FirstName: req.FirstName,
		LastName:  req.LastName,
	}
	if err := s.repo.CreateEditor(ctx, e); err != nil {
		return dto.EditorResponseTo{}, err
	}
	return dto.EditorResponseTo{ID: e.ID, Login: e.Login, FirstName: e.FirstName, LastName: e.LastName}, nil
}

func (s *editorService) GetByID(ctx context.Context, id int64) (dto.EditorResponseTo, error) {
	e, err := s.repo.GetEditorByID(ctx, id)
	if err != nil {
		return dto.EditorResponseTo{}, err
	}
	return dto.EditorResponseTo{ID: e.ID, Login: e.Login, FirstName: e.FirstName, LastName: e.LastName}, nil
}

func (s *editorService) GetAll(ctx context.Context, params repository.ListParams) ([]dto.EditorResponseTo, error) {
	editors, err := s.repo.GetAllEditors(ctx, params)
	if err != nil {
		return nil, err
	}
	res := make([]dto.EditorResponseTo, len(editors))
	for i, e := range editors {
		res[i] = dto.EditorResponseTo{ID: e.ID, Login: e.Login, FirstName: e.FirstName, LastName: e.LastName}
	}
	return res, nil
}

func (s *editorService) Update(ctx context.Context, id int64, req dto.EditorRequestTo) (dto.EditorResponseTo, error) {
	e := &domain.Editor{ID: id, Login: req.Login, Password: req.Password, FirstName: req.FirstName, LastName: req.LastName}
	if err := s.repo.UpdateEditor(ctx, e); err != nil {
		return dto.EditorResponseTo{}, err
	}
	return dto.EditorResponseTo{ID: e.ID, Login: e.Login, FirstName: e.FirstName, LastName: e.LastName}, nil
}

func (s *editorService) Delete(ctx context.Context, id int64) error {
	return s.repo.DeleteEditor(ctx, id)
}

type articleService struct {
	repo repository.Storage
}

func (s *articleService) Create(ctx context.Context, req dto.ArticleRequestTo) (dto.ArticleResponseTo, error) {
	if _, err := s.repo.GetEditorByID(ctx, req.EditorID); err != nil {
		return dto.ArticleResponseTo{}, err
	}
	now := time.Now().UTC()
	a := &domain.Article{
		EditorID: req.EditorID,
		Title:    req.Title,
		Content:  req.Content,
		Created:  now,
		Modified: now,
	}
	
	// ИЗМЕНЕНИЯ ЗДЕСЬ (парсим строки)
	for _, tagName := range req.Tags {
		a.Tags = append(a.Tags, domain.Tag{Name: tagName})
	}

	if err := s.repo.CreateArticle(ctx, a); err != nil {
		return dto.ArticleResponseTo{}, err
	}

	res := dto.ArticleResponseTo{
		ID: a.ID, EditorID: a.EditorID, Title: a.Title, Content: a.Content,
		Created: a.Created.Format(time.RFC3339), Modified: a.Modified.Format(time.RFC3339),
	}
	for _, t := range a.Tags {
		res.Tags = append(res.Tags, dto.TagResponseTo{ID: t.ID, Name: t.Name})
	}
	return res, nil
}

func (s *articleService) GetByID(ctx context.Context, id int64) (dto.ArticleResponseTo, error) {
	a, err := s.repo.GetArticleByID(ctx, id)
	if err != nil {
		return dto.ArticleResponseTo{}, err
	}
	res := dto.ArticleResponseTo{
		ID: a.ID, EditorID: a.EditorID, Title: a.Title, Content: a.Content,
		Created: a.Created.Format(time.RFC3339), Modified: a.Modified.Format(time.RFC3339),
	}
	for _, t := range a.Tags {
		res.Tags = append(res.Tags, dto.TagResponseTo{ID: t.ID, Name: t.Name})
	}
	return res, nil
}

func (s *articleService) GetAll(ctx context.Context, params repository.ListParams) ([]dto.ArticleResponseTo, error) {
	articles, err := s.repo.GetAllArticles(ctx, params)
	if err != nil {
		return nil, err
	}
	res := make([]dto.ArticleResponseTo, len(articles))
	for i, a := range articles {
		res[i] = dto.ArticleResponseTo{
			ID: a.ID, EditorID: a.EditorID, Title: a.Title, Content: a.Content,
			Created: a.Created.Format(time.RFC3339), Modified: a.Modified.Format(time.RFC3339),
		}
		for _, t := range a.Tags {
			res[i].Tags = append(res[i].Tags, dto.TagResponseTo{ID: t.ID, Name: t.Name})
		}
	}
	return res, nil
}

func (s *articleService) Update(ctx context.Context, id int64, req dto.ArticleRequestTo) (dto.ArticleResponseTo, error) {
	old, err := s.repo.GetArticleByID(ctx, id)
	if err != nil {
		return dto.ArticleResponseTo{}, err
	}
	if _, err := s.repo.GetEditorByID(ctx, req.EditorID); err != nil {
		return dto.ArticleResponseTo{}, err
	}
	old.EditorID = req.EditorID
	old.Title = req.Title
	old.Content = req.Content
	old.Modified = time.Now().UTC()
	
	// ИЗМЕНЕНИЯ ЗДЕСЬ (парсим строки)
	old.Tags = nil
	for _, tagName := range req.Tags {
		old.Tags = append(old.Tags, domain.Tag{Name: tagName})
	}

	if err := s.repo.UpdateArticle(ctx, old); err != nil {
		return dto.ArticleResponseTo{}, err
	}

	res := dto.ArticleResponseTo{
		ID: old.ID, EditorID: old.EditorID, Title: old.Title, Content: old.Content,
		Created: old.Created.Format(time.RFC3339), Modified: old.Modified.Format(time.RFC3339),
	}
	for _, t := range old.Tags {
		res.Tags = append(res.Tags, dto.TagResponseTo{ID: t.ID, Name: t.Name})
	}
	return res, nil
}

func (s *articleService) Delete(ctx context.Context, id int64) error {
	return s.repo.DeleteArticle(ctx, id)
}

type tagService struct {
	repo repository.Storage
}

func (s *tagService) Create(ctx context.Context, req dto.TagRequestTo) (dto.TagResponseTo, error) {
	t := &domain.Tag{Name: req.Name}
	if err := s.repo.CreateTag(ctx, t); err != nil {
		return dto.TagResponseTo{}, err
	}
	return dto.TagResponseTo{ID: t.ID, Name: t.Name}, nil
}

func (s *tagService) GetByID(ctx context.Context, id int64) (dto.TagResponseTo, error) {
	t, err := s.repo.GetTagByID(ctx, id)
	if err != nil {
		return dto.TagResponseTo{}, err
	}
	return dto.TagResponseTo{ID: t.ID, Name: t.Name}, nil
}

func (s *tagService) GetAll(ctx context.Context, params repository.ListParams) ([]dto.TagResponseTo, error) {
	tags, err := s.repo.GetAllTags(ctx, params)
	if err != nil {
		return nil, err
	}
	res := make([]dto.TagResponseTo, len(tags))
	for i, t := range tags {
		res[i] = dto.TagResponseTo{ID: t.ID, Name: t.Name}
	}
	return res, nil
}

func (s *tagService) Update(ctx context.Context, id int64, req dto.TagRequestTo) (dto.TagResponseTo, error) {
	t := &domain.Tag{ID: id, Name: req.Name}
	if err := s.repo.UpdateTag(ctx, t); err != nil {
		return dto.TagResponseTo{}, err
	}
	return dto.TagResponseTo{ID: t.ID, Name: t.Name}, nil
}

func (s *tagService) Delete(ctx context.Context, id int64) error {
	return s.repo.DeleteTag(ctx, id)
}

type commentService struct {
	repo repository.Storage
}

func (s *commentService) Create(ctx context.Context, req dto.CommentRequestTo) (dto.CommentResponseTo, error) {
	if _, err := s.repo.GetArticleByID(ctx, req.ArticleID); err != nil {
		return dto.CommentResponseTo{}, err
	}
	c := &domain.Comment{ArticleID: req.ArticleID, Content: req.Content}
	if err := s.repo.CreateComment(ctx, c); err != nil {
		return dto.CommentResponseTo{}, err
	}
	return dto.CommentResponseTo{ID: c.ID, ArticleID: c.ArticleID, Content: c.Content}, nil
}

func (s *commentService) GetByID(ctx context.Context, id int64) (dto.CommentResponseTo, error) {
	c, err := s.repo.GetCommentByID(ctx, id)
	if err != nil {
		return dto.CommentResponseTo{}, err
	}
	return dto.CommentResponseTo{ID: c.ID, ArticleID: c.ArticleID, Content: c.Content}, nil
}

func (s *commentService) GetAll(ctx context.Context, params repository.ListParams) ([]dto.CommentResponseTo, error) {
	comments, err := s.repo.GetAllComments(ctx, params)
	if err != nil {
		return nil, err
	}
	res := make([]dto.CommentResponseTo, len(comments))
	for i, c := range comments {
		res[i] = dto.CommentResponseTo{ID: c.ID, ArticleID: c.ArticleID, Content: c.Content}
	}
	return res, nil
}

func (s *commentService) Update(ctx context.Context, id int64, req dto.CommentRequestTo) (dto.CommentResponseTo, error) {
	if _, err := s.repo.GetArticleByID(ctx, req.ArticleID); err != nil {
		return dto.CommentResponseTo{}, err
	}
	c := &domain.Comment{ID: id, ArticleID: req.ArticleID, Content: req.Content}
	if err := s.repo.UpdateComment(ctx, c); err != nil {
		return dto.CommentResponseTo{}, err
	}
	return dto.CommentResponseTo{ID: c.ID, ArticleID: c.ArticleID, Content: c.Content}, nil
}

func (s *commentService) Delete(ctx context.Context, id int64) error {
	return s.repo.DeleteComment(ctx, id)
}