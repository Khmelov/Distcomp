package main

import (
	db "laba3/config"
	"laba3/redis"
	routes "laba3/routes"

	"github.com/gofiber/fiber/v2"
)

func main() {
	app := fiber.New(
		fiber.Config{
			AppName: "Laba3",
		})
	db.Connect()
	redis.InitRedis()
	defer db.Session.Close()
	routes.Handlers(app)
	app.Listen(":24130")
}
