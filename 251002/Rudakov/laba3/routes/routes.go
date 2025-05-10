package routes

import (
	controllers "laba3/controllers"

	"github.com/gofiber/fiber/v2"
)

func Handlers(app *fiber.App) {
	app.Get("/api/v1.0/messages/:id", controllers.GetMessageByID)
	app.Put("/api/v1.0/messages/:id", controllers.PutMessageByID)
	app.Get("/api/v1.0/messages", controllers.GetMessage)

}
