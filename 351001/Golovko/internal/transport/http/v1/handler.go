package v1

import (
	"errors"
	"net/http"
	"strconv"

	"distcomp/internal/apperrors"
	"distcomp/internal/dto"
	"distcomp/internal/repository/memory"
	"distcomp/internal/service"

	"github.com/gin-gonic/gin"
)

type Handler struct {
	services *service.Manager
}

func NewHandler(services *service.Manager) *Handler {
	return &Handler{services: services}
}

func (h *Handler) InitRoutes(api *gin.RouterGroup) {
	v1 := api.Group("/v1.0")
	{
		editors := v1.Group("/editors")
		{
			editors.POST("", h.createEditor)
			editors.GET("", h.getAllEditors)
			editors.GET("/:id", h.getEditorByID)
			editors.PUT("", h.updateEditor)
			editors.PUT("/:id", h.updateEditor)
			editors.DELETE("/:id", h.deleteEditor)
		}

		articles := v1.Group("/articles")
		{
			articles.POST("", h.createArticle)
			articles.GET("", h.getAllArticles)
			articles.GET("/:id", h.getArticleByID)
			articles.PUT("", h.updateArticle)
			articles.PUT("/:id", h.updateArticle)
			articles.DELETE("/:id", h.deleteArticle)
		}

		tags := v1.Group("/tags")
		{
			tags.POST("", h.createTag)
			tags.GET("", h.getAllTags)
			tags.GET("/:id", h.getTagByID)
			tags.PUT("", h.updateTag)
			tags.PUT("/:id", h.updateTag)
			tags.DELETE("/:id", h.deleteTag)
		}

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
	if errors.Is(err, memory.ErrNotFound) {
		c.JSON(http.StatusNotFound, apperrors.New("entity not found", apperrors.CodeNotFound))
		return
	}
	c.JSON(http.StatusBadRequest, apperrors.New(err.Error(), apperrors.CodeBadRequest))
}

// @Summary      Create Editor
// @Tags         Editor
// @Accept       json
// @Produce      json
// @Param        input body dto.EditorRequestTo true "Editor details"
// @Success      201  {object}  dto.EditorResponseTo
// @Failure      400  {object}  apperrors.ErrorResponse
// @Router       /editors [post]
func (h *Handler) createEditor(c *gin.Context) {
	var req dto.EditorRequestTo
	if err := c.ShouldBindJSON(&req); err != nil {
		c.JSON(http.StatusBadRequest, apperrors.New("validation error: "+err.Error(), apperrors.CodeValidationFailed))
		return
	}
	res, err := h.services.Editor.Create(c.Request.Context(), req)
	if err != nil {
		handleError(c, err)
		return
	}
	c.JSON(http.StatusCreated, res)
}

// @Summary      Get Editor by ID
// @Tags         Editor
// @Produce      json
// @Param        id   path      int  true  "Editor ID"
// @Success      200  {object}  dto.EditorResponseTo
// @Failure      400  {object}  apperrors.ErrorResponse
// @Failure      404  {object}  apperrors.ErrorResponse
// @Router       /editors/{id} [get]
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

// @Summary      Get all Editors
// @Tags         Editor
// @Produce      json
// @Success      200  {array}   dto.EditorResponseTo
// @Router       /editors [get]
func (h *Handler) getAllEditors(c *gin.Context) {
	res, err := h.services.Editor.GetAll(c.Request.Context())
	if err != nil {
		handleError(c, err)
		return
	}
	c.JSON(http.StatusOK, res)
}

// @Summary      Update Editor
// @Tags         Editor
// @Accept       json
// @Produce      json
// @Param        id   path      int  false  "Editor ID"
// @Param        input body dto.EditorRequestTo true "Editor details"
// @Success      200  {object}  dto.EditorResponseTo
// @Failure      400  {object}  apperrors.ErrorResponse
// @Failure      404  {object}  apperrors.ErrorResponse
// @Router       /editors/{id} [put]
func (h *Handler) updateEditor(c *gin.Context) {
	var req dto.EditorRequestTo
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

	res, err := h.services.Editor.Update(c.Request.Context(), id, req)
	if err != nil {
		handleError(c, err)
		return
	}
	c.JSON(http.StatusOK, res)
}

// @Summary      Delete Editor
// @Tags         Editor
// @Param        id   path      int  true  "Editor ID"
// @Success      204
// @Failure      400  {object}  apperrors.ErrorResponse
// @Failure      404  {object}  apperrors.ErrorResponse
// @Router       /editors/{id} [delete]
func (h *Handler) deleteEditor(c *gin.Context) {
	id, err := parseID(c)
	if err != nil {
		c.JSON(http.StatusBadRequest, apperrors.New("invalid id format", apperrors.CodeBadRequest))
		return
	}
	if err := h.services.Editor.Delete(c.Request.Context(), id); err != nil {
		handleError(c, err)
		return
	}
	c.Status(http.StatusNoContent)
}

// @Summary      Create Article
// @Tags         Article
// @Accept       json
// @Produce      json
// @Param        input body dto.ArticleRequestTo true "Article details"
// @Success      201  {object}  dto.ArticleResponseTo
// @Failure      400  {object}  apperrors.ErrorResponse
// @Router       /articles [post]
func (h *Handler) createArticle(c *gin.Context) {
	var req dto.ArticleRequestTo
	if err := c.ShouldBindJSON(&req); err != nil {
		c.JSON(http.StatusBadRequest, apperrors.New("validation error: "+err.Error(), apperrors.CodeValidationFailed))
		return
	}
	res, err := h.services.Article.Create(c.Request.Context(), req)
	if err != nil {
		handleError(c, err)
		return
	}
	c.JSON(http.StatusCreated, res)
}

// @Summary      Get Article by ID
// @Tags         Article
// @Produce      json
// @Param        id   path      int  true  "Article ID"
// @Success      200  {object}  dto.ArticleResponseTo
// @Failure      400  {object}  apperrors.ErrorResponse
// @Failure      404  {object}  apperrors.ErrorResponse
// @Router       /articles/{id} [get]
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

// @Summary      Get all Articles
// @Tags         Article
// @Produce      json
// @Success      200  {array}   dto.ArticleResponseTo
// @Router       /articles [get]
func (h *Handler) getAllArticles(c *gin.Context) {
	res, err := h.services.Article.GetAll(c.Request.Context())
	if err != nil {
		handleError(c, err)
		return
	}
	c.JSON(http.StatusOK, res)
}

// @Summary      Update Article
// @Tags         Article
// @Accept       json
// @Produce      json
// @Param        id   path      int  false  "Article ID"
// @Param        input body dto.ArticleRequestTo true "Article details"
// @Success      200  {object}  dto.ArticleResponseTo
// @Failure      400  {object}  apperrors.ErrorResponse
// @Failure      404  {object}  apperrors.ErrorResponse
// @Router       /articles/{id} [put]
func (h *Handler) updateArticle(c *gin.Context) {
	var req dto.ArticleRequestTo
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

	res, err := h.services.Article.Update(c.Request.Context(), id, req)
	if err != nil {
		handleError(c, err)
		return
	}
	c.JSON(http.StatusOK, res)
}

// @Summary      Delete Article
// @Tags         Article
// @Param        id   path      int  true  "Article ID"
// @Success      204
// @Failure      400  {object}  apperrors.ErrorResponse
// @Failure      404  {object}  apperrors.ErrorResponse
// @Router       /articles/{id} [delete]
func (h *Handler) deleteArticle(c *gin.Context) {
	id, err := parseID(c)
	if err != nil {
		c.JSON(http.StatusBadRequest, apperrors.New("invalid id format", apperrors.CodeBadRequest))
		return
	}
	if err := h.services.Article.Delete(c.Request.Context(), id); err != nil {
		handleError(c, err)
		return
	}
	c.Status(http.StatusNoContent)
}

// @Summary      Create Tag
// @Tags         Tag
// @Accept       json
// @Produce      json
// @Param        input body dto.TagRequestTo true "Tag details"
// @Success      201  {object}  dto.TagResponseTo
// @Failure      400  {object}  apperrors.ErrorResponse
// @Router       /tags [post]
func (h *Handler) createTag(c *gin.Context) {
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

// @Summary      Get Tag by ID
// @Tags         Tag
// @Produce      json
// @Param        id   path      int  true  "Tag ID"
// @Success      200  {object}  dto.TagResponseTo
// @Failure      400  {object}  apperrors.ErrorResponse
// @Failure      404  {object}  apperrors.ErrorResponse
// @Router       /tags/{id} [get]
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

// @Summary      Get all Tags
// @Tags         Tag
// @Produce      json
// @Success      200  {array}   dto.TagResponseTo
// @Router       /tags [get]
func (h *Handler) getAllTags(c *gin.Context) {
	res, err := h.services.Tag.GetAll(c.Request.Context())
	if err != nil {
		handleError(c, err)
		return
	}
	c.JSON(http.StatusOK, res)
}

// @Summary      Update Tag
// @Tags         Tag
// @Accept       json
// @Produce      json
// @Param        id   path      int  false  "Tag ID"
// @Param        input body dto.TagRequestTo true "Tag details"
// @Success      200  {object}  dto.TagResponseTo
// @Failure      400  {object}  apperrors.ErrorResponse
// @Failure      404  {object}  apperrors.ErrorResponse
// @Router       /tags/{id} [put]
func (h *Handler) updateTag(c *gin.Context) {
	var req dto.TagRequestTo
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

	res, err := h.services.Tag.Update(c.Request.Context(), id, req)
	if err != nil {
		handleError(c, err)
		return
	}
	c.JSON(http.StatusOK, res)
}

// @Summary      Delete Tag
// @Tags         Tag
// @Param        id   path      int  true  "Tag ID"
// @Success      204
// @Failure      400  {object}  apperrors.ErrorResponse
// @Failure      404  {object}  apperrors.ErrorResponse
// @Router       /tags/{id} [delete]
func (h *Handler) deleteTag(c *gin.Context) {
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

// @Summary      Create Comment
// @Tags         Comment
// @Accept       json
// @Produce      json
// @Param        input body dto.CommentRequestTo true "Comment details"
// @Success      201  {object}  dto.CommentResponseTo
// @Failure      400  {object}  apperrors.ErrorResponse
// @Router       /comments [post]
func (h *Handler) createComment(c *gin.Context) {
	var req dto.CommentRequestTo
	if err := c.ShouldBindJSON(&req); err != nil {
		c.JSON(http.StatusBadRequest, apperrors.New("validation error: "+err.Error(), apperrors.CodeValidationFailed))
		return
	}
	res, err := h.services.Comment.Create(c.Request.Context(), req)
	if err != nil {
		handleError(c, err)
		return
	}
	c.JSON(http.StatusCreated, res)
}

// @Summary      Get Comment by ID
// @Tags         Comment
// @Produce      json
// @Param        id   path      int  true  "Comment ID"
// @Success      200  {object}  dto.CommentResponseTo
// @Failure      400  {object}  apperrors.ErrorResponse
// @Failure      404  {object}  apperrors.ErrorResponse
// @Router       /comments/{id} [get]
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

// @Summary      Get all Comments
// @Tags         Comment
// @Produce      json
// @Success      200  {array}   dto.CommentResponseTo
// @Router       /comments [get]
func (h *Handler) getAllComments(c *gin.Context) {
	res, err := h.services.Comment.GetAll(c.Request.Context())
	if err != nil {
		handleError(c, err)
		return
	}
	c.JSON(http.StatusOK, res)
}

// @Summary      Update Comment
// @Tags         Comment
// @Accept       json
// @Produce      json
// @Param        id   path      int  false  "Comment ID"
// @Param        input body dto.CommentRequestTo true "Comment details"
// @Success      200  {object}  dto.CommentResponseTo
// @Failure      400  {object}  apperrors.ErrorResponse
// @Failure      404  {object}  apperrors.ErrorResponse
// @Router       /comments/{id} [put]
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

	res, err := h.services.Comment.Update(c.Request.Context(), id, req)
	if err != nil {
		handleError(c, err)
		return
	}
	c.JSON(http.StatusOK, res)
}

// @Summary      Delete Comment
// @Tags         Comment
// @Param        id   path      int  true  "Comment ID"
// @Success      204
// @Failure      400  {object}  apperrors.ErrorResponse
// @Failure      404  {object}  apperrors.ErrorResponse
// @Router       /comments/{id} [delete]
func (h *Handler) deleteComment(c *gin.Context) {
	id, err := parseID(c)
	if err != nil {
		c.JSON(http.StatusBadRequest, apperrors.New("invalid id format", apperrors.CodeBadRequest))
		return
	}
	if err := h.services.Comment.Delete(c.Request.Context(), id); err != nil {
		handleError(c, err)
		return
	}
	c.Status(http.StatusNoContent)
}