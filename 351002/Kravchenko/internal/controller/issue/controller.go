package issue

import (
	"errors"
	"net/http"
	"strconv"

	"github.com/gin-gonic/gin"

	issuedto "lab1/internal/dto/issue"
	editormodel "lab1/internal/model/editor"
	issuemodel "lab1/internal/model/issue"
	issueservice "lab1/internal/service/issue"
	httperrors "lab1/pkg/http"
)

type IssueController struct {
	issueService issueservice.Service
}

func NewIssueController(issueService issueservice.Service) *IssueController {
	return &IssueController{issueService: issueService}
}

func (c *IssueController) RegisterRoutes(r *gin.RouterGroup) {
	issues := r.Group("/issues")
	{
		issues.POST("", c.CreateIssue)
		issues.GET("", c.ListIssues)
		issues.GET("/:id", c.GetIssue)
		// Убрали /:id
		issues.PUT("", c.UpdateIssue)
		issues.DELETE("/:id", c.DeleteIssue)
	}
}

func (c *IssueController) CreateIssue(ctx *gin.Context) {
	var req issuedto.CreateIssueRequest
	if err := ctx.ShouldBindJSON(&req); err != nil {
		ctx.JSON(http.StatusBadRequest, httperrors.ErrorResponse{
			ErrorMessage: httperrors.ErrMessageInvalidRequestBody,
			ErrorCode:    httperrors.ErrCodeInvalidRequestBody,
		})
		return
	}

	input := &issuemodel.CreateIssueInput{
		EditorID: req.EditorID,
		Title:    req.Title,
		Content:  req.Content,
		Stickers: req.Stickers,
	}

	issue, err := c.issueService.CreateIssue(ctx, input)
	if err != nil {
		if errors.Is(err, editormodel.ErrNotFound) {
			ctx.JSON(http.StatusBadRequest, httperrors.ErrorResponse{
				ErrorMessage: "Editor not found",
				ErrorCode:    httperrors.ErrCodeInvalidParam,
			})
			return
		}
		ctx.JSON(http.StatusForbidden, httperrors.ErrorResponse{
			ErrorMessage: httperrors.ErrMessageInternalError,
			ErrorCode:    httperrors.ErrCodeInternalError,
		})
		return
	}

	ctx.JSON(http.StatusCreated, issuedto.ToResponse(issue))
}

func (c *IssueController) GetIssue(ctx *gin.Context) {
	id, err := getIDParam(ctx)
	if err != nil {
		ctx.JSON(http.StatusBadRequest, httperrors.ErrorResponse{
			ErrorMessage: httperrors.ErrMessageInvalidID,
			ErrorCode:    httperrors.ErrCodeInvalidID,
		})
		return
	}

	issue, err := c.issueService.GetIssue(ctx, id)
	if err != nil {
		if errors.Is(err, issuemodel.ErrNotFound) {
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

	ctx.JSON(http.StatusOK, issuedto.ToResponse(issue))
}

func (c *IssueController) ListIssues(ctx *gin.Context) {
	limit, offset := getPaginationParams(ctx)

	issues, err := c.issueService.ListIssues(ctx, limit, offset)
	if err != nil {
		ctx.JSON(http.StatusInternalServerError, httperrors.ErrorResponse{
			ErrorMessage: httperrors.ErrMessageInternalError,
			ErrorCode:    httperrors.ErrCodeInternalError,
		})
		return
	}

	ctx.JSON(http.StatusOK, issuedto.ToResponseList(issues))
}

func (c *IssueController) UpdateIssue(ctx *gin.Context) {
	var req issuedto.UpdateIssueRequest
	if err := ctx.ShouldBindJSON(&req); err != nil {
		ctx.JSON(http.StatusBadRequest, httperrors.ErrorResponse{
			ErrorMessage: httperrors.ErrMessageInvalidRequestBody,
			ErrorCode:    httperrors.ErrCodeInvalidRequestBody,
		})
		return
	}

	input := &issuemodel.UpdateIssueInput{
		Title:    req.Title,
		Content:  req.Content,
		Stickers: req.Stickers,
	}

	issue, err := c.issueService.UpdateIssue(ctx, req.ID, input)
	if err != nil {
		if errors.Is(err, issuemodel.ErrNotFound) {
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

	ctx.JSON(http.StatusOK, issuedto.ToResponse(issue))
}

func (c *IssueController) DeleteIssue(ctx *gin.Context) {
	id, err := getIDParam(ctx)
	if err != nil {
		ctx.JSON(http.StatusBadRequest, httperrors.ErrorResponse{
			ErrorMessage: httperrors.ErrMessageInvalidID,
			ErrorCode:    httperrors.ErrCodeInvalidID,
		})
		return
	}

	err = c.issueService.DeleteIssue(ctx, id)
	if err != nil {
		if errors.Is(err, issuemodel.ErrNotFound) {
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
