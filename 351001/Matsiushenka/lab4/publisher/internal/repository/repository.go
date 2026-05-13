package repository

import (
	"errors"
	"publisher/internal/domain"

	"gorm.io/gorm"
)

type Repository struct {
	DB *gorm.DB
}

func (r *Repository) CreateEditor(e *domain.Editor) error { return r.DB.Create(e).Error }
func (r *Repository) GetEditor(id uint) (*domain.Editor, error) {
	var e domain.Editor
	err := r.DB.First(&e, id).Error
	return &e, err
}
func (r *Repository) GetAllEditors() ([]domain.Editor, error) {
	var res []domain.Editor
	err := r.DB.Find(&res).Error
	return res, err
}
func (r *Repository) UpdateEditor(e *domain.Editor) error { return r.DB.Save(e).Error }
func (r *Repository) DeleteEditor(id uint) error          { return r.DB.Delete(&domain.Editor{}, id).Error }

func (r *Repository) CreateTopic(t *domain.Topic) error { return r.DB.Create(t).Error }
func (r *Repository) GetTopic(id uint) (*domain.Topic, error) {
	var t domain.Topic
	err := r.DB.Preload("Markers").First(&t, id).Error
	return &t, err
}
func (r *Repository) GetAllTopics() ([]domain.Topic, error) {
	var res []domain.Topic
	err := r.DB.Preload("Markers").Find(&res).Error
	return res, err
}
func (r *Repository) UpdateTopic(t *domain.Topic) error { return r.DB.Save(t).Error }
func (r *Repository) DeleteTopic(id uint) error         { return r.DB.Delete(&domain.Topic{}, id).Error }

func (r *Repository) CreateMarker(m *domain.Marker) error { return r.DB.Create(m).Error }
func (r *Repository) GetMarker(id uint) (*domain.Marker, error) {
	var m domain.Marker
	err := r.DB.First(&m, id).Error
	return &m, err
}
func (r *Repository) GetAllMarkers() ([]domain.Marker, error) {
	var res []domain.Marker
	err := r.DB.Find(&res).Error
	return res, err
}
func (r *Repository) UpdateMarker(m *domain.Marker) error { return r.DB.Save(m).Error }

func (r *Repository) DeleteMarker(id uint) error {
	res := r.DB.Delete(&domain.Marker{}, id)
	if res.Error != nil {
		return res.Error
	}
	if res.RowsAffected == 0 {
		return errors.New("not_found")
	}
	return nil
}
