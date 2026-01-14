package routes

import (
	controllers "laba3/controllers"

	"github.com/gofiber/fiber/v2"
)

func Handlers(app *fiber.App) {
	app.Get("/api/v1.0/comments/:id", controllers.GetCommentByID)
	app.Put("/api/v1.0/comments/:id", controllers.PutCommentByID)
	app.Get("/api/v1.0/comments", controllers.GetComment)

}
