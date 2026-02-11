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

func setupMarkerRouter() *gin.Engine {
	gin.SetMode(gin.TestMode)
	markerRepo := inmemory.NewMarkerRepositoryInMemory()
	markerService := service.NewMarkerService(markerRepo)
	markerController := controller.NewMarkerController(markerService)

	r := gin.New()
	v1 := r.Group("/api/v1.0")
	{
		markers := v1.Group("/markers")
		{
			markers.POST("", markerController.Create)
			markers.GET("", markerController.GetAll)
			markers.GET("/:id", markerController.GetByID)
			markers.PUT("/:id", markerController.Update)
			markers.DELETE("/:id", markerController.Delete)
		}
	}
	return r
}

func TestMarkerController_Create(t *testing.T) {
	router := setupMarkerRouter()

	reqBody := request.MarkerRequestTo{
		Name: "Test Marker",
	}
	jsonValue, _ := json.Marshal(reqBody)

	req, _ := http.NewRequest("POST", "/api/v1.0/markers", bytes.NewBuffer(jsonValue))
	req.Header.Set("Content-Type", "application/json")
	w := httptest.NewRecorder()
	router.ServeHTTP(w, req)

	assert.Equal(t, http.StatusCreated, w.Code)
	var marker response.MarkerResponseTo
	json.Unmarshal(w.Body.Bytes(), &marker)
	assert.NotEmpty(t, marker.ID)
	assert.Equal(t, "Test Marker", marker.Name)
}

func TestMarkerController_GetByID(t *testing.T) {
	router := setupMarkerRouter()

	// Создаем метку
	reqBody := request.MarkerRequestTo{
		Name: "Test Marker",
	}
	jsonValue, _ := json.Marshal(reqBody)
	req, _ := http.NewRequest("POST", "/api/v1.0/markers", bytes.NewBuffer(jsonValue))
	req.Header.Set("Content-Type", "application/json")
	w := httptest.NewRecorder()
	router.ServeHTTP(w, req)
	var createdMarker response.MarkerResponseTo
	json.Unmarshal(w.Body.Bytes(), &createdMarker)

	// Получаем метку по ID
	req, _ = http.NewRequest("GET", "/api/v1.0/markers/"+createdMarker.ID, nil)
	w = httptest.NewRecorder()
	router.ServeHTTP(w, req)

	assert.Equal(t, http.StatusOK, w.Code)
	var marker response.MarkerResponseTo
	json.Unmarshal(w.Body.Bytes(), &marker)
	assert.Equal(t, createdMarker.ID, marker.ID)
	assert.Equal(t, "Test Marker", marker.Name)
}

func TestMarkerController_GetAll(t *testing.T) {
	router := setupMarkerRouter()

	// Создаем несколько меток
	for i := 0; i < 3; i++ {
		reqBody := request.MarkerRequestTo{
			Name: "Marker " + string(rune('0'+i)),
		}
		jsonValue, _ := json.Marshal(reqBody)
		req, _ := http.NewRequest("POST", "/api/v1.0/markers", bytes.NewBuffer(jsonValue))
		req.Header.Set("Content-Type", "application/json")
		w := httptest.NewRecorder()
		router.ServeHTTP(w, req)
	}

	// Получаем все метки
	req, _ := http.NewRequest("GET", "/api/v1.0/markers", nil)
	w := httptest.NewRecorder()
	router.ServeHTTP(w, req)

	assert.Equal(t, http.StatusOK, w.Code)
	var markers []response.MarkerResponseTo
	json.Unmarshal(w.Body.Bytes(), &markers)
	assert.GreaterOrEqual(t, len(markers), 3)
}

func TestMarkerController_Update(t *testing.T) {
	router := setupMarkerRouter()

	// Создаем метку
	reqBody := request.MarkerRequestTo{
		Name: "Test Marker",
	}
	jsonValue, _ := json.Marshal(reqBody)
	req, _ := http.NewRequest("POST", "/api/v1.0/markers", bytes.NewBuffer(jsonValue))
	req.Header.Set("Content-Type", "application/json")
	w := httptest.NewRecorder()
	router.ServeHTTP(w, req)
	var createdMarker response.MarkerResponseTo
	json.Unmarshal(w.Body.Bytes(), &createdMarker)

	// Обновляем метку
	updateBody := request.MarkerRequestTo{
		Name: "Updated Marker",
	}
	jsonValue, _ = json.Marshal(updateBody)
	req, _ = http.NewRequest("PUT", "/api/v1.0/markers/"+createdMarker.ID, bytes.NewBuffer(jsonValue))
	req.Header.Set("Content-Type", "application/json")
	w = httptest.NewRecorder()
	router.ServeHTTP(w, req)

	assert.Equal(t, http.StatusOK, w.Code)
	var marker response.MarkerResponseTo
	json.Unmarshal(w.Body.Bytes(), &marker)
	assert.Equal(t, "Updated Marker", marker.Name)
}

func TestMarkerController_Delete(t *testing.T) {
	router := setupMarkerRouter()

	// Создаем метку
	reqBody := request.MarkerRequestTo{
		Name: "Test Marker",
	}
	jsonValue, _ := json.Marshal(reqBody)
	req, _ := http.NewRequest("POST", "/api/v1.0/markers", bytes.NewBuffer(jsonValue))
	req.Header.Set("Content-Type", "application/json")
	w := httptest.NewRecorder()
	router.ServeHTTP(w, req)
	var createdMarker response.MarkerResponseTo
	json.Unmarshal(w.Body.Bytes(), &createdMarker)

	// Удаляем метку
	req, _ = http.NewRequest("DELETE", "/api/v1.0/markers/"+createdMarker.ID, nil)
	w = httptest.NewRecorder()
	router.ServeHTTP(w, req)

	assert.Equal(t, http.StatusNoContent, w.Code)

	// Проверяем, что метка удалена
	req, _ = http.NewRequest("GET", "/api/v1.0/markers/"+createdMarker.ID, nil)
	w = httptest.NewRecorder()
	router.ServeHTTP(w, req)
	assert.Equal(t, http.StatusNotFound, w.Code)
}

