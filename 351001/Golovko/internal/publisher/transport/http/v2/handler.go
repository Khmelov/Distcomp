package v2

import (
	"errors"
	"net/http"
	"strconv"
	"strings"

	"distcomp/internal/apperrors"
	"distcomp/internal/dto"
	"distcomp/internal/publisher/repository/postgres"
	"distcomp/internal/publisher/service"
	"distcomp/internal/publisher/transport/http/middleware"
	"distcomp/internal/repository"

	"github.com/gin-gonic/gin"
	"github.com/lib/pq"
)

type Handler struct {
	services *service.Manager
}

func NewHandler(services *service.Manager) *Handler {
	return &Handler{services: services}
}

func (h *Handler) InitRoutes(api *gin.RouterGroup) {
	v2 := api.Group("/v2.0")
	{
		v2.POST("/login", h.login)
		v2.POST("/editors", h.createEditor)

		protected := v2.Group("")
		protected.Use(middleware.Auth())
		{
			editors := protected.Group("/editors")
			{
				editors.GET("", h.getAllEditors)
				editors.GET("/:id", h.getEditorByID)
				editors.PUT("", h.updateEditor)
				editors.PUT("/:id", h.updateEditor)
				editors.DELETE("/:id", h.deleteEditor)
			}

			articles := protected.Group("/articles")
			{
				articles.POST("", h.createArticle)
				articles.GET("", h.getAllArticles)
				articles.GET("/:id", h.getArticleByID)
				articles.PUT("", h.updateArticle)
				articles.PUT("/:id", h.updateArticle)
				articles.DELETE("/:id", h.deleteArticle)
			}

			tags := protected.Group("/tags")
			{
				tags.POST("", h.createTag)
				tags.GET("", h.getAllTags)
				tags.GET("/:id", h.getTagByID)
				tags.PUT("", h.updateTag)
				tags.PUT("/:id", h.updateTag)
				tags.DELETE("/:id", h.deleteTag)
			}

			comments := protected.Group("/comments")
			{
				comments.POST("", h.createComment)
				comments.GET("", h.getAllComments)
				comments.GET("/:id", h.getCommentByID)
				comments.PUT("", h.updateComment)
				comments.PUT("/:id", h.updateComment)
				comments.DELETE("/:id", h.deleteComment)
			}
		}
	}
}

func (h *Handler) hasAccess(c *gin.Context, ownerID int64) bool {
	if c.GetString(middleware.CtxEditorRole) == "ADMIN" {
		return true
	}
	return c.GetInt64(middleware.CtxEditorID) == ownerID
}

func (h *Handler) isAdmin(c *gin.Context) bool {
	return c.GetString(middleware.CtxEditorRole) == "ADMIN"
}

func parseListParams(c *gin.Context) repository.ListParams {
	var params repository.ListParams
	if limit, err := strconv.Atoi(c.Query("limit")); err == nil {
		params.Limit = limit
	}
	if offset, err := strconv.Atoi(c.Query("offset")); err == nil {
		params.Offset = offset
	}
	params.SortBy = c.Query("sort")
	params.Order = c.Query("order")
	return params
}

func parseID(c *gin.Context) (int64, error) {
	return strconv.ParseInt(c.Param("id"), 10, 64)
}

func handleError(c *gin.Context, err error) {
	if errors.Is(err, postgres.ErrNotFound) || strings.Contains(err.Error(), "entity not found") {
		c.JSON(http.StatusNotFound, apperrors.New("entity not found", apperrors.CodeNotFound))
		return
	}
	var pqErr *pq.Error
	if errors.As(err, &pqErr) {
		if pqErr.Code == "23505" {
			c.JSON(http.StatusForbidden, apperrors.New(err.Error(), 40301))
			return
		}
	}
	c.JSON(http.StatusBadRequest, apperrors.New(err.Error(), apperrors.CodeBadRequest))
}

