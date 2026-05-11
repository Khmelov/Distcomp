package router

import (
	"distcomp/internal/controller"

	"github.com/gin-gonic/gin"
)

func SetupRouter(handler *controller.Handler) *gin.Engine {
	r := gin.Default()

	v1 := r.Group("/api/v1.0")
	{
		// Editors
		v1.POST("/editors", handler.CreateEditor)
		v1.GET("/editors/:id", handler.GetEditor)
		v1.GET("/editors", handler.GetAllEditors)
		v1.PUT("/editors", handler.UpdateEditor)
		v1.DELETE("/editors/:id", handler.DeleteEditor)

		// Topics
		v1.POST("/topics", handler.CreateTopic)
		v1.GET("/topics/:id", handler.GetTopic)
		v1.GET("/topics", handler.GetAllTopics)
		v1.PUT("/topics", handler.UpdateTopic)
		v1.DELETE("/topics/:id", handler.DeleteTopic)

		// Markers
		v1.POST("/markers", handler.CreateMarker)
		v1.GET("/markers/:id", handler.GetMarker)
		v1.GET("/markers", handler.GetAllMarkers)
		v1.PUT("/markers", handler.UpdateMarker)
		v1.DELETE("/markers/:id", handler.DeleteMarker)

		// Notes
		v1.POST("/notes", handler.CreateNote)
		v1.GET("/notes/:id", handler.GetNote)
		v1.GET("/notes", handler.GetAllNotes)
		v1.PUT("/notes", handler.UpdateNote)
		v1.DELETE("/notes/:id", handler.DeleteNote)
	}

	return r
}
