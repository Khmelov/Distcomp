package controller

import (
	"labs/publisher/internal/controller/editor"
	"labs/publisher/internal/controller/issue"
	"labs/publisher/internal/controller/note"
	stickercontroller "labs/publisher/internal/controller/sticker"
	"labs/publisher/internal/service"

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
