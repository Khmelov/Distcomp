package controller

import (
	"github.com/gin-gonic/gin"
)

type EditorController interface {
	RegisterRoutes(r *gin.RouterGroup)
}

type IssueController interface {
	RegisterRoutes(r *gin.RouterGroup)
}

type NoteController interface {
	RegisterRoutes(r *gin.RouterGroup)
}

type StickerController interface {
	RegisterRoutes(r *gin.RouterGroup)
}
