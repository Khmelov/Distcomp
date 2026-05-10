package controller

import (
	"github.com/gin-gonic/gin"
)

type NoteController interface {
	RegisterRoutes(r *gin.RouterGroup)
}