func (h *Handler) login(c *gin.Context) {
	var req dto.LoginRequestTo
	if err := c.ShouldBindJSON(&req); err != nil {
		c.JSON(http.StatusBadRequest, apperrors.New(err.Error(), 40000))
		return
	}
	res, err := h.services.Auth.Login(c.Request.Context(), req)
	if err != nil {
		c.JSON(http.StatusUnauthorized, apperrors.New(err.Error(), 40100))
		return
	}
	c.JSON(http.StatusOK, res)
}

func (h *Handler) createEditor(c *gin.Context) {
	var req dto.EditorRequestTo
	if err := c.ShouldBindJSON(&req); err != nil {
		c.JSON(http.StatusBadRequest, apperrors.New("validation error: "+err.Error(), apperrors.CodeValidationFailed))
		return
	}
	req.Role = "CUSTOMER"
	res, err := h.services.Editor.Create(c.Request.Context(), req)
	if err != nil {
		handleError(c, err)
		return
	}
	c.JSON(http.StatusCreated, res)
}

func (h *Handler) getEditorByID(c *gin.Context) {
	id, err := parseID(c)
	if err != nil {
		c.JSON(http.StatusBadRequest, apperrors.New("invalid id format", apperrors.CodeBadRequest))
		return
	}
	res, err := h.services.Editor.GetByID(c.Request.Context(), id)
	if err != nil {
		handleError(c, err)
		return
	}
	c.JSON(http.StatusOK, res)
}

func (h *Handler) getAllEditors(c *gin.Context) {
	params := parseListParams(c)
	res, err := h.services.Editor.GetAll(c.Request.Context(), params)
	if err != nil {
		handleError(c, err)
		return
	}
	c.JSON(http.StatusOK, res)
}

func (h *Handler) updateEditor(c *gin.Context) {
	var req dto.EditorRequestTo
	if err := c.ShouldBindJSON(&req); err != nil {
		c.JSON(http.StatusBadRequest, apperrors.New("validation error: "+err.Error(), apperrors.CodeValidationFailed))
		return
	}

	var id int64
	if idParam := c.Param("id"); idParam != "" {
		id, _ = strconv.ParseInt(idParam, 10, 64)
	} else {
		id = req.ID
	}

	if !h.hasAccess(c, id) {
		c.JSON(http.StatusForbidden, apperrors.New("access denied", 40300))
		return
	}

	if !h.isAdmin(c) {
		req.Role = "CUSTOMER"
	}

	res, err := h.services.Editor.Update(c.Request.Context(), id, req)
	if err != nil {
		handleError(c, err)
		return
	}
	c.JSON(http.StatusOK, res)
}

func (h *Handler) deleteEditor(c *gin.Context) {
	id, err := parseID(c)
	if err != nil {
		c.JSON(http.StatusBadRequest, apperrors.New("invalid id format", apperrors.CodeBadRequest))
		return
	}

	if !h.hasAccess(c, id) {
		c.JSON(http.StatusForbidden, apperrors.New("access denied", 40300))
		return
	}

	if err := h.services.Editor.Delete(c.Request.Context(), id); err != nil {
		handleError(c, err)
		return
	}
	c.Status(http.StatusNoContent)
}

func (h *Handler) createArticle(c *gin.Context) {
	var req dto.ArticleRequestTo
	if err := c.ShouldBindJSON(&req); err != nil {
		c.JSON(http.StatusBadRequest, apperrors.New("validation error: "+err.Error(), apperrors.CodeValidationFailed))
		return
	}
	req.EditorID = c.GetInt64(middleware.CtxEditorID)
	res, err := h.services.Article.Create(c.Request.Context(), req)
	if err != nil {
		handleError(c, err)
		return
	}
	c.JSON(http.StatusCreated, res)
}

func (h *Handler) getArticleByID(c *gin.Context) {
	id, err := parseID(c)
	if err != nil {
		c.JSON(http.StatusBadRequest, apperrors.New("invalid id format", apperrors.CodeBadRequest))
		return
	}
	res, err := h.services.Article.GetByID(c.Request.Context(), id)
	if err != nil {
		handleError(c, err)
		return
	}
	c.JSON(http.StatusOK, res)
}

