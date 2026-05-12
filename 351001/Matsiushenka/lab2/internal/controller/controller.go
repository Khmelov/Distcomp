package controller

import (
	"net/http"
	"strconv"

	"distcomp/internal/domain"
	"distcomp/internal/service"
	"distcomp/pkg/response"

	"github.com/gin-gonic/gin"
)

type Handler struct {
	service *service.Service
}

func NewHandler(s *service.Service) *Handler {
	return &Handler{service: s}
}

func parseID(c *gin.Context) (uint, error) {
	idStr := c.Param("id")
	id, err := strconv.ParseUint(idStr, 10, 32)
	return uint(id), err
}

func handleError(c *gin.Context, err error) {
	if err.Error() == "not_found_404" {
		response.SendError(c, http.StatusNotFound, "40401", "Entity not found")
		return
	}
	if err.Error() == "duplicate_403" {
		response.SendError(c, http.StatusForbidden, "40301", "Entity already exists")
		return
	}
	if err.Error() == "association_400" {
		response.SendError(c, http.StatusBadRequest, "40002", "Invalid association ID")
		return
	}
	response.SendError(c, http.StatusInternalServerError, "50001", err.Error())
}

func (h *Handler) CreateEditor(c *gin.Context) {
	var dto domain.EditorDTO
	if err := c.ShouldBindJSON(&dto); err != nil {
		response.SendError(c, http.StatusBadRequest, "40001", "Validation failed")
		return
	}
	editor, err := h.service.CreateEditor(&dto)
	if err != nil {
		handleError(c, err)
		return
	}
	c.JSON(http.StatusCreated, editor)
}

func (h *Handler) GetEditor(c *gin.Context) {
	id, _ := parseID(c)
	editor, err := h.service.Repo().GetEditor(id)
	if err != nil {
		response.SendError(c, http.StatusNotFound, "40401", "Editor not found")
		return
	}
	c.JSON(http.StatusOK, editor)
}

func (h *Handler) GetAllEditors(c *gin.Context) {
	res, _ := h.service.Repo().GetAllEditors()
	c.JSON(http.StatusOK, res)
}

func (h *Handler) UpdateEditor(c *gin.Context) {
	var dto domain.EditorDTO
	if err := c.ShouldBindJSON(&dto); err != nil || dto.ID == nil {
		response.SendError(c, http.StatusBadRequest, "40001", "Validation failed or missing ID")
		return
	}
	editor, err := h.service.UpdateEditor(&dto)
	if err != nil {
		handleError(c, err)
		return
	}
	c.JSON(http.StatusOK, editor)
}

func (h *Handler) DeleteEditor(c *gin.Context) {
	id, _ := parseID(c)
	if err := h.service.Repo().DeleteEditor(id); err != nil {
		handleError(c, err)
		return
	}
	c.Status(http.StatusNoContent)
}

func (h *Handler) CreateTopic(c *gin.Context) {
	var dto domain.TopicDTO
	if err := c.ShouldBindJSON(&dto); err != nil {
		response.SendError(c, http.StatusBadRequest, "40001", "Validation failed")
		return
	}
	topic, err := h.service.CreateTopic(&dto)
	if err != nil {
		handleError(c, err)
		return
	}
	c.JSON(http.StatusCreated, topic)
}

func (h *Handler) GetTopic(c *gin.Context) {
	id, _ := parseID(c)
	topic, err := h.service.Repo().GetTopic(id)
	if err != nil {
		response.SendError(c, http.StatusNotFound, "40401", "Not found")
		return
	}
	c.JSON(http.StatusOK, topic)
}

func (h *Handler) GetAllTopics(c *gin.Context) {
	res, _ := h.service.Repo().GetAllTopics()
	c.JSON(http.StatusOK, res)
}

func (h *Handler) UpdateTopic(c *gin.Context) {
	var dto domain.TopicDTO
	if err := c.ShouldBindJSON(&dto); err != nil || dto.ID == nil {
		response.SendError(c, http.StatusBadRequest, "40001", "Validation failed")
		return
	}
	topic, err := h.service.Repo().GetTopic(*dto.ID)
	if err != nil {
		response.SendError(c, http.StatusNotFound, "40401", "Not found")
		return
	}
	topic.Title = dto.Title
	topic.Content = dto.Content
	h.service.Repo().UpdateTopic(topic)
	c.JSON(http.StatusOK, topic)
}

