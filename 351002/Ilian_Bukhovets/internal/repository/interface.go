package repository

import "github.com/bsuir/rest-api/internal/domain"

// Repository представляет обобщенный интерфейс для CRUD операций
type Repository[T any] interface {
	Create(entity *T) (*T, error)
	GetByID(id int64) (*T, error)
	GetAll() ([]*T, error)
	Update(id int64, entity *T) (*T, error)
	Delete(id int64) error
}

// AuthorRepository интерфейс для работы с авторами
type AuthorRepository interface {
	Repository[domain.Author]
}

// IssueRepository интерфейс для работы с задачами
type IssueRepository interface {
	Repository[domain.Issue]
	GetByAuthorID(authorID int64) ([]*domain.Issue, error)
	GetByMarkerName(name string) ([]*domain.Issue, error)
	GetByMarkerIDs(markerIDs []int64) ([]*domain.Issue, error)
	GetByAuthorLogin(login string) ([]*domain.Issue, error)
	GetByFilters(markerNames []string, markerIDs []int64, authorLogin, title, content string) ([]*domain.Issue, error)
}

// MarkerRepository интерфейс для работы с метками
type MarkerRepository interface {
	Repository[domain.Marker]
	GetByIssueID(issueID int64) ([]*domain.Marker, error)
}

// NoteRepository интерфейс для работы с заметками
type NoteRepository interface {
	Repository[domain.Note]
	GetByIssueID(issueID int64) ([]*domain.Note, error)
}

