package main

import (
	"news-board/publisher/internal/app"
)

// @title News Board API
// @version 1.0
// @description REST API для управления пользователями, новостями, маркерами и уведомлениями.
// @host localhost:24110
// @BasePath /api/v1.0
// @schemes http
func main() {
	app.Run()
}
