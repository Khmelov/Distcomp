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

func setupIssueRouter() *gin.Engine {
	gin.SetMode(gin.TestMode)
	authorRepo := inmemory.NewAuthorRepositoryInMemory()
	issueRepo := inmemory.NewIssueRepositoryInMemory()
	markerRepo := inmemory.NewMarkerRepositoryInMemory()
	noteRepo := inmemory.NewNoteRepositoryInMemory()

	authorService := service.NewAuthorService(authorRepo)
	markerService := service.NewMarkerService(markerRepo)
	issueService := service.NewIssueService(issueRepo, authorRepo, markerRepo)
	noteService := service.NewNoteService(noteRepo, issueRepo)

	authorController := controller.NewAuthorController(authorService)
	issueController := controller.NewIssueController(issueService)
	markerController := controller.NewMarkerController(markerService)
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
			issues.GET("", issueController.GetAll)
			issues.GET("/:id", issueController.GetByID)
			issues.PUT("/:id", issueController.Update)
			issues.DELETE("/:id", issueController.Delete)
			issues.GET("/:id/author", issueController.GetAuthorByIssueID)
			issues.GET("/:id/markers", issueController.GetMarkersByIssueID)
			issues.GET("/:id/notes", noteController.GetByIssueID)
		}
		markers := v1.Group("/markers")
		{
			markers.POST("", markerController.Create)
		}
	}
	return r
}

func TestIssueController_Create(t *testing.T) {
	router := setupIssueRouter()

	// Сначала создаем автора
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

	// Создаем задачу
	reqBody := request.IssueRequestTo{
		Title:    "Test Issue",
		Content:  "Test Content",
		AuthorID: author.ID,
	}
	jsonValue, _ = json.Marshal(reqBody)
	req, _ = http.NewRequest("POST", "/api/v1.0/issues", bytes.NewBuffer(jsonValue))
	req.Header.Set("Content-Type", "application/json")
	w = httptest.NewRecorder()
	router.ServeHTTP(w, req)

	assert.Equal(t, http.StatusCreated, w.Code)
	var issue response.IssueResponseTo
	json.Unmarshal(w.Body.Bytes(), &issue)
	assert.NotEmpty(t, issue.ID)
	assert.Equal(t, "Test Issue", issue.Title)
	assert.Equal(t, author.ID, issue.AuthorID)
}

func TestIssueController_GetByID(t *testing.T) {
	router := setupIssueRouter()

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
	var createdIssue response.IssueResponseTo
	json.Unmarshal(w.Body.Bytes(), &createdIssue)

	// Получаем задачу по ID
	req, _ = http.NewRequest("GET", "/api/v1.0/issues/"+createdIssue.ID, nil)
	w = httptest.NewRecorder()
	router.ServeHTTP(w, req)

	assert.Equal(t, http.StatusOK, w.Code)
	var issue response.IssueResponseTo
	json.Unmarshal(w.Body.Bytes(), &issue)
	assert.Equal(t, createdIssue.ID, issue.ID)
	assert.Equal(t, "Test Issue", issue.Title)
}

func TestIssueController_GetAll(t *testing.T) {
	router := setupIssueRouter()

	// Создаем автора
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

	// Создаем несколько задач
	for i := 0; i < 3; i++ {
		issueReq := request.IssueRequestTo{
			Title:    "Issue " + string(rune('0'+i)),
			Content:  "Content",
			AuthorID: author.ID,
		}
		jsonValue, _ = json.Marshal(issueReq)
		req, _ = http.NewRequest("POST", "/api/v1.0/issues", bytes.NewBuffer(jsonValue))
		req.Header.Set("Content-Type", "application/json")
		w = httptest.NewRecorder()
		router.ServeHTTP(w, req)
	}

	// Получаем все задачи
	req, _ = http.NewRequest("GET", "/api/v1.0/issues", nil)
	w = httptest.NewRecorder()
	router.ServeHTTP(w, req)

	assert.Equal(t, http.StatusOK, w.Code)
	var issues []response.IssueResponseTo
	json.Unmarshal(w.Body.Bytes(), &issues)
	assert.GreaterOrEqual(t, len(issues), 3)
}

