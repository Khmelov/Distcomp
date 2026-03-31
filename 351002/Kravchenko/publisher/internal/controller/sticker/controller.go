package sticker

import (
	"errors"
	stickerservice "labs/publisher/internal/service/sticker"
	stickerdto "labs/shared/dto/sticker"
	stickermodel "labs/shared/model/sticker"
	httperrors "labs/shared/pkg/http"
	"net/http"
	"strconv"

	"github.com/gin-gonic/gin"
)

type Controller struct {
	stickerService stickerservice.Service
}

func New(stickerService stickerservice.Service) *Controller {
	return &Controller{stickerService: stickerService}
}

func (c *Controller) RegisterRoutes(r *gin.RouterGroup) {
	stickers := r.Group("/stickers")
	{
		stickers.POST("", c.CreateSticker)
		stickers.GET("", c.ListStickers)
		stickers.GET("/:id", c.GetSticker)
		stickers.PUT("/:id", c.UpdateSticker)
		stickers.DELETE("/:id", c.DeleteSticker)
	}
}

func (c *Controller) CreateSticker(ctx *gin.Context) {
	var req stickerdto.CreateStickerRequest
	if err := ctx.ShouldBindJSON(&req); err != nil {
		ctx.JSON(http.StatusBadRequest, httperrors.ErrorResponse{
			ErrorMessage: httperrors.ErrMessageInvalidRequestBody,
			ErrorCode:    httperrors.ErrCodeInvalidRequestBody,
		})
		return
	}

	input := &stickermodel.CreateStickerInput{
		Name: req.Name,
	}

	sticker, err := c.stickerService.CreateSticker(ctx, input)
	if err != nil {
		if errors.Is(err, stickermodel.ErrNameTaken) {
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

	ctx.JSON(http.StatusCreated, stickerdto.ToResponse(sticker))
}

func (c *Controller) GetSticker(ctx *gin.Context) {
	id, err := getIDParam(ctx)
	if err != nil {
		ctx.JSON(http.StatusBadRequest, httperrors.ErrorResponse{
			ErrorMessage: httperrors.ErrMessageInvalidID,
			ErrorCode:    httperrors.ErrCodeInvalidID,
		})
		return
	}

	sticker, err := c.stickerService.GetSticker(ctx, id)
	if err != nil {
		if errors.Is(err, stickermodel.ErrNotFound) {
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

	ctx.JSON(http.StatusOK, stickerdto.ToResponse(sticker))
}

func (c *Controller) ListStickers(ctx *gin.Context) {
	limit, offset := getPaginationParams(ctx)

	stickers, err := c.stickerService.ListStickers(ctx, limit, offset)
	if err != nil {
		ctx.JSON(http.StatusInternalServerError, httperrors.ErrorResponse{
			ErrorMessage: httperrors.ErrMessageInternalError,
			ErrorCode:    httperrors.ErrCodeInternalError,
		})
		return
	}

	ctx.JSON(http.StatusOK, stickerdto.ToResponseList(stickers))
}

func (c *Controller) UpdateSticker(ctx *gin.Context) {
	id, err := getIDParam(ctx)
	if err != nil {
		ctx.JSON(http.StatusBadRequest, httperrors.ErrorResponse{
			ErrorMessage: httperrors.ErrMessageInvalidID,
			ErrorCode:    httperrors.ErrCodeInvalidID,
		})
		return
	}

	var req stickerdto.UpdateStickerRequest
	if err := ctx.ShouldBindJSON(&req); err != nil {
		ctx.JSON(http.StatusBadRequest, httperrors.ErrorResponse{
			ErrorMessage: httperrors.ErrMessageInvalidRequestBody,
			ErrorCode:    httperrors.ErrCodeInvalidRequestBody,
		})
		return
	}

	input := &stickermodel.UpdateStickerInput{
		Name: req.Name,
	}

	sticker, err := c.stickerService.UpdateSticker(ctx, id, input)
	if err != nil {
		if errors.Is(err, stickermodel.ErrNotFound) {
			ctx.JSON(http.StatusNotFound, httperrors.ErrorResponse{
				ErrorMessage: httperrors.ErrMessageNotFound,
				ErrorCode:    httperrors.ErrCodeNotFound,
			})
			return
		}
		if errors.Is(err, stickermodel.ErrNameTaken) {
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

	ctx.JSON(http.StatusOK, stickerdto.ToResponse(sticker))
}

func (c *Controller) DeleteSticker(ctx *gin.Context) {
	id, err := getIDParam(ctx)
	if err != nil {
		ctx.JSON(http.StatusBadRequest, httperrors.ErrorResponse{
			ErrorMessage: httperrors.ErrMessageInvalidID,
			ErrorCode:    httperrors.ErrCodeInvalidID,
		})
		return
	}

	err = c.stickerService.DeleteSticker(ctx, id)
	if err != nil {
		if errors.Is(err, stickermodel.ErrNotFound) {
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
