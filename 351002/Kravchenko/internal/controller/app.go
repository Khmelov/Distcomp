package controller

import (
	"lab1/internal/controller/editor"
	"lab1/internal/controller/issue"
	"lab1/internal/controller/note"
	stickercontroller "lab1/internal/controller/sticker"
	"lab1/internal/service"

	"github.com/gin-gonic/gin"
)

type AppController interface {
	EditorController() EditorController
	IssueController() IssueController
	NoteController() NoteController
	StickerController() StickerController
	RegisterRoutes(r *gin.RouterGroup)
}

func New(services service.AppService) AppController {
	return &appController{
		editorController:  editor.NewEditorController(services.EditorService()),
		issueController:   issue.NewIssueController(services.IssueService()),
		noteController:    note.NewNoteController(services.NoteService()),
		stickerController: stickercontroller.New(services.StickerService()),
	}
}

type appController struct {
	editorController  EditorController
	issueController   IssueController
	noteController    NoteController
	stickerController StickerController
}

func (c *appController) EditorController() EditorController {
	return c.editorController
}

func (c *appController) IssueController() IssueController {
	return c.issueController
}

func (c *appController) NoteController() NoteController {
	return c.noteController
}

func (c *appController) StickerController() StickerController {
	return c.stickerController
}

func (c *appController) RegisterRoutes(r *gin.RouterGroup) {
	c.EditorController().RegisterRoutes(r)
	c.IssueController().RegisterRoutes(r)
	c.NoteController().RegisterRoutes(r)
	c.StickerController().RegisterRoutes(r)
}
