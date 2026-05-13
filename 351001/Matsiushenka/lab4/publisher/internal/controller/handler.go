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
	fullPath := ""
	if pathID != "" {
		fullPath = "/" + pathID
	}

	var bodyBytes []byte
	if c.Request.Method == http.MethodPost || c.Request.Method == http.MethodPut {
		var note domain.Note
		if err := c.ShouldBindJSON(&note); err == nil {
			if c.Request.Method == http.MethodPost {
				note.State = "PENDING"
			}
			service.SendNoteToKafka(note)
			bodyBytes, _ = json.Marshal(note)
		}
	} else {
		if c.Request.Body != nil {
			bodyBytes, _ = io.ReadAll(c.Request.Body)
		}
	}

	data, status, err := service.ProxyToDiscussion(c.Request.Method, fullPath, bodyBytes)
	if err != nil {
		response.SendError(c, 500, "50001", "Discussion service unavailable")
		return
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

func (h *Handler) CreateEditor(c *gin.Context) {
	var dto domain.EditorDTO
	if err := c.ShouldBindJSON(&dto); err != nil {
		response.SendError(c, 400, "40001", "Validation failed: "+err.Error())
		return
	}
	e := domain.Editor{
		Login:     dto.Login,
		Password:  dto.Password,
		Firstname: dto.Firstname,
		Lastname:  dto.Lastname,
	}
	if err := h.Repo.CreateEditor(&e); err != nil {
		response.SendError(c, 403, "40301", "Duplicate login or database error")
		return
	}
	c.JSON(201, e)
}

func (h *Handler) GetEditor(c *gin.Context) {
	id, _ := strconv.Atoi(c.Param("id"))
	res, err := h.Repo.GetEditor(uint(id))
	if err != nil {
		response.SendError(c, 404, "40401", "Not found")
		return
	}
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
	if err := c.ShouldBindJSON(&e); err != nil {
		c.Status(400)
		return
	}
	h.Repo.UpdateEditor(&e)
	c.JSON(200, e)
}

func (h *Handler) DeleteEditor(c *gin.Context) {
	id, _ := strconv.Atoi(c.Param("id"))
	h.Repo.DeleteEditor(uint(id))
	c.Status(204)
}

// Topic / Marker Handlers (без изменений, но убедись что они есть)
func (h *Handler) CreateTopic(c *gin.Context) {
	var t domain.Topic
	if err := c.ShouldBindJSON(&t); err != nil {
		c.Status(400)
		return
	}
	h.Repo.CreateTopic(&t)
	c.JSON(201, t)
}
func (h *Handler) GetTopic(c *gin.Context) {
	id, _ := strconv.Atoi(c.Param("id"))
	res, err := h.Repo.GetTopic(uint(id))
	if err != nil {
		c.Status(404)
		return
	}
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
	c.JSON(200, t)
}
func (h *Handler) DeleteTopic(c *gin.Context) {
	id, _ := strconv.Atoi(c.Param("id"))
	h.Repo.DeleteTopic(uint(id))
	c.Status(204)
}
func (h *Handler) CreateMarker(c *gin.Context) {
	var m domain.Marker
	c.BindJSON(&m)
	h.Repo.CreateMarker(&m)
	c.JSON(201, m)
}
func (h *Handler) GetMarker(c *gin.Context) {
	id, _ := strconv.Atoi(c.Param("id"))
	res, err := h.Repo.GetMarker(uint(id))
	if err != nil {
		c.Status(404)
		return
	}
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
	c.JSON(200, m)
}
func (h *Handler) DeleteMarker(c *gin.Context) {
	id, _ := strconv.Atoi(c.Param("id"))
	if err := h.Repo.DeleteMarker(uint(id)); err != nil {
		response.SendError(c, 404, "40401", "Not found")
		return
	}
	c.Status(204)
}
