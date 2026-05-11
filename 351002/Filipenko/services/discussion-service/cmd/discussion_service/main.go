package main

import (
	"discussion-service/internal/bootstrap"
)

func main() {
	app := bootstrap.NewApp()
	app.Run()
}