func (h *Handler) getAllArticles(c *gin.Context) {
	params := parseListParams(c)
	res, err := h.services.Article.GetAll(c.Request.Context(), params)
	if err != nil {
		handleError(c, err)
		return
	}
	c.JSON(http.StatusOK, res)
}

func (h *Handler) updateArticle(c *gin.Context) {
	var req dto.ArticleRequestTo
	if err := c.ShouldBindJSON(&req); err != nil {
		c.JSON(http.StatusBadRequest, apperrors.New("validation error: "+err.Error(), apperrors.CodeValidationFailed))
		return
	}

	var id int64
	if idParam := c.Param("id"); idParam != "" {
		id, _ = strconv.ParseInt(idParam, 10, 64)
	} else {
		id = req.ID
	}

	art, err := h.services.Article.GetByID(c.Request.Context(), id)
	if err != nil {
		handleError(c, err)
		return
	}

	if !h.hasAccess(c, art.EditorID) {
		c.JSON(http.StatusForbidden, apperrors.New("access denied", 40300))
		return
	}

	req.EditorID = art.EditorID
	res, err := h.services.Article.Update(c.Request.Context(), id, req)
	if err != nil {
		handleError(c, err)
		return
	}
	c.JSON(http.StatusOK, res)
}

func (h *Handler) deleteArticle(c *gin.Context) {
	id, err := parseID(c)
	if err != nil {
		c.JSON(http.StatusBadRequest, apperrors.New("invalid id format", apperrors.CodeBadRequest))
		return
	}

	art, err := h.services.Article.GetByID(c.Request.Context(), id)
	if err != nil {
		handleError(c, err)
		return
	}

	if !h.hasAccess(c, art.EditorID) {
		c.JSON(http.StatusForbidden, apperrors.New("access denied", 40300))
		return
	}

	if err := h.services.Article.Delete(c.Request.Context(), id); err != nil {
		handleError(c, err)
		return
	}
	c.Status(http.StatusNoContent)
}

func (h *Handler) createTag(c *gin.Context) {
	if !h.isAdmin(c) {
		c.JSON(http.StatusForbidden, apperrors.New("access denied", 40300))
		return
	}
	var req dto.TagRequestTo
	if err := c.ShouldBindJSON(&req); err != nil {
		c.JSON(http.StatusBadRequest, apperrors.New("validation error: "+err.Error(), apperrors.CodeValidationFailed))
		return
	}
	res, err := h.services.Tag.Create(c.Request.Context(), req)
	if err != nil {
		handleError(c, err)
		return
	}
	c.JSON(http.StatusCreated, res)
}

func (h *Handler) getTagByID(c *gin.Context) {
	id, err := parseID(c)
	if err != nil {
		c.JSON(http.StatusBadRequest, apperrors.New("invalid id format", apperrors.CodeBadRequest))
		return
	}
	res, err := h.services.Tag.GetByID(c.Request.Context(), id)
	if err != nil {
		handleError(c, err)
		return
	}
	c.JSON(http.StatusOK, res)
}

func (h *Handler) getAllTags(c *gin.Context) {
	params := parseListParams(c)
	res, err := h.services.Tag.GetAll(c.Request.Context(), params)
	if err != nil {
		handleError(c, err)
		return
	}
	c.JSON(http.StatusOK, res)
}

func (h *Handler) updateTag(c *gin.Context) {
	if !h.isAdmin(c) {
		c.JSON(http.StatusForbidden, apperrors.New("access denied", 40300))
		return
	}
	var req dto.TagRequestTo
	if err := c.ShouldBindJSON(&req); err != nil {
		c.JSON(http.StatusBadRequest, apperrors.New("validation error: "+err.Error(), apperrors.CodeValidationFailed))
		return
	}

	var id int64
	if idParam := c.Param("id"); idParam != "" {
		id, _ = strconv.ParseInt(idParam, 10, 64)
	} else {
		id = req.ID
	}

	res, err := h.services.Tag.Update(c.Request.Context(), id, req)
	if err != nil {
		handleError(c, err)
		return
	}
	c.JSON(http.StatusOK, res)
}

