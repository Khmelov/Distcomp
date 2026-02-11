package controller

import (
	"bytes"
	"encoding/json"
	"net/http"
	"net/http/httptest"
	"testing"

	"github.com/bsuir/rest-api/internal/controller"
	"github.com/bsuir/rest-api/internal/dto/request"
	"github.com/bsuir/rest-api/internal/dto/response"
	"github.com/bsuir/rest-api/internal/repository/inmemory"
	"github.com/bsuir/rest-api/internal/service"
	"github.com/gin-gonic/gin"
	"github.com/stretchr/testify/assert"
)

func setupNoteRouter() *gin.Engine {
	gin.SetMode(gin.TestMode)
	authorRepo := inmemory.NewAuthorRepositoryInMemory()
	issueRepo := inmemory.NewIssueRepositoryInMemory()
	noteRepo := inmemory.NewNoteRepositoryInMemory()

	authorService := service.NewAuthorService(authorRepo)
	issueService := service.NewIssueService(issueRepo, authorRepo, nil)
	noteService := service.NewNoteService(noteRepo, issueRepo)

	authorController := controller.NewAuthorController(authorService)
	issueController := controller.NewIssueController(issueService)
	noteController := controller.NewNoteController(noteService)

	r := gin.New()
	v1 := r.Group("/api/v1.0")
	{
		authors := v1.Group("/authors")
		{
			authors.POST("", authorController.Create)
		}
		issues := v1.Group("/issues")
		{
			issues.POST("", issueController.Create)
			issues.GET("/:id/notes", noteController.GetByIssueID)
		}
		notes := v1.Group("/notes")
		{
			notes.POST("", noteController.Create)
			notes.GET("", noteController.GetAll)
			notes.GET("/:id", noteController.GetByID)
			notes.PUT("/:id", noteController.Update)
			notes.DELETE("/:id", noteController.Delete)
		}
	}
	return r
}

func TestNoteController_Create(t *testing.T) {
	router := setupNoteRouter()

	// Сначала создаем автора и задачу
	authorReq := request.AuthorRequestTo{
		Login:     "testuser",
		Password:  "password123",
		Firstname: "John",
		Lastname:  "Doe",
	}
	jsonValue, _ := json.Marshal(authorReq)
	req, _ := http.NewRequest("POST", "/api/v1.0/authors", bytes.NewBuffer(jsonValue))
	req.Header.Set("Content-Type", "application/json")
	w := httptest.NewRecorder()
	router.ServeHTTP(w, req)
	var author response.AuthorResponseTo
	json.Unmarshal(w.Body.Bytes(), &author)

	issueReq := request.IssueRequestTo{
		Title:    "Test Issue",
		Content:  "Test Content",
		AuthorID: author.ID,
	}
	jsonValue, _ = json.Marshal(issueReq)
	req, _ = http.NewRequest("POST", "/api/v1.0/issues", bytes.NewBuffer(jsonValue))
	req.Header.Set("Content-Type", "application/json")
	w = httptest.NewRecorder()
	router.ServeHTTP(w, req)
	var issue response.IssueResponseTo
	json.Unmarshal(w.Body.Bytes(), &issue)

	// Создаем заметку
	reqBody := request.NoteRequestTo{
		Content: "Test Note Content",
		IssueID: issue.ID,
	}
	jsonValue, _ = json.Marshal(reqBody)
	req, _ = http.NewRequest("POST", "/api/v1.0/notes", bytes.NewBuffer(jsonValue))
	req.Header.Set("Content-Type", "application/json")
	w = httptest.NewRecorder()
	router.ServeHTTP(w, req)

	assert.Equal(t, http.StatusCreated, w.Code)
	var note response.NoteResponseTo
	json.Unmarshal(w.Body.Bytes(), &note)
	assert.NotEmpty(t, note.ID)
	assert.Equal(t, "Test Note Content", note.Content)
	assert.Equal(t, issue.ID, note.IssueID)
}

