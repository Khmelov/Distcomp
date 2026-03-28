package main

import (
	"log"

	"github.com/bsuir/rest-api/internal/controller"
	"github.com/bsuir/rest-api/internal/repository/inmemory"
	"github.com/bsuir/rest-api/internal/service"
	"github.com/gin-gonic/gin"
)

func main() {
	// Инициализация репозиториев
	authorRepo := inmemory.NewAuthorRepositoryInMemory()
	issueRepo := inmemory.NewIssueRepositoryInMemory()
	markerRepo := inmemory.NewMarkerRepositoryInMemory()
	noteRepo := inmemory.NewNoteRepositoryInMemory()

	// Инициализация сервисов
	authorService := service.NewAuthorService(authorRepo)
	markerService := service.NewMarkerService(markerRepo)
	issueService := service.NewIssueService(issueRepo, authorRepo, markerRepo)
	noteService := service.NewNoteService(noteRepo, issueRepo)

	// Инициализация контроллеров
	authorController := controller.NewAuthorController(authorService)
	issueController := controller.NewIssueController(issueService)
	markerController := controller.NewMarkerController(markerService)
	noteController := controller.NewNoteController(noteService)

	// Настройка роутера
	r := gin.Default()

	// Группа API v1.0
	v1 := r.Group("/api/v1.0")
	{
		// Authors routes
		authors := v1.Group("/authors")
		{
			authors.POST("", authorController.Create)
			authors.GET("", authorController.GetAll)
			authors.GET("/:id", authorController.GetByID)
			authors.PUT("/:id", authorController.Update)
			authors.DELETE("/:id", authorController.Delete)
		}

		// Issues routes
		issues := v1.Group("/issues")
		{
			issues.POST("", issueController.Create)
			issues.GET("", issueController.GetAll)
			issues.GET("/:id", issueController.GetByID)
			issues.PUT("/:id", issueController.Update)
			issues.DELETE("/:id", issueController.Delete)
			issues.GET("/:id/author", issueController.GetAuthorByIssueID)
			issues.GET("/:id/markers", issueController.GetMarkersByIssueID)
			issues.GET("/:id/notes", noteController.GetByIssueID)
			issues.GET("/search", issueController.GetByFilters)
		}

		// Markers routes
		markers := v1.Group("/markers")
		{
			markers.POST("", markerController.Create)
			markers.GET("", markerController.GetAll)
			markers.GET("/:id", markerController.GetByID)
			markers.PUT("/:id", markerController.Update)
			markers.DELETE("/:id", markerController.Delete)
		}

		// Notes routes
		notes := v1.Group("/notes")
		{
			notes.POST("", noteController.Create)
			notes.GET("", noteController.GetAll)
			notes.GET("/:id", noteController.GetByID)
			notes.PUT("/:id", noteController.Update)
			notes.DELETE("/:id", noteController.Delete)
		}
	}

	// Обработчик для PUT запросов без ID в URL
	r.NoRoute(func(c *gin.Context) {
		if c.Request.Method == "PUT" {
			path := c.Request.URL.Path
			// Проверяем, что это PUT запрос на ресурс без ID
			if path == "/api/v1.0/authors" || path == "/api/v1.0/issues" ||
				path == "/api/v1.0/markers" || path == "/api/v1.0/notes" {
				c.JSON(400, gin.H{
					"errorMessage": "id is required in URL path",
					"errorCode":    "40001",
				})
				return
			}
		}
		c.JSON(404, gin.H{
			"errorMessage": "Not found",
			"errorCode":    "40400",
		})
	})

	// Запуск сервера
	log.Println("Server starting on localhost:24110")
	if err := r.Run(":24110"); err != nil {
		log.Fatal("Failed to start server:", err)
	}
}

