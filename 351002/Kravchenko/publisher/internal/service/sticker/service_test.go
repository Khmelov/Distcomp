package sticker

import (
	"context"
	"lab1/publisher/internal/repository"
	"testing"

	stickermodel "lab1/internal/model/sticker"

	"github.com/stretchr/testify/assert"
	"github.com/stretchr/testify/require"
)

func TestStickerService_CreateSticker(t *testing.T) {
	// Setup
	repos := repository.NewInMemory()
	service := New(repos)

	// Test data
	req := &stickermodel.CreateStickerRequest{
		Name: "teststicker",
	}

	// Execute
	sticker, err := service.CreateSticker(context.Background(), req)

	// Assert
	require.NoError(t, err)
	require.NotNil(t, sticker)

	assert.Equal(t, req.Name, sticker.Name)
	assert.Greater(t, sticker.ID, int64(0))
}

func TestStickerService_CreateSticker_DuplicateName(t *testing.T) {
	// Setup
	repos := repository.NewInMemory()
	service := New(repos)

	// Create first sticker
	req1 := &stickermodel.CreateStickerRequest{
		Name: "teststicker",
	}
	_, err1 := service.CreateSticker(context.Background(), req1)
	require.NoError(t, err1)

	// Try to create second sticker with same name
	req2 := &stickermodel.CreateStickerRequest{
		Name: "teststicker",
	}
	_, err2 := service.CreateSticker(context.Background(), req2)

	// Assert
	require.Error(t, err2)
	assert.Equal(t, ErrStickerNameTaken, err2)
}

func TestStickerService_GetSticker(t *testing.T) {
	// Setup
	repos := repository.NewInMemory()
	service := New(repos)

	// Create sticker first
	req := &stickermodel.CreateStickerRequest{
		Name: "teststicker",
	}
	created, err := service.CreateSticker(context.Background(), req)
	require.NoError(t, err)

	// Execute
	retrieved, err := service.GetSticker(context.Background(), created.ID)

	// Assert
	require.NoError(t, err)
	assert.Equal(t, created.ID, retrieved.ID)
	assert.Equal(t, created.Name, retrieved.Name)
}

func TestStickerService_GetSticker_NotFound(t *testing.T) {
	// Setup
	repos := repository.NewInMemory()
	service := New(repos)

	// Execute
	_, err := service.GetSticker(context.Background(), 999)

	// Assert
	require.Error(t, err)
	assert.Equal(t, "sticker not found", err.Error())
}

func TestStickerService_ListStickers(t *testing.T) {
	// Setup
	repos := repository.NewInMemory()
	service := New(repos)

	// Create some stickers
	stickers := []*stickermodel.CreateStickerRequest{
		{Name: "sticker1"},
		{Name: "sticker2"},
		{Name: "sticker3"},
	}

	for _, req := range stickers {
		_, err := service.CreateSticker(context.Background(), req)
		require.NoError(t, err)
	}

	// Execute
	list, err := service.ListStickers(context.Background())

	// Assert
	require.NoError(t, err)
	assert.Equal(t, len(stickers), len(list))
}

func TestStickerService_UpdateSticker(t *testing.T) {
	// Setup
	repos := repository.NewInMemory()
	service := New(repos)

	// Create sticker first
	createReq := &stickermodel.CreateStickerRequest{
		Name: "teststicker",
	}
	created, err := service.CreateSticker(context.Background(), createReq)
	require.NoError(t, err)

	// Update sticker
	name := "updatedsticker"
	updateReq := &stickermodel.UpdateStickerRequest{
		Name: &name,
	}
	updated, err := service.UpdateSticker(context.Background(), created.ID, updateReq)

	// Assert
	require.NoError(t, err)
	assert.Equal(t, name, updated.Name)
}

func TestStickerService_UpdateSticker_NotFound(t *testing.T) {
	// Setup
	repos := repository.NewInMemory()
	service := New(repos)

	// Update sticker
	name := "updatedsticker"
	updateReq := &stickermodel.UpdateStickerRequest{
		Name: &name,
	}

	// Execute
	_, err := service.UpdateSticker(context.Background(), 999, updateReq)

	// Assert
	require.Error(t, err)
	assert.Equal(t, "sticker not found", err.Error())
}

func TestStickerService_DeleteSticker(t *testing.T) {
	// Setup
	repos := repository.NewInMemory()
	service := New(repos)

	// Create sticker first
	req := &stickermodel.CreateStickerRequest{
		Name: "teststicker",
	}
	created, err := service.CreateSticker(context.Background(), req)
	require.NoError(t, err)

	// Delete sticker
	err = service.DeleteSticker(context.Background(), created.ID)

	// Assert
	require.NoError(t, err)

	// Verify sticker is deleted
	_, err = service.GetSticker(context.Background(), created.ID)
	require.Error(t, err)
	assert.Equal(t, "sticker not found", err.Error())
}

func TestStickerService_DeleteSticker_NotFound(t *testing.T) {
	// Setup
	repos := repository.NewInMemory()
	service := New(repos)

	// Execute
	err := service.DeleteSticker(context.Background(), 999)

	// Assert
	require.Error(t, err)
	assert.Equal(t, "sticker not found", err.Error())
}
