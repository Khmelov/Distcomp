package controller

import (
	"labs/discussion/internal/controller/note"
	"labs/discussion/internal/service"

	"github.com/gin-gonic/gin"
)

type AppController interface {
	NoteController() NoteController
	RegisterRoutes(r *gin.RouterGroup)
}

func New(services service.AppService) AppController {
	return &appController{
		noteController: note.NewNoteController(services.NoteService()),
	}
}

type appController struct {
	noteController NoteController
}

func (c *appController) NoteController() NoteController {
	return c.noteController
}

func (c *appController) RegisterRoutes(r *gin.RouterGroup) {
	c.NoteController().RegisterRoutes(r)
}
