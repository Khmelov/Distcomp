package service

import (
	"errors"
	"strings"

	"github.com/bsuir/rest-api/internal/domain"
	"github.com/bsuir/rest-api/internal/dto/request"
	"github.com/bsuir/rest-api/internal/dto/response"
	"github.com/bsuir/rest-api/internal/repository"
)

// IssueService предоставляет бизнес-логику для работы с задачами
type IssueService struct {
	issueRepo  repository.IssueRepository
	authorRepo repository.AuthorRepository
	markerRepo repository.MarkerRepository
}

// NewIssueService создает новый экземпляр сервиса
func NewIssueService(issueRepo repository.IssueRepository, authorRepo repository.AuthorRepository, markerRepo repository.MarkerRepository) *IssueService {
	return &IssueService{
		issueRepo:  issueRepo,
		authorRepo: authorRepo,
		markerRepo: markerRepo,
	}
}

// Create создает новую задачу
func (s *IssueService) Create(req *request.IssueRequestTo) (*response.IssueResponseTo, error) {
	if err := s.validateIssueRequest(req); err != nil {
		return nil, err
	}
	// Проверяем существование автора
	if _, err := s.authorRepo.GetByID(req.AuthorID); err != nil {
		return nil, ErrInvalidAuthorID
	}
	// Проверяем существование всех меток
	for _, markerID := range req.MarkerIDs {
		if _, err := s.markerRepo.GetByID(markerID); err != nil {
			return nil, ErrMarkerNotFound
		}
	}
	issue := ToIssueDomain(req, GenerateID())
	created, err := s.issueRepo.Create(issue)
	if err != nil {
		return nil, err
	}
	return ToIssueResponse(created), nil
}

// GetByID получает задачу по ID
func (s *IssueService) GetByID(id int64) (*response.IssueResponseTo, error) {
	if id == 0 {
		return nil, errors.New("id is required")
	}
	issue, err := s.issueRepo.GetByID(id)
	if err != nil {
		return nil, ErrIssueNotFound
	}
	return ToIssueResponse(issue), nil
}

// GetAll получает все задачи
func (s *IssueService) GetAll() ([]*response.IssueResponseTo, error) {
	issues, err := s.issueRepo.GetAll()
	if err != nil {
		return nil, err
	}
	responses := make([]*response.IssueResponseTo, len(issues))
	for i, issue := range issues {
		responses[i] = ToIssueResponse(issue)
	}
	return responses, nil
}

// Update обновляет задачу
func (s *IssueService) Update(id int64, req *request.IssueRequestTo) (*response.IssueResponseTo, error) {
	if id == 0 {
		return nil, errors.New("id is required")
	}
	if err := s.validateIssueRequest(req); err != nil {
		return nil, err
	}
	// Проверяем существование автора
	if _, err := s.authorRepo.GetByID(req.AuthorID); err != nil {
		return nil, ErrInvalidAuthorID
	}
	// Проверяем существование всех меток
	for _, markerID := range req.MarkerIDs {
		if _, err := s.markerRepo.GetByID(markerID); err != nil {
			return nil, ErrMarkerNotFound
		}
	}
	existing, err := s.issueRepo.GetByID(id)
	if err != nil {
		return nil, ErrIssueNotFound
	}
	updatedIssue := UpdateIssueDomain(existing, req)
	updated, err := s.issueRepo.Update(id, updatedIssue)
	if err != nil {
		return nil, ErrIssueNotFound
	}
	return ToIssueResponse(updated), nil
}

// Delete удаляет задачу
func (s *IssueService) Delete(id int64) error {
	if id == 0 {
		return errors.New("id is required")
	}
	err := s.issueRepo.Delete(id)
	if err != nil {
		return ErrIssueNotFound
	}
	return nil
}

// GetAuthorByIssueID получает автора по ID задачи
func (s *IssueService) GetAuthorByIssueID(issueID int64) (*response.AuthorResponseTo, error) {
	issue, err := s.issueRepo.GetByID(issueID)
	if err != nil {
		return nil, ErrIssueNotFound
	}
	author, err := s.authorRepo.GetByID(issue.AuthorID)
	if err != nil {
		return nil, ErrAuthorNotFound
	}
	return ToAuthorResponse(author), nil
}