func TestNoteController_GetByID(t *testing.T) {
	router := setupNoteRouter()

	// Создаем автора, задачу и заметку
	authorReq := request.AuthorRequestTo{
		Login:     "testuser",
		Password:  "password123",
		Firstname: "John",
		Lastname:  "Doe",
	}
	jsonValue, _ := json.Marshal(authorReq)
	req, _ := http.NewRequest("POST", "/api/v1.0/authors", bytes.NewBuffer(jsonValue))
	req.Header.Set("Content-Type", "application/json")
	w := httptest.NewRecorder()
	router.ServeHTTP(w, req)
	var author response.AuthorResponseTo
	json.Unmarshal(w.Body.Bytes(), &author)

	issueReq := request.IssueRequestTo{
		Title:    "Test Issue",
		Content:  "Test Content",
		AuthorID: author.ID,
	}
	jsonValue, _ = json.Marshal(issueReq)
	req, _ = http.NewRequest("POST", "/api/v1.0/issues", bytes.NewBuffer(jsonValue))
	req.Header.Set("Content-Type", "application/json")
	w = httptest.NewRecorder()
	router.ServeHTTP(w, req)
	var issue response.IssueResponseTo
	json.Unmarshal(w.Body.Bytes(), &issue)

	noteReq := request.NoteRequestTo{
		Content: "Test Note",
		IssueID: issue.ID,
	}
	jsonValue, _ = json.Marshal(noteReq)
	req, _ = http.NewRequest("POST", "/api/v1.0/notes", bytes.NewBuffer(jsonValue))
	req.Header.Set("Content-Type", "application/json")
	w = httptest.NewRecorder()
	router.ServeHTTP(w, req)
	var createdNote response.NoteResponseTo
	json.Unmarshal(w.Body.Bytes(), &createdNote)

	// Получаем заметку по ID
	req, _ = http.NewRequest("GET", "/api/v1.0/notes/"+createdNote.ID, nil)
	w = httptest.NewRecorder()
	router.ServeHTTP(w, req)

	assert.Equal(t, http.StatusOK, w.Code)
	var note response.NoteResponseTo
	json.Unmarshal(w.Body.Bytes(), &note)
	assert.Equal(t, createdNote.ID, note.ID)
	assert.Equal(t, "Test Note", note.Content)
}

func TestNoteController_GetAll(t *testing.T) {
	router := setupNoteRouter()

	// Создаем автора и задачу
	authorReq := request.AuthorRequestTo{
		Login:     "testuser",
		Password:  "password123",
		Firstname: "John",
		Lastname:  "Doe",
	}
	jsonValue, _ := json.Marshal(authorReq)
	req, _ := http.NewRequest("POST", "/api/v1.0/authors", bytes.NewBuffer(jsonValue))
	req.Header.Set("Content-Type", "application/json")
	w := httptest.NewRecorder()
	router.ServeHTTP(w, req)
	var author response.AuthorResponseTo
	json.Unmarshal(w.Body.Bytes(), &author)

	issueReq := request.IssueRequestTo{
		Title:    "Test Issue",
		Content:  "Test Content",
		AuthorID: author.ID,
	}
	jsonValue, _ = json.Marshal(issueReq)
	req, _ = http.NewRequest("POST", "/api/v1.0/issues", bytes.NewBuffer(jsonValue))
	req.Header.Set("Content-Type", "application/json")
	w = httptest.NewRecorder()
	router.ServeHTTP(w, req)
	var issue response.IssueResponseTo
	json.Unmarshal(w.Body.Bytes(), &issue)

	// Создаем несколько заметок
	for i := 0; i < 3; i++ {
		noteReq := request.NoteRequestTo{
			Content: "Note " + string(rune('0'+i)),
			IssueID: issue.ID,
		}
		jsonValue, _ = json.Marshal(noteReq)
		req, _ = http.NewRequest("POST", "/api/v1.0/notes", bytes.NewBuffer(jsonValue))
		req.Header.Set("Content-Type", "application/json")
		w = httptest.NewRecorder()
		router.ServeHTTP(w, req)
	}

	// Получаем все заметки
	req, _ = http.NewRequest("GET", "/api/v1.0/notes", nil)
	w = httptest.NewRecorder()
	router.ServeHTTP(w, req)

	assert.Equal(t, http.StatusOK, w.Code)
	var notes []response.NoteResponseTo
	json.Unmarshal(w.Body.Bytes(), &notes)
	assert.GreaterOrEqual(t, len(notes), 3)
}

