package editor

import (
	"context"
	"testing"

	editormodel "lab1/internal/model/editor"
	"lab1/internal/repository"

	"github.com/stretchr/testify/assert"
	"github.com/stretchr/testify/require"
)

func TestEditorService_CreateEditor(t *testing.T) {
	// Setup
	repos := repository.NewInMemory()
	service := New(repos)

	// Test data
	req := &editormodel.CreateEditorRequest{
		Login:    "testuser",
		Password: "password123",
	}

	// Execute
	editor, err := service.CreateEditor(context.Background(), req)

	// Assert
	require.NoError(t, err)
	require.NotNil(t, editor)

	assert.Equal(t, req.Login, editor.Login)
	assert.Equal(t, req.Password, editor.Password)
	assert.Greater(t, editor.ID, int64(0))
}

func TestEditorService_CreateEditor_DuplicateLogin(t *testing.T) {
	// Setup
	repos := repository.NewInMemory()
	service := New(repos)

	// Create first editor
	req1 := &editormodel.CreateEditorRequest{
		Login:    "testuser",
		Password: "password123",
	}
	_, err1 := service.CreateEditor(context.Background(), req1)
	require.NoError(t, err1)

	// Try to create second editor with same login
	req2 := &editormodel.CreateEditorRequest{
		Login:    "testuser",
		Password: "differentpassword",
	}
	_, err2 := service.CreateEditor(context.Background(), req2)

	// Assert
	require.Error(t, err2)
	assert.Equal(t, ErrEditorLoginTaken, err2)
}

func TestEditorService_GetEditor(t *testing.T) {
	// Setup
	repos := repository.NewInMemory()
	service := New(repos)

	// Create editor first
	req := &editormodel.CreateEditorRequest{
		Login:    "testuser",
		Password: "password123",
	}
	created, err := service.CreateEditor(context.Background(), req)
	require.NoError(t, err)

	// Execute
	retrieved, err := service.GetEditor(context.Background(), created.ID)

	// Assert
	require.NoError(t, err)
	assert.Equal(t, created.ID, retrieved.ID)
	assert.Equal(t, created.Login, retrieved.Login)
	assert.Equal(t, created.Password, retrieved.Password)
}

func TestEditorService_GetEditor_NotFound(t *testing.T) {
	// Setup
	repos := repository.NewInMemory()
	service := New(repos)

	// Execute
	_, err := service.GetEditor(context.Background(), 999)

	// Assert
	require.Error(t, err)
	assert.Equal(t, ErrEditorNotFound, err)
}

func TestEditorService_ListEditors(t *testing.T) {
	// Setup
	repos := repository.NewInMemory()
	service := New(repos)

	// Create some editors
	editors := []*editormodel.CreateEditorRequest{
		{Login: "user1", Password: "pass1"},
		{Login: "user2", Password: "pass2"},
		{Login: "user3", Password: "pass3"},
	}

	for _, req := range editors {
		_, err := service.CreateEditor(context.Background(), req)
		require.NoError(t, err)
	}

	// Execute
	list, err := service.ListEditors(context.Background())

	// Assert
	require.NoError(t, err)
	assert.Equal(t, len(editors), len(list))
}

func TestEditorService_UpdateEditor(t *testing.T) {
	// Setup
	repos := repository.NewInMemory()
	service := New(repos)

	// Create editor first
	createReq := &editormodel.CreateEditorRequest{
		Login:    "testuser",
		Password: "password123",
	}
	created, err := service.CreateEditor(context.Background(), createReq)
	require.NoError(t, err)

	// Update editor
	login := "updateduser"
	password := "newpassword"
	updateReq := &editormodel.UpdateEditorRequest{
		Login:    &login,
		Password: &password,
	}
	updated, err := service.UpdateEditor(context.Background(), created.ID, updateReq)

	// Assert
	require.NoError(t, err)
	assert.Equal(t, login, updated.Login)
	assert.Equal(t, password, updated.Password)
}

func TestEditorService_DeleteEditor(t *testing.T) {
	// Setup
	repos := repository.NewInMemory()
	service := New(repos)

	// Create editor first
	req := &editormodel.CreateEditorRequest{
		Login:    "testuser",
		Password: "password123",
	}
	created, err := service.CreateEditor(context.Background(), req)
	require.NoError(t, err)

	// Delete editor
	err = service.DeleteEditor(context.Background(), created.ID)

	// Assert
	require.NoError(t, err)

	// Verify editor is deleted
	_, err = service.GetEditor(context.Background(), created.ID)
	require.Error(t, err)
	assert.Equal(t, ErrEditorNotFound, err)
}
