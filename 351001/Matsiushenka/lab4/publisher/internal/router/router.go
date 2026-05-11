package router

import (
	"publisher/internal/controller"

	"github.com/gin-gonic/gin"
)

func SetupRouter(h *controller.Handler) *gin.Engine {
	r := gin.Default()

	v1 := r.Group("/api/v1.0")
	{
		v1.POST("/editors", h.CreateEditor)
		v1.GET("/editors/:id", h.GetEditor)
		v1.GET("/editors", h.GetAllEditors)
		v1.PUT("/editors", h.UpdateEditor)
		v1.DELETE("/editors/:id", h.DeleteEditor)

		v1.POST("/topics", h.CreateTopic)
		v1.GET("/topics/:id", h.GetTopic)
		v1.GET("/topics", h.GetAllTopics)
		v1.PUT("/topics", h.UpdateTopic)
		v1.DELETE("/topics/:id", h.DeleteTopic)

		v1.POST("/markers", h.CreateMarker)
		v1.GET("/markers/:id", h.GetMarker)
		v1.GET("/markers", h.GetAllMarkers)
		v1.PUT("/markers", h.UpdateMarker)
		v1.PUT("/markers/:id", h.UpdateMarker)
		v1.DELETE("/markers/:id", h.DeleteMarker)

		v1.POST("/notes", h.ProxyNote)
		v1.GET("/notes", h.ProxyNote)
		v1.GET("/notes/:id", h.ProxyNote)
		v1.PUT("/notes", h.ProxyNote)
		v1.PUT("/notes/:id", h.ProxyNote)
		v1.DELETE("/notes/:id", h.ProxyNote)
	}

	return r
}
