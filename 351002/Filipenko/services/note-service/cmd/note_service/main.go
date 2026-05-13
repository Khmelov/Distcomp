package main

import (
	"note-service/internal/bootstrap"
)

func main() {
	app := bootstrap.NewApp()
	app.Run()
}