// GetMarkersByIssueID получает метки по ID задачи
func (s *IssueService) GetMarkersByIssueID(issueID int64) ([]*response.MarkerResponseTo, error) {
	issue, err := s.issueRepo.GetByID(issueID)
	if err != nil {
		return nil, ErrIssueNotFound
	}
	markers := make([]*response.MarkerResponseTo, 0)
	for _, markerID := range issue.MarkerIDs {
		marker, err := s.markerRepo.GetByID(markerID)
		if err == nil {
			markers = append(markers, ToMarkerResponse(marker))
		}
	}
	return markers, nil
}

// GetNotesByIssueID получает заметки по ID задачи (используется через NoteService)
func (s *IssueService) GetNotesByIssueID(issueID string) ([]*response.NoteResponseTo, error) {
	// Эта функция будет реализована в NoteService
	return nil, nil
}

// GetByFilters получает задачи по фильтрам
func (s *IssueService) GetByFilters(markerNames []string, markerIDs []int64, authorLogin, title, content string) ([]*response.IssueResponseTo, error) {
	// Получаем все задачи
	allIssues, err := s.issueRepo.GetAll()
	if err != nil {
		return nil, err
	}
	var filteredIssues []*domain.Issue
	// Фильтруем по markerIDs
	if len(markerIDs) > 0 {
		markerSet := make(map[int64]bool)
		for _, id := range markerIDs {
			markerSet[id] = true
		}
		for _, issue := range allIssues {
			for _, issueMarkerID := range issue.MarkerIDs {
				if markerSet[issueMarkerID] {
					filteredIssues = append(filteredIssues, issue)
					break
				}
			}
		}
	} else {
		filteredIssues = allIssues
	}
	// Фильтруем по markerNames
	if len(markerNames) > 0 {
		var nameFiltered []*domain.Issue
		for _, issue := range filteredIssues {
			for _, markerID := range issue.MarkerIDs {
				marker, err := s.markerRepo.GetByID(markerID)
				if err == nil {
					for _, name := range markerNames {
						if strings.EqualFold(marker.Name, name) {
							nameFiltered = append(nameFiltered, issue)
							goto nextIssue
						}
					}
				}
			}
		nextIssue:
		}
		filteredIssues = nameFiltered
	}
	// Фильтруем по authorLogin
	if authorLogin != "" {
		author, err := s.findAuthorByLogin(authorLogin)
		if err == nil {
			var loginFiltered []*domain.Issue
			for _, issue := range filteredIssues {
				if issue.AuthorID == author.ID {
					loginFiltered = append(loginFiltered, issue)
				}
			}
			filteredIssues = loginFiltered
		}
	}
	// Фильтруем по title
	if title != "" {
		var titleFiltered []*domain.Issue
		for _, issue := range filteredIssues {
			if strings.Contains(strings.ToLower(issue.Title), strings.ToLower(title)) {
				titleFiltered = append(titleFiltered, issue)
			}
		}
		filteredIssues = titleFiltered
	}
	// Фильтруем по content
	if content != "" {
		var contentFiltered []*domain.Issue
		for _, issue := range filteredIssues {
			if strings.Contains(strings.ToLower(issue.Content), strings.ToLower(content)) {
				contentFiltered = append(contentFiltered, issue)
			}
		}
		filteredIssues = contentFiltered
	}
	responses := make([]*response.IssueResponseTo, len(filteredIssues))
	for i, issue := range filteredIssues {
		responses[i] = ToIssueResponse(issue)
	}
	return responses, nil
}

// findAuthorByLogin находит автора по логину
func (s *IssueService) findAuthorByLogin(login string) (*domain.Author, error) {
	authors, err := s.authorRepo.GetAll()
	if err != nil {
		return nil, err
	}
	for _, author := range authors {
		if author.Login == login {
			return author, nil
		}
	}
	return nil, ErrAuthorNotFound
}

// validateIssueRequest валидирует запрос задачи
func (s *IssueService) validateIssueRequest(req *request.IssueRequestTo) error {
	if req.Title == "" {
		return errors.New("title is required")
	}
	if len(req.Title) < 2 || len(req.Title) > 64 {
		return errors.New("title must be between 2 and 64 characters")
	}
	if req.Content == "" {
		return errors.New("content is required")
	}
	if len(req.Content) < 4 || len(req.Content) > 2048 {
		return errors.New("content must be between 4 and 2048 characters")
	}
	if req.AuthorID == 0 {
		return errors.New("authorId is required")
	}
	return nil
}

