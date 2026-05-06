package domain

import "errors"

var (
	ErrUserNotFound       = errors.New("user not found")
	ErrUserLoginNotUnique = errors.New("user login already exists")
	ErrInvalidCredentials = errors.New("invalid user credentials")
	ErrUnauthorized       = errors.New("unauthorized")
	ErrForbidden          = errors.New("forbidden")

	ErrNewsNotFound     = errors.New("news not found")
	ErrNewsUserNotFound = errors.New("user for news not found")
	ErrNewsDuplicate    = errors.New("news with same title and content already exists for this user")

	ErrMarkerNotFound      = errors.New("marker not found")
	ErrMarkerAlreadyExists = errors.New("marker already exists")
	ErrNewsMarkerDuplicate = errors.New("marker is already attached to news")

	ErrNoticeNotFound = errors.New("notice not found")
)
