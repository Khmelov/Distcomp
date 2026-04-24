package memory

import (
	"context"
	"errors"
	"sync"

	"distcomp/internal/domain"
)

var ErrNotFound = errors.New("entity not found")

type Storage struct {
	mu       sync.RWMutex
	editors  map[int64]*domain.Editor
	articles map[int64]*domain.Article
	tags     map[int64]*domain.Tag
	comments map[int64]*domain.Comment

	editorIDCounter  int64
	articleIDCounter int64
	tagIDCounter     int64
	commentIDCounter int64
}

func NewStorage() *Storage {
	return &Storage{
		editors:  make(map[int64]*domain.Editor),
		articles: make(map[int64]*domain.Article),
		tags:     make(map[int64]*domain.Tag),
		comments: make(map[int64]*domain.Comment),
	}
}

// --- Editor ---

func (s *Storage) CreateEditor(ctx context.Context, editor *domain.Editor) error {
	s.mu.Lock()
	defer s.mu.Unlock()
	s.editorIDCounter++
	editor.ID = s.editorIDCounter
	s.editors[editor.ID] = editor
	return nil
}

func (s *Storage) GetEditorByID(ctx context.Context, id int64) (*domain.Editor, error) {
	s.mu.RLock()
	defer s.mu.RUnlock()
	e, ok := s.editors[id]
	if !ok {
		return nil, ErrNotFound
	}
	return e, nil
}

func (s *Storage) GetAllEditors(ctx context.Context) ([]*domain.Editor, error) {
	s.mu.RLock()
	defer s.mu.RUnlock()
	var res []*domain.Editor
	for _, e := range s.editors {
		res = append(res, e)
	}
	return res, nil
}

func (s *Storage) UpdateEditor(ctx context.Context, editor *domain.Editor) error {
	s.mu.Lock()
	defer s.mu.Unlock()
	if _, ok := s.editors[editor.ID]; !ok {
		return ErrNotFound
	}
	s.editors[editor.ID] = editor
	return nil
}

func (s *Storage) DeleteEditor(ctx context.Context, id int64) error {
	s.mu.Lock()
	defer s.mu.Unlock()
	if _, ok := s.editors[id]; !ok {
		return ErrNotFound
	}
	delete(s.editors, id)
	return nil
}

// --- Article ---

func (s *Storage) CreateArticle(ctx context.Context, article *domain.Article) error {
	s.mu.Lock()
	defer s.mu.Unlock()
	s.articleIDCounter++
	article.ID = s.articleIDCounter
	s.articles[article.ID] = article
	return nil
}

func (s *Storage) GetArticleByID(ctx context.Context, id int64) (*domain.Article, error) {
	s.mu.RLock()
	defer s.mu.RUnlock()
	a, ok := s.articles[id]
	if !ok {
		return nil, ErrNotFound
	}
	return a, nil
}

func (s *Storage) GetAllArticles(ctx context.Context) ([]*domain.Article, error) {
	s.mu.RLock()
	defer s.mu.RUnlock()
	var res []*domain.Article
	for _, a := range s.articles {
		res = append(res, a)
	}
	return res, nil
}

func (s *Storage) UpdateArticle(ctx context.Context, article *domain.Article) error {
	s.mu.Lock()
	defer s.mu.Unlock()
	if _, ok := s.articles[article.ID]; !ok {
		return ErrNotFound
	}
	s.articles[article.ID] = article
	return nil
}

func (s *Storage) DeleteArticle(ctx context.Context, id int64) error {
	s.mu.Lock()
	defer s.mu.Unlock()
	if _, ok := s.articles[id]; !ok {
		return ErrNotFound
	}
	delete(s.articles, id)
	return nil
}

// --- Tag ---

func (s *Storage) CreateTag(ctx context.Context, tag *domain.Tag) error {
	s.mu.Lock()
	defer s.mu.Unlock()
	s.tagIDCounter++
	tag.ID = s.tagIDCounter
	s.tags[tag.ID] = tag
	return nil
}

func (s *Storage) GetTagByID(ctx context.Context, id int64) (*domain.Tag, error) {
	s.mu.RLock()
	defer s.mu.RUnlock()
	t, ok := s.tags[id]
	if !ok {
		return nil, ErrNotFound
	}
	return t, nil
}

func (s *Storage) GetAllTags(ctx context.Context) ([]*domain.Tag, error) {
	s.mu.RLock()
	defer s.mu.RUnlock()
	var res []*domain.Tag
	for _, t := range s.tags {
		res = append(res, t)
	}
	return res, nil
}

func (s *Storage) UpdateTag(ctx context.Context, tag *domain.Tag) error {
	s.mu.Lock()
	defer s.mu.Unlock()
	if _, ok := s.tags[tag.ID]; !ok {
		return ErrNotFound
	}
	s.tags[tag.ID] = tag
	return nil
}

func (s *Storage) DeleteTag(ctx context.Context, id int64) error {
	s.mu.Lock()
	defer s.mu.Unlock()
	if _, ok := s.tags[id]; !ok {
		return ErrNotFound
	}
	delete(s.tags, id)
	return nil
}

// --- Comment ---

func (s *Storage) CreateComment(ctx context.Context, comment *domain.Comment) error {
	s.mu.Lock()
	defer s.mu.Unlock()
	s.commentIDCounter++
	comment.ID = s.commentIDCounter
	s.comments[comment.ID] = comment
	return nil
}

func (s *Storage) GetCommentByID(ctx context.Context, id int64) (*domain.Comment, error) {
	s.mu.RLock()
	defer s.mu.RUnlock()
	c, ok := s.comments[id]
	if !ok {
		return nil, ErrNotFound
	}
	return c, nil
}

func (s *Storage) GetAllComments(ctx context.Context) ([]*domain.Comment, error) {
	s.mu.RLock()
	defer s.mu.RUnlock()
	var res []*domain.Comment
	for _, c := range s.comments {
		res = append(res, c)
	}
	return res, nil
}

func (s *Storage) UpdateComment(ctx context.Context, comment *domain.Comment) error {
	s.mu.Lock()
	defer s.mu.Unlock()
	if _, ok := s.comments[comment.ID]; !ok {
		return ErrNotFound
	}
	s.comments[comment.ID] = comment
	return nil
}

func (s *Storage) DeleteComment(ctx context.Context, id int64) error {
	s.mu.Lock()
	defer s.mu.Unlock()
	if _, ok := s.comments[id]; !ok {
		return ErrNotFound
	}
	delete(s.comments, id)
	return nil
}