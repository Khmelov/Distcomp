package editor

import (
	"errors"
	"net/http"
	"strconv"

	"github.com/gin-gonic/gin"

	editordto "lab1/internal/dto/editor"
	editormodel "lab1/internal/model/editor"
	editorservice "lab1/internal/service/editor"
	httperrors "lab1/pkg/http"
)

type EditorController struct {
	editorService editorservice.Service
}

func NewEditorController(editorService editorservice.Service) *EditorController {
	return &EditorController{editorService: editorService}
}

func (c *EditorController) RegisterRoutes(r *gin.RouterGroup) {
	editors := r.Group("/editors")
	{
		editors.POST("", c.CreateEditor)
		editors.GET("", c.ListEditors)
		editors.GET("/:id", c.GetEditor)
		// Убрали /:id, так как тесты отправляют PUT на корень
		editors.PUT("", c.UpdateEditor)
		editors.DELETE("/:id", c.DeleteEditor)
	}
}

func (c *EditorController) CreateEditor(ctx *gin.Context) {
	var req editordto.CreateEditorRequest
	if err := ctx.ShouldBindJSON(&req); err != nil {
		ctx.JSON(http.StatusBadRequest, httperrors.ErrorResponse{
			ErrorMessage: httperrors.ErrMessageInvalidRequestBody,
			ErrorCode:    httperrors.ErrCodeInvalidRequestBody,
		})
		return
	}

	input := &editormodel.CreateEditorInput{
		Login:     req.Login,
		Password:  req.Password,
		Firstname: req.Firstname,
		Lastname:  req.Lastname,
	}

	editor, err := c.editorService.CreateEditor(ctx, input)
	if err != nil {
		if errors.Is(err, editormodel.ErrLoginTaken) {
			ctx.JSON(http.StatusForbidden, httperrors.ErrorResponse{
				ErrorMessage: httperrors.ErrMessageConflict,
				ErrorCode:    httperrors.ErrCodeConflict,
			})
			return
		}
		ctx.JSON(http.StatusInternalServerError, httperrors.ErrorResponse{
			ErrorMessage: httperrors.ErrMessageInternalError,
			ErrorCode:    httperrors.ErrCodeInternalError,
		})
		return
	}

	ctx.JSON(http.StatusCreated, editordto.ToResponse(editor))
}

func (c *EditorController) GetEditor(ctx *gin.Context) {
	id, err := getIDParam(ctx)
	if err != nil {
		ctx.JSON(http.StatusBadRequest, httperrors.ErrorResponse{
			ErrorMessage: httperrors.ErrMessageInvalidID,
			ErrorCode:    httperrors.ErrCodeInvalidID,
		})
		return
	}

	editor, err := c.editorService.GetEditor(ctx, id)
	if err != nil {
		if errors.Is(err, editormodel.ErrNotFound) {
			ctx.JSON(http.StatusNotFound, httperrors.ErrorResponse{
				ErrorMessage: httperrors.ErrMessageNotFound,
				ErrorCode:    httperrors.ErrCodeNotFound,
			})
			return
		}
		ctx.JSON(http.StatusInternalServerError, httperrors.ErrorResponse{
			ErrorMessage: httperrors.ErrMessageInternalError,
			ErrorCode:    httperrors.ErrCodeInternalError,
		})
		return
	}

	ctx.JSON(http.StatusOK, editordto.ToResponse(editor))
}

func (c *EditorController) ListEditors(ctx *gin.Context) {
	limit, offset := getPaginationParams(ctx)

	editors, err := c.editorService.ListEditors(ctx, limit, offset)
	if err != nil {
		ctx.JSON(http.StatusInternalServerError, httperrors.ErrorResponse{
			ErrorMessage: httperrors.ErrMessageInternalError,
			ErrorCode:    httperrors.ErrCodeInternalError,
		})
		return
	}

	ctx.JSON(http.StatusOK, editordto.ToResponseList(editors))
}

func (c *EditorController) UpdateEditor(ctx *gin.Context) {
	var req editordto.UpdateEditorRequest
	if err := ctx.ShouldBindJSON(&req); err != nil {
		ctx.JSON(http.StatusBadRequest, httperrors.ErrorResponse{
			ErrorMessage: httperrors.ErrMessageInvalidRequestBody,
			ErrorCode:    httperrors.ErrCodeInvalidRequestBody,
		})
		return
	}

	input := &editormodel.UpdateEditorInput{
		Login:     req.Login,
		Password:  req.Password,
		Firstname: req.Firstname,
		Lastname:  req.Lastname,
	}

	editor, err := c.editorService.UpdateEditor(ctx, req.ID, input)
	if err != nil {
		if errors.Is(err, editormodel.ErrNotFound) {
			ctx.JSON(http.StatusNotFound, httperrors.ErrorResponse{
				ErrorMessage: httperrors.ErrMessageNotFound,
				ErrorCode:    httperrors.ErrCodeNotFound,
			})
			return
		}
		if errors.Is(err, editormodel.ErrLoginTaken) {
			ctx.JSON(http.StatusConflict, httperrors.ErrorResponse{
				ErrorMessage: httperrors.ErrMessageConflict,
				ErrorCode:    httperrors.ErrCodeConflict,
			})
			return
		}
		ctx.JSON(http.StatusInternalServerError, httperrors.ErrorResponse{
			ErrorMessage: httperrors.ErrMessageInternalError,
			ErrorCode:    httperrors.ErrCodeInternalError,
		})
		return
	}

	ctx.JSON(http.StatusOK, editordto.ToResponse(editor))
}

func (c *EditorController) DeleteEditor(ctx *gin.Context) {
	id, err := getIDParam(ctx)
	if err != nil {
		ctx.JSON(http.StatusBadRequest, httperrors.ErrorResponse{
			ErrorMessage: httperrors.ErrMessageInvalidID,
			ErrorCode:    httperrors.ErrCodeInvalidID,
		})
		return
	}

	err = c.editorService.DeleteEditor(ctx, id)
	if err != nil {
		if errors.Is(err, editormodel.ErrNotFound) {
			ctx.JSON(http.StatusNotFound, httperrors.ErrorResponse{
				ErrorMessage: httperrors.ErrMessageNotFound,
				ErrorCode:    httperrors.ErrCodeNotFound,
			})
			return
		}
		ctx.JSON(http.StatusInternalServerError, httperrors.ErrorResponse{
			ErrorMessage: httperrors.ErrMessageInternalError,
			ErrorCode:    httperrors.ErrCodeInternalError,
		})
		return
	}

	ctx.Status(http.StatusNoContent)
}

func getIDParam(ctx *gin.Context) (int64, error) {
	return strconv.ParseInt(ctx.Param("id"), 10, 64)
}

func getPaginationParams(ctx *gin.Context) (limit, offset int) {
	limit, _ = strconv.Atoi(ctx.DefaultQuery("limit", "10"))
	offset, _ = strconv.Atoi(ctx.DefaultQuery("offset", "0"))
	if limit < 0 {
		limit = 10
	}
	if limit > 100 {
		limit = 100
	}
	if offset < 0 {
		offset = 0
	}
	return limit, offset
}
