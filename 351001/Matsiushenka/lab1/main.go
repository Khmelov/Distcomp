package main

import (
	"lab-rest/internal/handlers"
	"lab-rest/internal/models"
	"lab-rest/internal/repository"
	"net/http"
	"strconv"

	"github.com/gin-gonic/gin"
)

func main() {
	r := gin.Default()

	r.NoRoute(func(c *gin.Context) {
		handlers.SendErr(c, 404, 01, "Route not found")
	})

	v1 := r.Group("/api/v1.0")

	eRepo := repository.NewStorage[models.EditorResponseTo]()
	tRepo := repository.NewStorage[models.TopicResponseTo]()
	mRepo := repository.NewStorage[models.MarkerResponseTo]()
	nRepo := repository.NewStorage[models.NoteResponseTo]()

	eH := &handlers.BaseHandler[models.EditorRequestTo, models.EditorResponseTo]{
		Repo: eRepo,
		MapTo: func(r models.EditorRequestTo, id int64) models.EditorResponseTo {
			return models.EditorResponseTo{ID: id, Login: r.Login, FirstName: r.FirstName, LastName: r.LastName}
		},
	}
	v1.GET("/editors", eH.GetAll)
	v1.GET("/editors/:id", eH.GetByID)
	v1.POST("/editors", eH.Create)
	v1.DELETE("/editors/:id", eH.Delete)

	editorUpdate := func(c *gin.Context) {
		var req struct {
			ID int64 `json:"id"`
			models.EditorRequestTo
		}
		if err := c.ShouldBindJSON(&req); err != nil {
			handlers.SendErr(c, 400, 01, "Validation failed")
			return
		}
		id := req.ID
		if id == 0 {
			id, _ = strconv.ParseInt(c.Param("id"), 10, 64)
		}
		newVal := models.EditorResponseTo{ID: id, Login: req.Login, FirstName: req.FirstName, LastName: req.LastName}
		if ok := eRepo.Update(id, newVal); ok {
			c.JSON(http.StatusOK, newVal)
		} else {
			handlers.SendErr(c, 404, 01, "Not found")
		}
	}
	v1.PUT("/editors", editorUpdate)
	v1.PUT("/editors/:id", editorUpdate)

	tH := &handlers.BaseHandler[models.TopicRequestTo, models.TopicResponseTo]{
		Repo: tRepo,
		MapTo: func(r models.TopicRequestTo, id int64) models.TopicResponseTo {
			return models.TopicResponseTo{ID: id, EditorID: r.EditorID, Title: r.Title, Content: r.Content}
		},
	}
	v1.GET("/topics", tH.GetAll)
	v1.GET("/topics/:id", tH.GetByID)
	v1.POST("/topics", tH.Create)
	v1.DELETE("/topics/:id", tH.Delete)

	topicUpdate := func(c *gin.Context) {
		var req struct {
			ID int64 `json:"id"`
			models.TopicRequestTo
		}
		if err := c.ShouldBindJSON(&req); err != nil {
			handlers.SendErr(c, 400, 01, "Validation failed")
			return
		}
		id := req.ID
		if id == 0 {
			id, _ = strconv.ParseInt(c.Param("id"), 10, 64)
		}
		newVal := models.TopicResponseTo{ID: id, EditorID: req.EditorID, Title: req.Title, Content: req.Content}
		if ok := tRepo.Update(id, newVal); ok {
			c.JSON(http.StatusOK, newVal)
		} else {
			handlers.SendErr(c, 404, 01, "Not found")
		}
	}
	v1.PUT("/topics", topicUpdate)
	v1.PUT("/topics/:id", topicUpdate)

	mH := &handlers.BaseHandler[models.MarkerRequestTo, models.MarkerResponseTo]{
		Repo: mRepo,
		MapTo: func(r models.MarkerRequestTo, id int64) models.MarkerResponseTo {
			return models.MarkerResponseTo{ID: id, Name: r.Name}
		},
	}
	v1.GET("/markers", mH.GetAll)
	v1.GET("/markers/:id", mH.GetByID)
	v1.POST("/markers", mH.Create)
	v1.DELETE("/markers/:id", mH.Delete)

	markerUpdate := func(c *gin.Context) {
		var req struct {
			ID int64 `json:"id"`
			models.MarkerRequestTo
		}
		if err := c.ShouldBindJSON(&req); err != nil {
			handlers.SendErr(c, 400, 01, "Validation failed")
			return
		}
		id := req.ID
		if id == 0 {
			id, _ = strconv.ParseInt(c.Param("id"), 10, 64)
		}
		newVal := models.MarkerResponseTo{ID: id, Name: req.Name}
		if ok := mRepo.Update(id, newVal); ok {
			c.JSON(http.StatusOK, newVal)
		} else {
			handlers.SendErr(c, 404, 01, "Not found")
		}
	}
	v1.PUT("/markers", markerUpdate)
	v1.PUT("/markers/:id", markerUpdate)

	nH := &handlers.BaseHandler[models.NoteRequestTo, models.NoteResponseTo]{
		Repo: nRepo,
		MapTo: func(r models.NoteRequestTo, id int64) models.NoteResponseTo {
			return models.NoteResponseTo{ID: id, TopicID: r.TopicID, Content: r.Content}
		},
	}
	v1.GET("/notes", nH.GetAll)
	v1.GET("/notes/:id", nH.GetByID)
	v1.POST("/notes", nH.Create)
	v1.DELETE("/notes/:id", nH.Delete)

	noteUpdate := func(c *gin.Context) {
		var req struct {
			ID int64 `json:"id"`
			models.NoteRequestTo
		}
		if err := c.ShouldBindJSON(&req); err != nil {
			handlers.SendErr(c, 400, 01, "Validation failed")
			return
		}
		id := req.ID
		if id == 0 {
			id, _ = strconv.ParseInt(c.Param("id"), 10, 64)
		}
		newVal := models.NoteResponseTo{ID: id, TopicID: req.TopicID, Content: req.Content}
		if ok := nRepo.Update(id, newVal); ok {
			c.JSON(http.StatusOK, newVal)
		} else {
			handlers.SendErr(c, 404, 01, "Not found")
		}
	}
	v1.PUT("/notes", noteUpdate)
	v1.PUT("/notes/:id", noteUpdate)

	r.Run(":24110")
}