func TestIssueController_Update(t *testing.T) {
	router := setupIssueRouter()

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
	var createdIssue response.IssueResponseTo
	json.Unmarshal(w.Body.Bytes(), &createdIssue)

	// Обновляем задачу
	updateBody := request.IssueRequestTo{
		Title:    "Updated Issue",
		Content:  "Updated Content",
		AuthorID: author.ID,
	}
	jsonValue, _ = json.Marshal(updateBody)
	req, _ = http.NewRequest("PUT", "/api/v1.0/issues/"+createdIssue.ID, bytes.NewBuffer(jsonValue))
	req.Header.Set("Content-Type", "application/json")
	w = httptest.NewRecorder()
	router.ServeHTTP(w, req)

	assert.Equal(t, http.StatusOK, w.Code)
	var issue response.IssueResponseTo
	json.Unmarshal(w.Body.Bytes(), &issue)
	assert.Equal(t, "Updated Issue", issue.Title)
	assert.Equal(t, "Updated Content", issue.Content)
}

func TestIssueController_Delete(t *testing.T) {
	router := setupIssueRouter()

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
	var createdIssue response.IssueResponseTo
	json.Unmarshal(w.Body.Bytes(), &createdIssue)

	// Удаляем задачу
	req, _ = http.NewRequest("DELETE", "/api/v1.0/issues/"+createdIssue.ID, nil)
	w = httptest.NewRecorder()
	router.ServeHTTP(w, req)

	assert.Equal(t, http.StatusNoContent, w.Code)

	// Проверяем, что задача удалена
	req, _ = http.NewRequest("GET", "/api/v1.0/issues/"+createdIssue.ID, nil)
	w = httptest.NewRecorder()
	router.ServeHTTP(w, req)
	assert.Equal(t, http.StatusNotFound, w.Code)
}

func TestIssueController_GetAuthorByIssueID(t *testing.T) {
	router := setupIssueRouter()

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

	// Получаем автора по ID задачи
	req, _ = http.NewRequest("GET", "/api/v1.0/issues/"+issue.ID+"/author", nil)
	w = httptest.NewRecorder()
	router.ServeHTTP(w, req)

	assert.Equal(t, http.StatusOK, w.Code)
	var returnedAuthor response.AuthorResponseTo
	json.Unmarshal(w.Body.Bytes(), &returnedAuthor)
	assert.Equal(t, author.ID, returnedAuthor.ID)
	assert.Equal(t, "testuser", returnedAuthor.Login)
}

func TestIssueController_GetMarkersByIssueID(t *testing.T) {
	router := setupIssueRouter()

	// Создаем автора
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

	// Создаем метки
	markerReq1 := request.MarkerRequestTo{Name: "Marker1"}
	jsonValue, _ = json.Marshal(markerReq1)
	req, _ = http.NewRequest("POST", "/api/v1.0/markers", bytes.NewBuffer(jsonValue))
	req.Header.Set("Content-Type", "application/json")
	w = httptest.NewRecorder()
	router.ServeHTTP(w, req)
	var marker1 response.MarkerResponseTo
	json.Unmarshal(w.Body.Bytes(), &marker1)

	markerReq2 := request.MarkerRequestTo{Name: "Marker2"}
	jsonValue, _ = json.Marshal(markerReq2)
	req, _ = http.NewRequest("POST", "/api/v1.0/markers", bytes.NewBuffer(jsonValue))
	req.Header.Set("Content-Type", "application/json")
	w = httptest.NewRecorder()
	router.ServeHTTP(w, req)
	var marker2 response.MarkerResponseTo
	json.Unmarshal(w.Body.Bytes(), &marker2)

	// Создаем задачу с метками
	issueReq := request.IssueRequestTo{
		Title:     "Test Issue",
		Content:   "Test Content",
		AuthorID:  author.ID,
		MarkerIDs: []string{marker1.ID, marker2.ID},
	}
	jsonValue, _ = json.Marshal(issueReq)
	req, _ = http.NewRequest("POST", "/api/v1.0/issues", bytes.NewBuffer(jsonValue))
	req.Header.Set("Content-Type", "application/json")
	w = httptest.NewRecorder()
	router.ServeHTTP(w, req)
	var issue response.IssueResponseTo
	json.Unmarshal(w.Body.Bytes(), &issue)

	// Получаем метки по ID задачи
	req, _ = http.NewRequest("GET", "/api/v1.0/issues/"+issue.ID+"/markers", nil)
	w = httptest.NewRecorder()
	router.ServeHTTP(w, req)

	assert.Equal(t, http.StatusOK, w.Code)
	var markers []response.MarkerResponseTo
	json.Unmarshal(w.Body.Bytes(), &markers)
	assert.Equal(t, 2, len(markers))
}

