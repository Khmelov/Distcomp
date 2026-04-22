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

func setupAuthorRouter() *gin.Engine {
	gin.SetMode(gin.TestMode)
	authorRepo := inmemory.NewAuthorRepositoryInMemory()
	authorService := service.NewAuthorService(authorRepo)
	authorController := controller.NewAuthorController(authorService)

	r := gin.New()
	v1 := r.Group("/api/v1.0")
	{
		authors := v1.Group("/authors")
		{
			authors.POST("", authorController.Create)
			authors.GET("", authorController.GetAll)
			authors.GET("/:id", authorController.GetByID)
			authors.PUT("/:id", authorController.Update)
			authors.DELETE("/:id", authorController.Delete)
		}
	}
	return r
}

func TestAuthorController_Create(t *testing.T) {
	router := setupAuthorRouter()

	reqBody := request.AuthorRequestTo{
		Login:     "testuser",
		Password:  "password123",
		Firstname: "John",
		Lastname:  "Doe",
	}
	jsonValue, _ := json.Marshal(reqBody)

	req, _ := http.NewRequest("POST", "/api/v1.0/authors", bytes.NewBuffer(jsonValue))
	req.Header.Set("Content-Type", "application/json")
	w := httptest.NewRecorder()
	router.ServeHTTP(w, req)

	assert.Equal(t, http.StatusCreated, w.Code)
	var author response.AuthorResponseTo
	json.Unmarshal(w.Body.Bytes(), &author)
	assert.NotEmpty(t, author.ID)
	assert.Equal(t, "testuser", author.Login)
	assert.Equal(t, "John", author.Firstname)
	assert.Equal(t, "Doe", author.Lastname)
}

func TestAuthorController_GetByID(t *testing.T) {
	router := setupAuthorRouter()

	// Сначала создаем автора
	reqBody := request.AuthorRequestTo{
		Login:     "testuser",
		Password:  "password123",
		Firstname: "John",
		Lastname:  "Doe",
	}
	jsonValue, _ := json.Marshal(reqBody)
	req, _ := http.NewRequest("POST", "/api/v1.0/authors", bytes.NewBuffer(jsonValue))
	req.Header.Set("Content-Type", "application/json")
	w := httptest.NewRecorder()
	router.ServeHTTP(w, req)
	var createdAuthor response.AuthorResponseTo
	json.Unmarshal(w.Body.Bytes(), &createdAuthor)

	// Теперь получаем его по ID
	req, _ = http.NewRequest("GET", "/api/v1.0/authors/"+createdAuthor.ID, nil)
	w = httptest.NewRecorder()
	router.ServeHTTP(w, req)

	assert.Equal(t, http.StatusOK, w.Code)
	var author response.AuthorResponseTo
	json.Unmarshal(w.Body.Bytes(), &author)
	assert.Equal(t, createdAuthor.ID, author.ID)
	assert.Equal(t, "testuser", author.Login)
}

func TestAuthorController_GetAll(t *testing.T) {
	router := setupAuthorRouter()

	// Создаем несколько авторов
	for i := 0; i < 3; i++ {
		reqBody := request.AuthorRequestTo{
			Login:     "user" + string(rune('0'+i)),
			Password:  "password",
			Firstname: "First",
			Lastname:  "Last",
		}
		jsonValue, _ := json.Marshal(reqBody)
		req, _ := http.NewRequest("POST", "/api/v1.0/authors", bytes.NewBuffer(jsonValue))
		req.Header.Set("Content-Type", "application/json")
		w := httptest.NewRecorder()
		router.ServeHTTP(w, req)
	}

	// Получаем всех авторов
	req, _ := http.NewRequest("GET", "/api/v1.0/authors", nil)
	w := httptest.NewRecorder()
	router.ServeHTTP(w, req)

	assert.Equal(t, http.StatusOK, w.Code)
	var authors []response.AuthorResponseTo
	json.Unmarshal(w.Body.Bytes(), &authors)
	assert.GreaterOrEqual(t, len(authors), 3)
}

func TestAuthorController_Update(t *testing.T) {
	router := setupAuthorRouter()

	// Создаем автора
	reqBody := request.AuthorRequestTo{
		Login:     "testuser",
		Password:  "password123",
		Firstname: "John",
		Lastname:  "Doe",
	}
	jsonValue, _ := json.Marshal(reqBody)
	req, _ := http.NewRequest("POST", "/api/v1.0/authors", bytes.NewBuffer(jsonValue))
	req.Header.Set("Content-Type", "application/json")
	w := httptest.NewRecorder()
	router.ServeHTTP(w, req)
	var createdAuthor response.AuthorResponseTo
	json.Unmarshal(w.Body.Bytes(), &createdAuthor)

	// Обновляем автора
	updateBody := request.AuthorRequestTo{
		Login:     "updateduser",
		Password:  "newpassword",
		Firstname: "Jane",
		Lastname:  "Smith",
	}
	jsonValue, _ = json.Marshal(updateBody)
	req, _ = http.NewRequest("PUT", "/api/v1.0/authors/"+createdAuthor.ID, bytes.NewBuffer(jsonValue))
	req.Header.Set("Content-Type", "application/json")
	w = httptest.NewRecorder()
	router.ServeHTTP(w, req)

	assert.Equal(t, http.StatusOK, w.Code)
	var author response.AuthorResponseTo
	json.Unmarshal(w.Body.Bytes(), &author)
	assert.Equal(t, "updateduser", author.Login)
	assert.Equal(t, "Jane", author.Firstname)
}

func TestAuthorController_Delete(t *testing.T) {
	router := setupAuthorRouter()

	// Создаем автора
	reqBody := request.AuthorRequestTo{
		Login:     "testuser",
		Password:  "password123",
		Firstname: "John",
		Lastname:  "Doe",
	}
	jsonValue, _ := json.Marshal(reqBody)
	req, _ := http.NewRequest("POST", "/api/v1.0/authors", bytes.NewBuffer(jsonValue))
	req.Header.Set("Content-Type", "application/json")
	w := httptest.NewRecorder()
	router.ServeHTTP(w, req)
	var createdAuthor response.AuthorResponseTo
	json.Unmarshal(w.Body.Bytes(), &createdAuthor)

	// Удаляем автора
	req, _ = http.NewRequest("DELETE", "/api/v1.0/authors/"+createdAuthor.ID, nil)
	w = httptest.NewRecorder()
	router.ServeHTTP(w, req)

	assert.Equal(t, http.StatusNoContent, w.Code)

	// Проверяем, что автор удален
	req, _ = http.NewRequest("GET", "/api/v1.0/authors/"+createdAuthor.ID, nil)
	w = httptest.NewRecorder()
	router.ServeHTTP(w, req)
	assert.Equal(t, http.StatusNotFound, w.Code)
}

func TestAuthorController_Create_ValidationError(t *testing.T) {
	router := setupAuthorRouter()

	reqBody := request.AuthorRequestTo{
		Login: "", // Пустое поле
	}
	jsonValue, _ := json.Marshal(reqBody)

	req, _ := http.NewRequest("POST", "/api/v1.0/authors", bytes.NewBuffer(jsonValue))
	req.Header.Set("Content-Type", "application/json")
	w := httptest.NewRecorder()
	router.ServeHTTP(w, req)

	assert.Equal(t, http.StatusBadRequest, w.Code)
}