func TestNoteController_Update(t *testing.T) {
	router := setupNoteRouter()

	// Создаем автора, задачу и заметку
	authorReq := request.AuthorRequestTo{
		Login:     "testuser",
		Password:  "password123",
		Firstname: "John",
		Lastname:  "Doe",
	}
	jsonValue, _ := json.Marshal(authorReq)
	req, _ := http.NewRequest("POST", "/api/v1.0/authors", bytes.NewBuffer(jsonValue))
	req.Header.Set("Content-Type", "application/json")
	w := httptest.NewRecorder()
	router.ServeHTTP(w, req)
	var author response.AuthorResponseTo
	json.Unmarshal(w.Body.Bytes(), &author)

	issueReq := request.IssueRequestTo{
		Title:    "Test Issue",
		Content:  "Test Content",
		AuthorID: author.ID,
	}
	jsonValue, _ = json.Marshal(issueReq)
	req, _ = http.NewRequest("POST", "/api/v1.0/issues", bytes.NewBuffer(jsonValue))
	req.Header.Set("Content-Type", "application/json")
	w = httptest.NewRecorder()
	router.ServeHTTP(w, req)
	var issue response.IssueResponseTo
	json.Unmarshal(w.Body.Bytes(), &issue)

	noteReq := request.NoteRequestTo{
		Content: "Test Note",
		IssueID: issue.ID,
	}
	jsonValue, _ = json.Marshal(noteReq)
	req, _ = http.NewRequest("POST", "/api/v1.0/notes", bytes.NewBuffer(jsonValue))
	req.Header.Set("Content-Type", "application/json")
	w = httptest.NewRecorder()
	router.ServeHTTP(w, req)
	var createdNote response.NoteResponseTo
	json.Unmarshal(w.Body.Bytes(), &createdNote)

	// Обновляем заметку
	updateBody := request.NoteRequestTo{
		Content: "Updated Note",
		IssueID: issue.ID,
	}
	jsonValue, _ = json.Marshal(updateBody)
	req, _ = http.NewRequest("PUT", "/api/v1.0/notes/"+createdNote.ID, bytes.NewBuffer(jsonValue))
	req.Header.Set("Content-Type", "application/json")
	w = httptest.NewRecorder()
	router.ServeHTTP(w, req)

	assert.Equal(t, http.StatusOK, w.Code)
	var note response.NoteResponseTo
	json.Unmarshal(w.Body.Bytes(), &note)
	assert.Equal(t, "Updated Note", note.Content)
}

