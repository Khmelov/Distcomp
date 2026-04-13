package issue

import (
	"context"
	"lab1/publisher/internal/repository"
	"lab1/publisher/internal/service/editor"
	"testing"

	editormodel "lab1/internal/model/editor"
	issuemodel "lab1/internal/model/issue"

	"github.com/stretchr/testify/assert"
	"github.com/stretchr/testify/require"
)

func TestIssueService_CreateIssue(t *testing.T) {
	// Setup
	repos := repository.NewInMemory()
	issueService := New(repos)
	editorService := editor.New(repos)

	// Create an editor first
	editorReq := &editormodel.CreateEditorRequest{
		Login:    "testuser",
		Password: "password123",
	}
	editor, err := editorService.CreateEditor(context.Background(), editorReq)
	require.NoError(t, err)

	// Test data
	req := &issuemodel.CreateIssueRequest{
		EditorID: editor.ID,
		Title:    "Test Issue",
		Content:  "Test content",
		Stickers: []int64{},
	}

	// Execute
	issue, err := issueService.CreateIssue(context.Background(), req)

	// Assert
	require.NoError(t, err)
	require.NotNil(t, issue)
	assert.Equal(t, req.Title, issue.Title)
	assert.Equal(t, req.Content, issue.Content)
	assert.Equal(t, req.EditorID, issue.EditorID)
	assert.Greater(t, issue.ID, int64(0))
}

func TestIssueService_CreateIssue_EditorNotFound(t *testing.T) {
	// Setup
	repos := repository.NewInMemory()
	issueService := New(repos)

	// Test data with non-existent editor
	req := &issuemodel.CreateIssueRequest{
		EditorID: 999,
		Title:    "Test Issue",
		Content:  "Test content",
		Stickers: []int64{},
	}

	// Execute
	_, err := issueService.CreateIssue(context.Background(), req)

	// Assert
	require.Error(t, err)
	assert.Equal(t, "editor not found", err.Error())
}

func TestIssueService_GetIssue(t *testing.T) {
	// Setup
	repos := repository.NewInMemory()
	issueService := New(repos)
	editorService := editor.New(repos)

	// Create an editor first
	editorReq := &editormodel.CreateEditorRequest{
		Login:    "testuser",
		Password: "password123",
	}
	editor, err := editorService.CreateEditor(context.Background(), editorReq)
	require.NoError(t, err)

	// Create an issue first
	req := &issuemodel.CreateIssueRequest{
		EditorID: editor.ID,
		Title:    "Test Issue",
		Content:  "Test content",
		Stickers: []int64{},
	}
	created, err := issueService.CreateIssue(context.Background(), req)
	require.NoError(t, err)

	// Execute
	retrieved, err := issueService.GetIssue(context.Background(), created.ID)

	// Assert
	require.NoError(t, err)
	assert.Equal(t, created.ID, retrieved.ID)
	assert.Equal(t, created.Title, retrieved.Title)
	assert.Equal(t, created.Content, retrieved.Content)
}

func TestIssueService_GetIssue_NotFound(t *testing.T) {
	// Setup
	repos := repository.NewInMemory()
	issueService := New(repos)

	// Execute
	_, err := issueService.GetIssue(context.Background(), 999)

	// Assert
	require.Error(t, err)
	assert.Equal(t, "issue not found", err.Error())
}

func TestIssueService_ListIssues(t *testing.T) {
	// Setup
	repos := repository.NewInMemory()
	issueService := New(repos)
	editorService := editor.New(repos)

	// Create an editor first
	editorReq := &editormodel.CreateEditorRequest{
		Login:    "testuser",
		Password: "password123",
	}
	editor, err := editorService.CreateEditor(context.Background(), editorReq)
	require.NoError(t, err)

	// Create some issues
	issues := []*issuemodel.CreateIssueRequest{
		{EditorID: editor.ID, Title: "Issue 1", Content: "Content 1", Stickers: []int64{}},
		{EditorID: editor.ID, Title: "Issue 2", Content: "Content 2", Stickers: []int64{}},
		{EditorID: editor.ID, Title: "Issue 3", Content: "Content 3", Stickers: []int64{}},
	}

	for _, req := range issues {
		_, err := issueService.CreateIssue(context.Background(), req)
		require.NoError(t, err)
	}

	// Execute
	list, err := issueService.ListIssues(context.Background())

	// Assert
	require.NoError(t, err)
	assert.Equal(t, len(issues), len(list))
}

func TestIssueService_UpdateIssue(t *testing.T) {
	// Setup
	repos := repository.NewInMemory()
	issueService := New(repos)
	editorService := editor.New(repos)

	// Create an editor first
	editorReq := &editormodel.CreateEditorRequest{
		Login:    "testuser",
		Password: "password123",
	}
	editor, err := editorService.CreateEditor(context.Background(), editorReq)
	require.NoError(t, err)

	// Create an issue first
	createReq := &issuemodel.CreateIssueRequest{
		EditorID: editor.ID,
		Title:    "Test Issue",
		Content:  "Test content",
		Stickers: []int64{},
	}
	created, err := issueService.CreateIssue(context.Background(), createReq)
	require.NoError(t, err)

	// Update issue
	title := "Updated Issue"
	content := "Updated content"
	updateReq := &issuemodel.UpdateIssueRequest{
		Title:   &title,
		Content: &content,
	}
	updated, err := issueService.UpdateIssue(context.Background(), created.ID, updateReq)

	// Assert
	require.NoError(t, err)
	assert.Equal(t, title, updated.Title)
	assert.Equal(t, content, updated.Content)
}

func TestIssueService_UpdateIssue_NotFound(t *testing.T) {
	// Setup
	repos := repository.NewInMemory()
	issueService := New(repos)

	// Update issue
	title := "Updated Issue"
	content := "Updated content"
	updateReq := &issuemodel.UpdateIssueRequest{
		Title:   &title,
		Content: &content,
	}

	// Execute
	_, err := issueService.UpdateIssue(context.Background(), 999, updateReq)

	// Assert
	require.Error(t, err)
	assert.Equal(t, "issue not found", err.Error())
}

func TestIssueService_DeleteIssue(t *testing.T) {
	// Setup
	repos := repository.NewInMemory()
	issueService := New(repos)
	editorService := editor.New(repos)

	// Create an editor first
	editorReq := &editormodel.CreateEditorRequest{
		Login:    "testuser",
		Password: "password123",
	}
	editor, err := editorService.CreateEditor(context.Background(), editorReq)
	require.NoError(t, err)

	// Create an issue first
	req := &issuemodel.CreateIssueRequest{
		EditorID: editor.ID,
		Title:    "Test Issue",
		Content:  "Test content",
		Stickers: []int64{},
	}
	created, err := issueService.CreateIssue(context.Background(), req)
	require.NoError(t, err)

	// Delete issue
	err = issueService.DeleteIssue(context.Background(), created.ID)

	// Assert
	require.NoError(t, err)

	// Verify issue is deleted
	_, err = issueService.GetIssue(context.Background(), created.ID)
	require.Error(t, err)
}

func TestIssueService_DeleteIssue_NotFound(t *testing.T) {
	// Setup
	repos := repository.NewInMemory()
	issueService := New(repos)

	// Execute
	err := issueService.DeleteIssue(context.Background(), 999)

	// Assert
	require.Error(t, err)
	assert.Equal(t, "issue not found", err.Error())
}
