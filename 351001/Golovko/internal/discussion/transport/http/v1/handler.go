package v1

import (
	"errors"
	"net/http"
	"strconv"

	"distcomp/internal/apperrors"
	"distcomp/internal/discussion/repository/cassandra"
	"distcomp/internal/discussion/service"
	"distcomp/internal/dto"

	"github.com/gin-gonic/gin"
)

type Handler struct {
	commentService *service.CommentService
}

func NewHandler(commentService *service.CommentService) *Handler {
	return &Handler{commentService: commentService}
}

func (h *Handler) InitRoutes(api *gin.RouterGroup) {
	v1 := api.Group("/v1.0")
	{
		comments := v1.Group("/comments")
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

func parseID(c *gin.Context) (int64, error) {
	return strconv.ParseInt(c.Param("id"), 10, 64)
}

func handleError(c *gin.Context, err error) {
	if errors.Is(err, cassandra.ErrNotFound) {
		c.JSON(http.StatusNotFound, apperrors.New("entity not found", apperrors.CodeNotFound))
		return
	}
	c.JSON(http.StatusBadRequest, apperrors.New(err.Error(), apperrors.CodeBadRequest))
}

func (h *Handler) createComment(c *gin.Context) {
	var req dto.CommentRequestTo
	if err := c.ShouldBindJSON(&req); err != nil {
		c.JSON(http.StatusBadRequest, apperrors.New("validation error: "+err.Error(), apperrors.CodeValidationFailed))
		return
	}
	res, err := h.commentService.Create(c.Request.Context(), req)
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
	res, err := h.commentService.GetByID(c.Request.Context(), id)
	if err != nil {
		handleError(c, err)
		return
	}
	c.JSON(http.StatusOK, res)
}

func (h *Handler) getAllComments(c *gin.Context) {
	res, err := h.commentService.GetAll(c.Request.Context())
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
		var err error
		id, err = strconv.ParseInt(idParam, 10, 64)
		if err != nil {
			c.JSON(http.StatusBadRequest, apperrors.New("invalid id format", apperrors.CodeBadRequest))
			return
		}
	} else {
		id = req.ID
	}

	res, err := h.commentService.Update(c.Request.Context(), id, req)
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
	if err := h.commentService.Delete(c.Request.Context(), id); err != nil {
		handleError(c, err)
		return
	}
	c.Status(http.StatusNoContent)
}