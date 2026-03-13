package note

import (
	"context"
	"testing"

	editormodel "lab1/internal/model/editor"
	issuemodel "lab1/internal/model/issue"
	notemodel "lab1/internal/model/note"
	"lab1/internal/repository"
	editor "lab1/internal/service/editor"
	issue "lab1/internal/service/issue"

	"github.com/stretchr/testify/assert"
	"github.com/stretchr/testify/require"
)

func TestNoteService_CreateNote(t *testing.T) {
	// Setup
	repos := repository.NewInMemory()
	noteService := New(repos)
	editorService := editor.New(repos)
	issueService := issue.New(repos)

	// Create an editor first
	editorReq := &editormodel.CreateEditorRequest{
		Login:    "testuser",
		Password: "password123",
	}
	editor, err := editorService.CreateEditor(context.Background(), editorReq)
	require.NoError(t, err)

	// Create an issue first
	issueReq := &issuemodel.CreateIssueRequest{
		EditorID: editor.ID,
		Title:    "Test Issue",
		Content:  "Test content",
		Stickers: []int64{},
	}
	issue, err := issueService.CreateIssue(context.Background(), issueReq)
	require.NoError(t, err)

	// Test data
	req := &notemodel.CreateNoteRequest{
		IssueID: issue.ID,
		Content: "Test note content",
	}

	// Execute
	note, err := noteService.CreateNote(context.Background(), req)

	// Assert
	require.NoError(t, err)
	require.NotNil(t, note)
	assert.Equal(t, req.Content, note.Content)
	assert.Equal(t, req.IssueID, note.IssueID)
	assert.Greater(t, note.ID, int64(0))
}

func TestNoteService_CreateNote_IssueNotFound(t *testing.T) {
	// Setup
	repos := repository.NewInMemory()
	noteService := New(repos)

	// Test data with non-existent issue
	req := &notemodel.CreateNoteRequest{
		IssueID: 999,
		Content: "Test note content",
	}

	// Execute
	_, err := noteService.CreateNote(context.Background(), req)

	// Assert
	require.Error(t, err)
	assert.Equal(t, ErrIssueNotFoundForNote, err)
}

func TestNoteService_GetNote(t *testing.T) {
	// Setup
	repos := repository.NewInMemory()
	noteService := New(repos)
	editorService := editor.New(repos)
	issueService := issue.New(repos)

	// Create an editor first
	editorReq := &editormodel.CreateEditorRequest{
		Login:    "testuser",
		Password: "password123",
	}
	editor, err := editorService.CreateEditor(context.Background(), editorReq)
	require.NoError(t, err)

	// Create an issue first
	issueReq := &issuemodel.CreateIssueRequest{
		EditorID: editor.ID,
		Title:    "Test Issue",
		Content:  "Test content",
		Stickers: []int64{},
	}
	issue, err := issueService.CreateIssue(context.Background(), issueReq)
	require.NoError(t, err)

	// Create a note first
	req := &notemodel.CreateNoteRequest{
		IssueID: issue.ID,
		Content: "Test note content",
	}
	created, err := noteService.CreateNote(context.Background(), req)
	require.NoError(t, err)

	// Execute
	retrieved, err := noteService.GetNote(context.Background(), created.ID)

	// Assert
	require.NoError(t, err)
	assert.Equal(t, created.ID, retrieved.ID)
	assert.Equal(t, created.Content, retrieved.Content)
}

func TestNoteService_GetNote_NotFound(t *testing.T) {
	// Setup
	repos := repository.NewInMemory()
	noteService := New(repos)

	// Execute
	_, err := noteService.GetNote(context.Background(), 999)

	// Assert
	require.Error(t, err)
	assert.Equal(t, "note not found", err.Error())
}

