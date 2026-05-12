package controller

import (
	"encoding/json"
	"io"
	"net/http"
	"publisher/internal/domain"
	"publisher/internal/repository"
	"publisher/internal/service"
	"publisher/pkg/response"
	"strconv"

	"github.com/gin-gonic/gin"
)

type Handler struct {
	Repo *repository.Repository
}

func (h *Handler) ProxyNote(c *gin.Context) {
	pathID := c.Param("id")
	cacheKey := "note:" + pathID

	// 1. Проверка кеша: если GET, то пробуем отдать из Redis
	if c.Request.Method == http.MethodGet && pathID != "" {
		if val, err := service.GetCache(cacheKey); err == nil && len(val) > 0 {
			c.Data(200, "application/json; charset=utf-8", val)
			return
		}
	}

	var bodyBytes []byte
	if c.Request.Body != nil {
		bodyBytes, _ = io.ReadAll(c.Request.Body)
	}

	// Kafka Integration
	if c.Request.Method == http.MethodPost || c.Request.Method == http.MethodPut {
		var note domain.Note
		if err := json.Unmarshal(bodyBytes, &note); err == nil {
			if c.Request.Method == http.MethodPost {
				note.State = "PENDING"
			}
			service.SendNoteToKafka(note)
			bodyBytes, _ = json.Marshal(note)
		}
	}

	fullPath := ""
	if pathID != "" {
		fullPath = "/" + pathID
	}

	// Прокси на Discussion (24130)
	data, status, err := service.ProxyToDiscussion(c.Request.Method, fullPath, bodyBytes)
	if err != nil {
		response.SendError(c, 500, "50001", "Discussion service error")
		return
	}

	// 2. Агрессивное наполнение кеша
	// Если получили 200 или 201 — записываем в Redis
	if pathID != "" && (status == http.StatusOK || status == http.StatusCreated) {
		service.SetCache(cacheKey, data)
	}

	// Если это DELETE через Publisher — чистим кеш
	if c.Request.Method == http.MethodDelete && pathID != "" {
		service.ClearCache(cacheKey)
	}

	if len(data) == 0 || string(data) == "null" {
		if c.Request.Method == http.MethodGet && pathID == "" {
			c.JSON(status, []interface{}{})
		} else {
			c.JSON(status, gin.H{})
		}
		return
	}
	c.Data(status, "application/json; charset=utf-8", data)
}

// --- EDITOR (валидация + кеш) ---

func (h *Handler) CreateEditor(c *gin.Context) {
	var dto domain.EditorDTO
	if err := c.ShouldBindJSON(&dto); err != nil {
		response.SendError(c, 400, "40001", "Validation failed")
		return
	}
	e := domain.Editor{Login: dto.Login, Password: dto.Password, Firstname: dto.Firstname, Lastname: dto.Lastname}
	if err := h.Repo.CreateEditor(&e); err != nil {
		response.SendError(c, 403, "40301", "Duplicate login")
		return
	}
	c.JSON(201, e)
}

func (h *Handler) GetEditor(c *gin.Context) {
	id := c.Param("id")
	cacheKey := "editor:" + id
	if val, err := service.GetCache(cacheKey); err == nil {
		c.Data(200, "application/json", val)
		return
	}
	uID, _ := strconv.Atoi(id)
	res, err := h.Repo.GetEditor(uint(uID))
	if err != nil {
		response.SendError(c, 404, "40401", "Not found")
		return
	}
	data, _ := json.Marshal(res)
	service.SetCache(cacheKey, data)
	c.JSON(200, res)
}

func (h *Handler) GetAllEditors(c *gin.Context) {
	res, _ := h.Repo.GetAllEditors()
	if res == nil {
		res = []domain.Editor{}
	}
	c.JSON(200, res)
}

func (h *Handler) UpdateEditor(c *gin.Context) {
	var e domain.Editor
	c.BindJSON(&e)
	h.Repo.UpdateEditor(&e)
	service.ClearCache("editor:" + strconv.Itoa(int(e.ID)))
	c.JSON(200, e)
}

func (h *Handler) DeleteEditor(c *gin.Context) {
	id := c.Param("id")
	uID, _ := strconv.Atoi(id)
	h.Repo.DeleteEditor(uint(uID))
	service.ClearCache("editor:" + id)
	c.Status(204)
}

// --- Topic / Marker (с кешем) ---

func (h *Handler) CreateTopic(c *gin.Context) {
	var t domain.Topic
	c.BindJSON(&t)
	h.Repo.CreateTopic(&t)
	c.JSON(201, t)
}
func (h *Handler) GetTopic(c *gin.Context) {
	id := c.Param("id")
	cacheKey := "topic:" + id
	if val, err := service.GetCache(cacheKey); err == nil {
		c.Data(200, "application/json", val)
		return
	}
	uID, _ := strconv.Atoi(id)
	res, err := h.Repo.GetTopic(uint(uID))
	if err != nil {
		c.Status(404)
		return
	}
	data, _ := json.Marshal(res)
	service.SetCache(cacheKey, data)
	c.JSON(200, res)
}
func (h *Handler) GetAllTopics(c *gin.Context) {
	res, _ := h.Repo.GetAllTopics()
	if res == nil {
		res = []domain.Topic{}
	}
	c.JSON(200, res)
}
func (h *Handler) UpdateTopic(c *gin.Context) {
	var t domain.Topic
	c.BindJSON(&t)
	h.Repo.UpdateTopic(&t)
	service.ClearCache("topic:" + strconv.Itoa(int(t.ID)))
	c.JSON(200, t)
}
func (h *Handler) DeleteTopic(c *gin.Context) {
	id := c.Param("id")
	uID, _ := strconv.Atoi(id)
	h.Repo.DeleteTopic(uint(uID))
	service.ClearCache("topic:" + id)
	c.Status(204)
}
func (h *Handler) CreateMarker(c *gin.Context) {
	var m domain.Marker
	c.BindJSON(&m)
	h.Repo.CreateMarker(&m)
	c.JSON(201, m)
}
func (h *Handler) GetMarker(c *gin.Context) {
	id := c.Param("id")
	cacheKey := "marker:" + id
	if val, err := service.GetCache(cacheKey); err == nil {
		c.Data(200, "application/json", val)
		return
	}
	uID, _ := strconv.Atoi(id)
	res, err := h.Repo.GetMarker(uint(uID))
	if err != nil {
		c.Status(404)
		return
	}
	data, _ := json.Marshal(res)
	service.SetCache(cacheKey, data)
	c.JSON(200, res)
}
func (h *Handler) GetAllMarkers(c *gin.Context) {
	res, _ := h.Repo.GetAllMarkers()
	if res == nil {
		res = []domain.Marker{}
	}
	c.JSON(200, res)
}
func (h *Handler) UpdateMarker(c *gin.Context) {
	var m domain.Marker
	c.BindJSON(&m)
	h.Repo.UpdateMarker(&m)
	service.ClearCache("marker:" + strconv.Itoa(int(m.ID)))
	c.JSON(200, m)
}
func (h *Handler) DeleteMarker(c *gin.Context) {
	id := c.Param("id")
	uID, _ := strconv.Atoi(id)
	if err := h.Repo.DeleteMarker(uint(uID)); err != nil {
		response.SendError(c, 404, "40401", "Not found")
		return
	}
	service.ClearCache("marker:" + id)
	c.Status(204)
}