func (h *Handler) DeleteTopic(c *gin.Context) {
	id, _ := parseID(c)
	if err := h.service.Repo().DeleteTopic(id); err != nil {
		handleError(c, err)
		return
	}
	c.Status(http.StatusNoContent)
}

func (h *Handler) CreateMarker(c *gin.Context) {
	var dto domain.MarkerDTO
	if err := c.ShouldBindJSON(&dto); err != nil {
		response.SendError(c, http.StatusBadRequest, "40001", "Validation failed")
		return
	}
	marker, err := h.service.CreateMarker(&dto)
	if err != nil {
		handleError(c, err)
		return
	}
	c.JSON(http.StatusCreated, marker)
}

func (h *Handler) GetMarker(c *gin.Context) {
	id, _ := parseID(c)
	m, err := h.service.Repo().GetMarker(id)
	if err != nil {
		response.SendError(c, http.StatusNotFound, "40401", "Not found")
		return
	}
	c.JSON(http.StatusOK, m)
}

func (h *Handler) GetAllMarkers(c *gin.Context) {
	res, _ := h.service.Repo().GetAllMarkers()
	c.JSON(http.StatusOK, res)
}

func (h *Handler) UpdateMarker(c *gin.Context) {
	var dto domain.MarkerDTO
	if err := c.ShouldBindJSON(&dto); err != nil || dto.ID == nil {
		response.SendError(c, http.StatusBadRequest, "40001", "Validation failed")
		return
	}
	m, err := h.service.Repo().GetMarker(*dto.ID)
	if err != nil {
		response.SendError(c, http.StatusNotFound, "40401", "Not found")
		return
	}
	m.Name = dto.Name
	h.service.Repo().UpdateMarker(m)
	c.JSON(http.StatusOK, m)
}

func (h *Handler) DeleteMarker(c *gin.Context) {
	id, _ := parseID(c)
	if err := h.service.Repo().DeleteMarker(id); err != nil {
		handleError(c, err)
		return
	}
	c.Status(http.StatusNoContent)
}

func (h *Handler) CreateNote(c *gin.Context) {
	var dto domain.NoteDTO
	if err := c.ShouldBindJSON(&dto); err != nil {
		response.SendError(c, http.StatusBadRequest, "40001", "Validation failed")
		return
	}
	note, err := h.service.CreateNote(&dto)
	if err != nil {
		handleError(c, err)
		return
	}
	c.JSON(http.StatusCreated, note)
}

func (h *Handler) GetNote(c *gin.Context) {
	id, _ := parseID(c)
	n, err := h.service.Repo().GetNote(id)
	if err != nil {
		response.SendError(c, http.StatusNotFound, "40401", "Not found")
		return
	}
	c.JSON(http.StatusOK, n)
}

func (h *Handler) GetAllNotes(c *gin.Context) {
	res, _ := h.service.Repo().GetAllNotes()
	c.JSON(http.StatusOK, res)
}

func (h *Handler) UpdateNote(c *gin.Context) {
	var dto domain.NoteDTO
	if err := c.ShouldBindJSON(&dto); err != nil || dto.ID == nil {
		response.SendError(c, http.StatusBadRequest, "40001", "Validation failed")
		return
	}
	n, err := h.service.Repo().GetNote(*dto.ID)
	if err != nil {
		response.SendError(c, http.StatusNotFound, "40401", "Not found")
		return
	}
	n.Content = dto.Content
	h.service.Repo().UpdateNote(n)
	c.JSON(http.StatusOK, n)
}

func (h *Handler) DeleteNote(c *gin.Context) {
	id, _ := parseID(c)
	if err := h.service.Repo().DeleteNote(id); err != nil {
		handleError(c, err)
		return
	}
	c.Status(http.StatusNoContent)
}
