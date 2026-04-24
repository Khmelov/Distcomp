package controller

import (
	"io"
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

// --- V2.0 AUTH ---

func (h *Handler) RegisterV2(c *gin.Context) {
	var dto domain.EditorDTO
	if err := c.ShouldBindJSON(&dto); err != nil {
		response.SendError(c, 400, "40001", "Validation failed")
		return
	}
	role := dto.Role
	if role == "" {
		role = "CUSTOMER"
	}
	e := domain.Editor{
		Login:     dto.Login,
		Password:  service.HashPassword(dto.Password),
		Firstname: dto.Firstname,
		Lastname:  dto.Lastname,
		Role:      role,
	}
	if err := h.Repo.CreateEditor(&e); err != nil {
		response.SendError(c, 403, "40301", "Exists")
		return
	}
	c.JSON(201, e)
}

func (h *Handler) LoginV2(c *gin.Context) {
	var req domain.LoginRequest
	if err := c.ShouldBindJSON(&req); err != nil {
		c.Status(400)
		return
	}
	var user domain.Editor
	if err := h.Repo.DB.Where("login = ?", req.Login).First(&user).Error; err != nil || !service.CheckPassword(req.Password, user.Password) {
		response.SendError(c, 401, "40101", "Auth failed")
		return
	}
	token, _ := service.GenerateJWT(user.Login, user.Role)
	c.JSON(200, domain.LoginResponse{AccessToken: token})
}

// --- PROXY ---

func (h *Handler) ProxyNote(c *gin.Context) {
	pathID := c.Param("id")
	fullPath := ""
	if pathID != "" {
		fullPath = "/" + pathID
	}

	var bodyBytes []byte
	if c.Request.Body != nil {
		bodyBytes, _ = io.ReadAll(c.Request.Body)
	}

	data, status, err := service.ProxyToDiscussion(c.Request.Method, fullPath, bodyBytes)
	if err != nil {
		c.Status(500)
		return
	}
	c.Data(status, "application/json", data)
}

// --- CRUD ---

func (h *Handler) CreateEditor(c *gin.Context) {
	var e domain.Editor
	c.BindJSON(&e)
	h.Repo.CreateEditor(&e)
	c.JSON(201, e)
}
func (h *Handler) GetEditor(c *gin.Context) {
	id, _ := strconv.Atoi(c.Param("id"))
	res, err := h.Repo.GetEditor(uint(id))
	if err != nil {
		c.Status(404)
		return
	}
	c.JSON(200, res)
}
func (h *Handler) GetAllEditors(c *gin.Context) {
	var res []domain.Editor
	h.Repo.DB.Find(&res)
	c.JSON(200, res)
}
func (h *Handler) UpdateEditor(c *gin.Context) {
	var e domain.Editor
	c.BindJSON(&e)
	h.Repo.UpdateEditor(&e)
	c.JSON(200, e)
}
func (h *Handler) DeleteEditor(c *gin.Context) {
	id, _ := strconv.Atoi(c.Param("id"))
	h.Repo.DeleteEditor(uint(id))
	c.Status(204)
}
func (h *Handler) CreateTopic(c *gin.Context) {
	var t domain.Topic
	c.BindJSON(&t)
	h.Repo.CreateTopic(&t)
	c.JSON(201, t)
}
func (h *Handler) GetAllTopics(c *gin.Context) {
	var res []domain.Topic
	h.Repo.DB.Find(&res)
	c.JSON(200, res)
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
func (h *Handler) GetAllMarkers(c *gin.Context) {
	var res []domain.Marker
	h.Repo.DB.Find(&res)
	c.JSON(200, res)
}
func (h *Handler) CreateMarker(c *gin.Context) {
	var m domain.Marker
	c.BindJSON(&m)
	h.Repo.CreateMarker(&m)
	c.JSON(201, m)
}
func (h *Handler) GetMarker(c *gin.Context) {
	id, _ := strconv.Atoi(c.Param("id"))
	var res domain.Marker
	if err := h.Repo.DB.First(&res, id).Error; err != nil {
		c.Status(404)
		return
	}
	c.JSON(200, res)
}
func (h *Handler) UpdateMarker(c *gin.Context) {
	var m domain.Marker
	c.BindJSON(&m)
	h.Repo.DB.Save(&m)
	c.JSON(200, m)
}
func (h *Handler) DeleteMarker(c *gin.Context) {
	id, _ := strconv.Atoi(c.Param("id"))
	h.Repo.DB.Delete(&domain.Marker{}, id)
	c.Status(204)
}
