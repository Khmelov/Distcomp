package service

import (
	"distcomp/internal/domain"
	"distcomp/internal/repository"
	"errors"
	"strings"
)

type Service struct {
	repo *repository.Repository
}

func NewService(repo *repository.Repository) *Service {
	return &Service{repo: repo}
}

func (s *Service) CreateEditor(dto *domain.EditorDTO) (*domain.Editor, error) {
	editor := &domain.Editor{
		Login:     dto.Login,
		Password:  dto.Password,
		Firstname: dto.Firstname,
		Lastname:  dto.Lastname,
	}
	if err := s.repo.CreateEditor(editor); err != nil {
		if strings.Contains(err.Error(), "duplicate key") || strings.Contains(err.Error(), "23505") {
			return nil, errors.New("duplicate_403")
		}
		return nil, err
	}
	return editor, nil
}

func (s *Service) UpdateEditor(dto *domain.EditorDTO) (*domain.Editor, error) {
	editor, err := s.repo.GetEditor(*dto.ID)
	if err != nil {
		return nil, err
	}
	editor.Login = dto.Login
	editor.Password = dto.Password
	editor.Firstname = dto.Firstname
	editor.Lastname = dto.Lastname
	if err := s.repo.UpdateEditor(editor); err != nil {
		return nil, err
	}
	return editor, nil
}

func (s *Service) CreateTopic(dto *domain.TopicDTO) (*domain.Topic, error) {
	if _, err := s.repo.GetEditor(dto.EditorID); err != nil {
		return nil, errors.New("association_400")
	}

	topic := &domain.Topic{
		EditorID: dto.EditorID,
		Title:    dto.Title,
		Content:  dto.Content,
		Markers:  []domain.Marker{},
	}

	for _, markerName := range dto.Markers {
		m, err := s.repo.GetMarkerByName(markerName)
		if err != nil {
			newMarker := &domain.Marker{Name: markerName}
			if err := s.repo.CreateMarker(newMarker); err == nil {
				topic.Markers = append(topic.Markers, *newMarker)
			}
		} else {
			topic.Markers = append(topic.Markers, *m)
		}
	}

	if err := s.repo.CreateTopic(topic); err != nil {
		if strings.Contains(err.Error(), "duplicate key") || strings.Contains(err.Error(), "23505") {
			return nil, errors.New("duplicate_403")
		}
		return nil, err
	}
	return topic, nil
}

func (s *Service) CreateMarker(dto *domain.MarkerDTO) (*domain.Marker, error) {
	marker := &domain.Marker{Name: dto.Name}
	if err := s.repo.CreateMarker(marker); err != nil {
		if strings.Contains(err.Error(), "duplicate key") || strings.Contains(err.Error(), "23505") {
			return nil, errors.New("duplicate_403")
		}
		return nil, err
	}
	return marker, nil
}

func (s *Service) CreateNote(dto *domain.NoteDTO) (*domain.Note, error) {
	if _, err := s.repo.GetTopic(dto.TopicID); err != nil {
		return nil, errors.New("association_400")
	}
	note := &domain.Note{
		TopicID: dto.TopicID,
		Content: dto.Content,
	}
	if err := s.repo.CreateNote(note); err != nil {
		return nil, err
	}
	return note, nil
}

func (s *Service) Repo() *repository.Repository {
	return s.repo
}