func TestNoteService_ListNotes(t *testing.T) {
	// Setup
	repos := repository.NewInMemory()
	noteService := New(repos)
	editorService := editor.New(repos)
	issueService := issue.New(repos)

	// Create an editor first
	editorReq := &editormodel.CreateEditorRequest{
		Login:    "testuser",
		Password: "password123",
	}
	editor, err := editorService.CreateEditor(context.Background(), editorReq)
	require.NoError(t, err)

	// Create an issue first
	issueReq := &issuemodel.CreateIssueRequest{
		EditorID: editor.ID,
		Title:    "Test Issue",
		Content:  "Test content",
		Stickers: []int64{},
	}
	issue, err := issueService.CreateIssue(context.Background(), issueReq)
	require.NoError(t, err)

	// Create some notes
	notes := []*notemodel.CreateNoteRequest{
		{IssueID: issue.ID, Content: "Note 1"},
		{IssueID: issue.ID, Content: "Note 2"},
		{IssueID: issue.ID, Content: "Note 3"},
	}

	for _, req := range notes {
		_, err := noteService.CreateNote(context.Background(), req)
		require.NoError(t, err)
	}

	// Execute
	list, err := noteService.ListNotes(context.Background())

	// Assert
	require.NoError(t, err)
	assert.Equal(t, len(notes), len(list))
}

func TestNoteService_UpdateNote(t *testing.T) {
	// Setup
	repos := repository.NewInMemory()
	noteService := New(repos)
	editorService := editor.New(repos)
	issueService := issue.New(repos)

	// Create an editor first
	editorReq := &editormodel.CreateEditorRequest{
		Login:    "testuser",
		Password: "password123",
	}
	editor, err := editorService.CreateEditor(context.Background(), editorReq)
	require.NoError(t, err)

	// Create an issue first
	issueReq := &issuemodel.CreateIssueRequest{
		EditorID: editor.ID,
		Title:    "Test Issue",
		Content:  "Test content",
		Stickers: []int64{},
	}
	issue, err := issueService.CreateIssue(context.Background(), issueReq)
	require.NoError(t, err)

	// Create a note first
	createReq := &notemodel.CreateNoteRequest{
		IssueID: issue.ID,
		Content: "Test note content",
	}
	created, err := noteService.CreateNote(context.Background(), createReq)
	require.NoError(t, err)

	// Update note
	content := "Updated note content"
	updateReq := &notemodel.UpdateNoteRequest{
		Content: &content,
	}
	updated, err := noteService.UpdateNote(context.Background(), created.ID, updateReq)

	// Assert
	require.NoError(t, err)
	assert.Equal(t, content, updated.Content)
}

func TestNoteService_UpdateNote_NotFound(t *testing.T) {
	// Setup
	repos := repository.NewInMemory()
	noteService := New(repos)

	// Update note
	content := "Updated note content"
	updateReq := &notemodel.UpdateNoteRequest{
		Content: &content,
	}

	// Execute
	_, err := noteService.UpdateNote(context.Background(), 999, updateReq)

	// Assert
	require.Error(t, err)
	assert.Equal(t, "note not found", err.Error())
}

func TestNoteService_DeleteNote(t *testing.T) {
	// Setup
	repos := repository.NewInMemory()
	noteService := New(repos)
	editorService := editor.New(repos)
	issueService := issue.New(repos)

	// Create an editor first
	editorReq := &editormodel.CreateEditorRequest{
		Login:    "testuser",
		Password: "password123",
	}
	editor, err := editorService.CreateEditor(context.Background(), editorReq)
	require.NoError(t, err)

	// Create an issue first
	issueReq := &issuemodel.CreateIssueRequest{
		EditorID: editor.ID,
		Title:    "Test Issue",
		Content:  "Test content",
		Stickers: []int64{},
	}
	issue, err := issueService.CreateIssue(context.Background(), issueReq)
	require.NoError(t, err)

	// Create a note first
	req := &notemodel.CreateNoteRequest{
		IssueID: issue.ID,
		Content: "Test note content",
	}
	created, err := noteService.CreateNote(context.Background(), req)
	require.NoError(t, err)

	// Delete note
	err = noteService.DeleteNote(context.Background(), created.ID)

	// Assert
	require.NoError(t, err)

	// Verify note is deleted
	_, err = noteService.GetNote(context.Background(), created.ID)
	require.Error(t, err)
}

func TestNoteService_DeleteNote_NotFound(t *testing.T) {
	// Setup
	repos := repository.NewInMemory()
	noteService := New(repos)

	// Execute
	err := noteService.DeleteNote(context.Background(), 999)

	// Assert
	require.Error(t, err)
	assert.Equal(t, "note not found", err.Error())
}
