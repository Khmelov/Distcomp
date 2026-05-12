package main

import (
	"discussion/internal/domain"
	"discussion/internal/repository"
	"math/rand"
	"net/http"
	"strconv"
	"time"

	"github.com/gin-gonic/gin"
)

func main() {
	repo := repository.NewNoteRepository()
	r := gin.Default()

	v1 := r.Group("/api/v1.0")
	{
		v1.POST("/notes", func(c *gin.Context) {
			var n domain.Note
			if err := c.ShouldBindJSON(&n); err != nil {
				c.Status(400)
				return
			}
			if n.ID == 0 {
				rand.Seed(time.Now().UnixNano())
				n.ID = rand.Intn(100000)
			}
			if err := repo.Create(n); err != nil {
				c.Status(500)
				return
			}
			c.JSON(http.StatusCreated, n)
		})

		v1.GET("/notes", func(c *gin.Context) {
			notes, err := repo.GetAll()
			if err != nil || notes == nil {
				c.JSON(200, []domain.Note{})
				return
			}
			c.JSON(200, notes)
		})

		v1.GET("/notes/:id", func(c *gin.Context) {
			id, _ := strconv.Atoi(c.Param("id"))
			n, err := repo.GetByID(id)
			if err != nil {
				c.Status(404)
				return
			}
			c.JSON(200, n)
		})

		v1.PUT("/notes", func(c *gin.Context) {
			var n domain.Note
			if err := c.ShouldBindJSON(&n); err != nil {
				c.Status(400)
				return
			}
			repo.Update(n)
			c.JSON(200, n)
		})

		v1.PUT("/notes/:id", func(c *gin.Context) {
			id, _ := strconv.Atoi(c.Param("id"))
			var n domain.Note
			if err := c.ShouldBindJSON(&n); err != nil {
				c.Status(400)
				return
			}
			n.ID = id
			repo.Update(n)
			c.JSON(200, n)
		})

		v1.DELETE("/notes/:id", func(c *gin.Context) {
			id, _ := strconv.Atoi(c.Param("id"))
			repo.Delete(id)
			c.Status(204)
		})
	}
	r.Run(":24130")
}
