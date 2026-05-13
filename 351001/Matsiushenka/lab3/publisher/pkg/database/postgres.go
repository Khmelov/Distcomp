package database

import (
	"publisher/internal/domain"

	"gorm.io/driver/postgres"
	"gorm.io/gorm"
)

func InitDB() *gorm.DB {
	dsn := "host=localhost user=postgres password=postgres dbname=distcomp port=5432 sslmode=disable"
	db, err := gorm.Open(postgres.Open(dsn), &gorm.Config{})
	if err != nil {
		panic("Failed to connect to Postgres")
	}

	db.AutoMigrate(&domain.Editor{}, &domain.Marker{}, &domain.Topic{})
	return db
}
