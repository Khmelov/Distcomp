package service

import (
	"sync"
	"time"

	"github.com/bsuir/rest-api/internal/domain"
	"github.com/bsuir/rest-api/internal/dto/request"
	"github.com/bsuir/rest-api/internal/dto/response"
)

var (
	idCounter int64
	idMutex   sync.Mutex
)

// ToAuthorDomain преобразует AuthorRequestTo в domain.Author
func ToAuthorDomain(req *request.AuthorRequestTo, id int64) *domain.Author {
	now := time.Now()
	return &domain.Author{
		BaseEntity: domain.BaseEntity{
			ID:       id,
			Created:  now,
			Modified: now,
		},
		Login:     req.Login,
		Password:  req.Password,
		Firstname: req.Firstname,
		Lastname:  req.Lastname,
	}
}

// ToAuthorResponse преобразует domain.Author в AuthorResponseTo
func ToAuthorResponse(author *domain.Author) *response.AuthorResponseTo {
	return &response.AuthorResponseTo{
		ID:       author.ID,
		Login:    author.Login,
		Password: author.Password,
		Firstname: author.Firstname,
		Lastname:  author.Lastname,
		Created:   author.Created.Format(time.RFC3339),
		Modified:  author.Modified.Format(time.RFC3339),
	}
}

// ToIssueDomain преобразует IssueRequestTo в domain.Issue
func ToIssueDomain(req *request.IssueRequestTo, id int64) *domain.Issue {
	now := time.Now()
	markerIDs := req.MarkerIDs
	if markerIDs == nil {
		markerIDs = []int64{}
	}
	return &domain.Issue{
		BaseEntity: domain.BaseEntity{
			ID:       id,
			Created:  now,
			Modified: now,
		},
		Title:     req.Title,
		Content:   req.Content,
		AuthorID:  req.AuthorID,
		MarkerIDs: markerIDs,
	}
}

// ToIssueResponse преобразует domain.Issue в IssueResponseTo
func ToIssueResponse(issue *domain.Issue) *response.IssueResponseTo {
	return &response.IssueResponseTo{
		ID:        issue.ID,
		Title:     issue.Title,
		Content:   issue.Content,
		AuthorID:  issue.AuthorID,
		MarkerIDs: issue.MarkerIDs,
		Created:   issue.Created.Format(time.RFC3339),
		Modified:  issue.Modified.Format(time.RFC3339),
	}
}

// ToMarkerDomain преобразует MarkerRequestTo в domain.Marker
func ToMarkerDomain(req *request.MarkerRequestTo, id int64) *domain.Marker {
	now := time.Now()
	return &domain.Marker{
		BaseEntity: domain.BaseEntity{
			ID:       id,
			Created:  now,
			Modified: now,
		},
		Name: req.Name,
	}
}

// ToMarkerResponse преобразует domain.Marker в MarkerResponseTo
func ToMarkerResponse(marker *domain.Marker) *response.MarkerResponseTo {
	return &response.MarkerResponseTo{
		ID:       marker.ID,
		Name:     marker.Name,
		Created:  marker.Created.Format(time.RFC3339),
		Modified: marker.Modified.Format(time.RFC3339),
	}
}

// ToNoteDomain преобразует NoteRequestTo в domain.Note
func ToNoteDomain(req *request.NoteRequestTo, id int64) *domain.Note {
	now := time.Now()
	return &domain.Note{
		BaseEntity: domain.BaseEntity{
			ID:       id,
			Created:  now,
			Modified: now,
		},
		Content: req.Content,
		IssueID: req.IssueID,
	}
}

// ToNoteResponse преобразует domain.Note в NoteResponseTo
func ToNoteResponse(note *domain.Note) *response.NoteResponseTo {
	return &response.NoteResponseTo{
		ID:       note.ID,
		Content:  note.Content,
		IssueID:  note.IssueID,
		Created:  note.Created.Format(time.RFC3339),
		Modified: note.Modified.Format(time.RFC3339),
	}
}

// UpdateAuthorDomain обновляет существующего автора, сохраняя Created
func UpdateAuthorDomain(existing *domain.Author, req *request.AuthorRequestTo) *domain.Author {
	existing.Login = req.Login
	existing.Password = req.Password
	existing.Firstname = req.Firstname
	existing.Lastname = req.Lastname
	existing.Modified = time.Now()
	return existing
}

// UpdateIssueDomain обновляет существующую задачу, сохраняя Created
func UpdateIssueDomain(existing *domain.Issue, req *request.IssueRequestTo) *domain.Issue {
	existing.Title = req.Title
	existing.Content = req.Content
	existing.AuthorID = req.AuthorID
	markerIDs := req.MarkerIDs
	if markerIDs == nil {
		markerIDs = []int64{}
	}
	existing.MarkerIDs = markerIDs
	existing.Modified = time.Now()
	return existing
}

// UpdateMarkerDomain обновляет существующую метку, сохраняя Created
func UpdateMarkerDomain(existing *domain.Marker, req *request.MarkerRequestTo) *domain.Marker {
	existing.Name = req.Name
	existing.Modified = time.Now()
	return existing
}

// UpdateNoteDomain обновляет существующую заметку, сохраняя Created
func UpdateNoteDomain(existing *domain.Note, req *request.NoteRequestTo) *domain.Note {
	existing.Content = req.Content
	existing.IssueID = req.IssueID
	existing.Modified = time.Now()
	return existing
}

// GenerateID генерирует новый числовой ID
func GenerateID() int64 {
	idMutex.Lock()
	defer idMutex.Unlock()
	idCounter++
	return idCounter
}