func (h *Handler) deleteTag(c *gin.Context) {
	if !h.isAdmin(c) {
		c.JSON(http.StatusForbidden, apperrors.New("access denied", 40300))
		return
	}
	id, err := parseID(c)
	if err != nil {
		c.JSON(http.StatusBadRequest, apperrors.New("invalid id format", apperrors.CodeBadRequest))
		return
	}
	if err := h.services.Tag.Delete(c.Request.Context(), id); err != nil {
		handleError(c, err)
		return
	}
	c.Status(http.StatusNoContent)
}

func (h *Handler) createComment(c *gin.Context) {
	var req dto.CommentRequestTo
	if err := c.ShouldBindJSON(&req); err != nil {
		c.JSON(http.StatusBadRequest, apperrors.New("validation error: "+err.Error(), apperrors.CodeValidationFailed))
		return
	}
	req.EditorID = c.GetInt64(middleware.CtxEditorID)
	if _, err := h.services.Article.GetByID(c.Request.Context(), req.ArticleID); err != nil {
		handleError(c, err)
		return
	}
	res, err := h.services.Comment.Create(c.Request.Context(), req)
	if err != nil {
		handleError(c, err)
		return
	}
	c.JSON(http.StatusCreated, res)
}

func (h *Handler) getCommentByID(c *gin.Context) {
	id, err := parseID(c)
	if err != nil {
		c.JSON(http.StatusBadRequest, apperrors.New("invalid id format", apperrors.CodeBadRequest))
		return
	}
	res, err := h.services.Comment.GetByID(c.Request.Context(), id)
	if err != nil {
		handleError(c, err)
		return
	}
	c.JSON(http.StatusOK, res)
}

func (h *Handler) getAllComments(c *gin.Context) {
	params := parseListParams(c)
	res, err := h.services.Comment.GetAll(c.Request.Context(), params)
	if err != nil {
		handleError(c, err)
		return
	}
	c.JSON(http.StatusOK, res)
}

func (h *Handler) updateComment(c *gin.Context) {
	var req dto.CommentRequestTo
	if err := c.ShouldBindJSON(&req); err != nil {
		c.JSON(http.StatusBadRequest, apperrors.New("validation error: "+err.Error(), apperrors.CodeValidationFailed))
		return
	}

	var id int64
	if idParam := c.Param("id"); idParam != "" {
		id, _ = strconv.ParseInt(idParam, 10, 64)
	} else {
		id = req.ID
	}

	com, err := h.services.Comment.GetByID(c.Request.Context(), id)
	if err != nil {
		handleError(c, err)
		return
	}

	if !h.hasAccess(c, com.EditorID) {
		c.JSON(http.StatusForbidden, apperrors.New("access denied", 40300))
		return
	}

	if _, err := h.services.Article.GetByID(c.Request.Context(), req.ArticleID); err != nil {
		handleError(c, err)
		return
	}

	req.EditorID = com.EditorID
	res, err := h.services.Comment.Update(c.Request.Context(), id, req)
	if err != nil {
		handleError(c, err)
		return
	}
	c.JSON(http.StatusOK, res)
}

func (h *Handler) deleteComment(c *gin.Context) {
	id, err := parseID(c)
	if err != nil {
		c.JSON(http.StatusBadRequest, apperrors.New("invalid id format", apperrors.CodeBadRequest))
		return
	}

	com, err := h.services.Comment.GetByID(c.Request.Context(), id)
	if err != nil {
		handleError(c, err)
		return
	}

	if !h.hasAccess(c, com.EditorID) {
		c.JSON(http.StatusForbidden, apperrors.New("access denied", 40300))
		return
	}

	if err := h.services.Comment.Delete(c.Request.Context(), id); err != nil {
		handleError(c, err)
		return
	}
	c.Status(http.StatusNoContent)
}
