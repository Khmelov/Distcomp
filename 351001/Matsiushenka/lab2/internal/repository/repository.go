package repository

import (
	"distcomp/internal/domain"
	"errors"

	"gorm.io/gorm"
)

var ErrNotFound = errors.New("not_found_404")

type Repository struct {
	db *gorm.DB
}

func NewRepository(db *gorm.DB) *Repository {
	return &Repository{db: db}
}

func (r *Repository) CreateEditor(e *domain.Editor) error { return r.db.Create(e).Error }
func (r *Repository) GetEditor(id uint) (*domain.Editor, error) {
	var e domain.Editor
	err := r.db.First(&e, id).Error
	return &e, err
}
func (r *Repository) GetAllEditors() ([]domain.Editor, error) {
	var editors []domain.Editor
	err := r.db.Find(&editors).Error
	return editors, err
}
func (r *Repository) UpdateEditor(e *domain.Editor) error { return r.db.Save(e).Error }
func (r *Repository) DeleteEditor(id uint) error {
	res := r.db.Delete(&domain.Editor{}, id)
	if res.Error != nil {
		return res.Error
	}
	if res.RowsAffected == 0 {
		return ErrNotFound
	}
	return nil
}

func (r *Repository) CreateTopic(t *domain.Topic) error { return r.db.Create(t).Error }
func (r *Repository) GetTopic(id uint) (*domain.Topic, error) {
	var t domain.Topic
	err := r.db.Preload("Markers").First(&t, id).Error
	return &t, err
}
func (r *Repository) GetAllTopics() ([]domain.Topic, error) {
	var topics []domain.Topic
	err := r.db.Preload("Markers").Find(&topics).Error
	return topics, err
}
func (r *Repository) UpdateTopic(t *domain.Topic) error { return r.db.Save(t).Error }

func (r *Repository) DeleteTopic(id uint) error {
	return r.db.Transaction(func(tx *gorm.DB) error {
		var topic domain.Topic
		if err := tx.Preload("Markers").First(&topic, id).Error; err != nil {
			if errors.Is(err, gorm.ErrRecordNotFound) {
				return ErrNotFound
			}
			return err
		}

		markerIDs := []uint{}
		for _, m := range topic.Markers {
			markerIDs = append(markerIDs, m.ID)
		}

		if err := tx.Model(&topic).Association("Markers").Clear(); err != nil {
			return err
		}

		if err := tx.Delete(&topic).Error; err != nil {
			return err
		}

		if len(markerIDs) > 0 {
			if err := tx.Delete(&domain.Marker{}, markerIDs).Error; err != nil {
				return err
			}
		}

		return nil
	})
}

func (r *Repository) CreateMarker(m *domain.Marker) error { return r.db.Create(m).Error }
func (r *Repository) GetMarker(id uint) (*domain.Marker, error) {
	var m domain.Marker
	err := r.db.First(&m, id).Error
	return &m, err
}
func (r *Repository) GetMarkerByName(name string) (*domain.Marker, error) {
	var m domain.Marker
	err := r.db.Where("name = ?", name).First(&m).Error
	return &m, err
}
func (r *Repository) GetAllMarkers() ([]domain.Marker, error) {
	var markers []domain.Marker
	err := r.db.Find(&markers).Error
	return markers, err
}
func (r *Repository) UpdateMarker(m *domain.Marker) error { return r.db.Save(m).Error }
func (r *Repository) DeleteMarker(id uint) error {
	res := r.db.Delete(&domain.Marker{}, id)
	if res.Error != nil {
		return res.Error
	}
	if res.RowsAffected == 0 {
		return ErrNotFound
	}
	return nil
}

func (r *Repository) CreateNote(n *domain.Note) error { return r.db.Create(n).Error }
func (r *Repository) GetNote(id uint) (*domain.Note, error) {
	var n domain.Note
	err := r.db.First(&n, id).Error
	return &n, err
}
func (r *Repository) GetAllNotes() ([]domain.Note, error) {
	var notes []domain.Note
	err := r.db.Find(&notes).Error
	return notes, err
}
func (r *Repository) UpdateNote(n *domain.Note) error { return r.db.Save(n).Error }
func (r *Repository) DeleteNote(id uint) error {
	res := r.db.Delete(&domain.Note{}, id)
	if res.Error != nil {
		return res.Error
	}
	if res.RowsAffected == 0 {
		return ErrNotFound
	}
	return nil
}
