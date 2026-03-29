package note

import (
	"errors"
	noteservice "labs/discussion/internal/service/note"
	notedto "labs/shared/dto/note"
	"labs/shared/model/issue"
	notemodel "labs/shared/model/note"
	httperrors "labs/shared/pkg/http"
	"net/http"
	"strconv"

	"github.com/gin-gonic/gin"
)

type NoteController struct {
	noteService noteservice.Service
}

func NewNoteController(noteService noteservice.Service) *NoteController {
	return &NoteController{noteService: noteService}
}

func (c *NoteController) RegisterRoutes(r *gin.RouterGroup) {
	notes := r.Group("/notes")
	{
		notes.POST("", c.CreateNote)
		notes.GET("", c.ListNotes)
		notes.GET("/:id", c.GetNote)
		notes.PUT("/:id", c.UpdateNote)
		notes.DELETE("/:id", c.DeleteNote)
	}
}

func (c *NoteController) CreateNote(ctx *gin.Context) {
	var req notedto.CreateNoteRequest
	if err := ctx.ShouldBindJSON(&req); err != nil {
		ctx.JSON(http.StatusBadRequest, httperrors.ErrorResponse{
			ErrorMessage: httperrors.ErrMessageInvalidRequestBody,
			ErrorCode:    httperrors.ErrCodeInvalidRequestBody,
		})
		return
	}

	input := &notemodel.CreateNoteInput{
		IssueID: req.IssueID,
		Content: req.Content,
	}

	note, err := c.noteService.CreateNote(ctx, input)
	if err != nil {
		if errors.Is(err, issue.ErrNotFound) {
			ctx.JSON(http.StatusBadRequest, httperrors.ErrorResponse{
				ErrorMessage: "Issue not found",
				ErrorCode:    httperrors.ErrCodeInvalidParam,
			})
			return
		}
		ctx.JSON(http.StatusInternalServerError, httperrors.ErrorResponse{
			ErrorMessage: httperrors.ErrMessageInternalError,
			ErrorCode:    httperrors.ErrCodeInternalError,
		})
		return
	}

	ctx.JSON(http.StatusCreated, notedto.ToResponse(note))
}

func (c *NoteController) GetNote(ctx *gin.Context) {
	id, err := getIDParam(ctx)
	if err != nil {
		ctx.JSON(http.StatusBadRequest, httperrors.ErrorResponse{
			ErrorMessage: httperrors.ErrMessageInvalidID,
			ErrorCode:    httperrors.ErrCodeInvalidID,
		})
		return
	}

	note, err := c.noteService.GetNote(ctx, id)
	if err != nil {
		if errors.Is(err, notemodel.ErrNotFound) {
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

	ctx.JSON(http.StatusOK, notedto.ToResponse(note))
}

func (c *NoteController) ListNotes(ctx *gin.Context) {
	limit, offset := getPaginationParams(ctx)

	notes, err := c.noteService.ListNotes(ctx, limit, offset)
	if err != nil {
		ctx.JSON(http.StatusInternalServerError, httperrors.ErrorResponse{
			ErrorMessage: httperrors.ErrMessageInternalError,
			ErrorCode:    httperrors.ErrCodeInternalError,
		})
		return
	}

	ctx.JSON(http.StatusOK, notedto.ToResponseList(notes))
}

func (c *NoteController) UpdateNote(ctx *gin.Context) {
	id, err := getIDParam(ctx)
	if err != nil {
		ctx.JSON(http.StatusBadRequest, httperrors.ErrorResponse{
			ErrorMessage: httperrors.ErrMessageInvalidID,
			ErrorCode:    httperrors.ErrCodeInvalidID,
		})
		return
	}

	var req notedto.UpdateNoteRequest
	if err := ctx.ShouldBindJSON(&req); err != nil {
		ctx.JSON(http.StatusBadRequest, httperrors.ErrorResponse{
			ErrorMessage: httperrors.ErrMessageInvalidRequestBody,
			ErrorCode:    httperrors.ErrCodeInvalidRequestBody,
		})
		return
	}

	input := &notemodel.UpdateNoteInput{
		Content: req.Content,
	}

	note, err := c.noteService.UpdateNote(ctx, id, input)
	if err != nil {
		if errors.Is(err, notemodel.ErrNotFound) {
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

	ctx.JSON(http.StatusOK, notedto.ToResponse(note))
}

func (c *NoteController) DeleteNote(ctx *gin.Context) {
	id, err := getIDParam(ctx)
	if err != nil {
		ctx.JSON(http.StatusBadRequest, httperrors.ErrorResponse{
			ErrorMessage: httperrors.ErrMessageInvalidID,
			ErrorCode:    httperrors.ErrCodeInvalidID,
		})
		return
	}

	err = c.noteService.DeleteNote(ctx, id)
	if err != nil {
		if errors.Is(err, notemodel.ErrNotFound) {
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