func TestNoteController_Delete(t *testing.T) {
	router := setupNoteRouter()

	// Создаем автора, задачу и заметку
	authorReq := request.AuthorRequestTo{
		Login:     "testuser",
		Password:  "password123",
		Firstname: "John",
		Lastname:  "Doe",
	}
	jsonValue, _ := json.Marshal(authorReq)
	req, _ := http.NewRequest("POST", "/api/v1.0/authors", bytes.NewBuffer(jsonValue))
	req.Header.Set("Content-Type", "application/json")
	w := httptest.NewRecorder()
	router.ServeHTTP(w, req)
	var author response.AuthorResponseTo
	json.Unmarshal(w.Body.Bytes(), &author)

	issueReq := request.IssueRequestTo{
		Title:    "Test Issue",
		Content:  "Test Content",
		AuthorID: author.ID,
	}
	jsonValue, _ = json.Marshal(issueReq)
	req, _ = http.NewRequest("POST", "/api/v1.0/issues", bytes.NewBuffer(jsonValue))
	req.Header.Set("Content-Type", "application/json")
	w = httptest.NewRecorder()
	router.ServeHTTP(w, req)
	var issue response.IssueResponseTo
	json.Unmarshal(w.Body.Bytes(), &issue)

	noteReq := request.NoteRequestTo{
		Content: "Test Note",
		IssueID: issue.ID,
	}
	jsonValue, _ = json.Marshal(noteReq)
	req, _ = http.NewRequest("POST", "/api/v1.0/notes", bytes.NewBuffer(jsonValue))
	req.Header.Set("Content-Type", "application/json")
	w = httptest.NewRecorder()
	router.ServeHTTP(w, req)
	var createdNote response.NoteResponseTo
	json.Unmarshal(w.Body.Bytes(), &createdNote)

	// Удаляем заметку
	req, _ = http.NewRequest("DELETE", "/api/v1.0/notes/"+createdNote.ID, nil)
	w = httptest.NewRecorder()
	router.ServeHTTP(w, req)

	assert.Equal(t, http.StatusNoContent, w.Code)

	// Проверяем, что заметка удалена
	req, _ = http.NewRequest("GET", "/api/v1.0/notes/"+createdNote.ID, nil)
	w = httptest.NewRecorder()
	router.ServeHTTP(w, req)
	assert.Equal(t, http.StatusNotFound, w.Code)
}

func TestNoteController_GetByIssueID(t *testing.T) {
	router := setupNoteRouter()

	// Создаем автора и задачу
	authorReq := request.AuthorRequestTo{
		Login:     "testuser",
		Password:  "password123",
		Firstname: "John",
		Lastname:  "Doe",
	}
	jsonValue, _ := json.Marshal(authorReq)
	req, _ := http.NewRequest("POST", "/api/v1.0/authors", bytes.NewBuffer(jsonValue))
	req.Header.Set("Content-Type", "application/json")
	w := httptest.NewRecorder()
	router.ServeHTTP(w, req)
	var author response.AuthorResponseTo
	json.Unmarshal(w.Body.Bytes(), &author)

	issueReq := request.IssueRequestTo{
		Title:    "Test Issue",
		Content:  "Test Content",
		AuthorID: author.ID,
	}
	jsonValue, _ = json.Marshal(issueReq)
	req, _ = http.NewRequest("POST", "/api/v1.0/issues", bytes.NewBuffer(jsonValue))
	req.Header.Set("Content-Type", "application/json")
	w = httptest.NewRecorder()
	router.ServeHTTP(w, req)
	var issue response.IssueResponseTo
	json.Unmarshal(w.Body.Bytes(), &issue)

	// Создаем несколько заметок для этой задачи
	for i := 0; i < 2; i++ {
		noteReq := request.NoteRequestTo{
			Content: "Note " + string(rune('0'+i)),
			IssueID: issue.ID,
		}
		jsonValue, _ = json.Marshal(noteReq)
		req, _ = http.NewRequest("POST", "/api/v1.0/notes", bytes.NewBuffer(jsonValue))
		req.Header.Set("Content-Type", "application/json")
		w = httptest.NewRecorder()
		router.ServeHTTP(w, req)
	}

	// Получаем заметки по ID задачи
	req, _ = http.NewRequest("GET", "/api/v1.0/issues/"+issue.ID+"/notes", nil)
	w = httptest.NewRecorder()
	router.ServeHTTP(w, req)

	assert.Equal(t, http.StatusOK, w.Code)
	var notes []response.NoteResponseTo
	json.Unmarshal(w.Body.Bytes(), &notes)
	assert.Equal(t, 2, len(notes))
}

