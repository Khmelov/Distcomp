package main

import (
	"lab-rest/internal/handlers"
	"lab-rest/internal/models"
	"strconv"
	"strings"

	"github.com/gin-gonic/gin"
	"gorm.io/driver/postgres"
	"gorm.io/gorm"
)

func main() {
	dsn := "host=localhost user=postgres password=postgres dbname=distcomp port=5432 sslmode=disable"
	db, err := gorm.Open(postgres.Open(dsn), &gorm.Config{})
	if err != nil {
		panic(err)
	}

	db.AutoMigrate(&models.Editor{}, &models.Topic{}, &models.Marker{}, &models.Note{})

	r := gin.Default()
	r.NoRoute(func(c *gin.Context) { handlers.SendErr(c, 404, 01, "Route not found") })
	v1 := r.Group("/api/v1.0")

	handleDBErr := func(c *gin.Context, err error) bool {
		if err == nil {
			return false
		}
		s := err.Error()
		if strings.Contains(s, "23505") || strings.Contains(s, "duplicate") {
			handlers.SendErr(c, 403, 01, "Duplicate key error")
			return true
		}
		if strings.Contains(s, "23503") || strings.Contains(s, "foreign key") {
			handlers.SendErr(c, 400, 02, "Foreign key association failed")
			return true
		}
		handlers.SendErr(c, 400, 01, "Bad Request")
		return true
	}

	v1.GET("/editors", func(c *gin.Context) {
		var list []models.Editor
		db.Find(&list)
		res := []models.EditorResponseTo{}
		for _, v := range list {
			res = append(res, models.EditorResponseTo{ID: v.ID, Login: v.Login, FirstName: v.FirstName, LastName: v.LastName})
		}
		c.JSON(200, res)
	})
	v1.POST("/editors", func(c *gin.Context) {
		var req models.EditorRequestTo
		if err := c.ShouldBindJSON(&req); err != nil {
			handlers.SendErr(c, 400, 01, "Validation failed")
			return
		}
		e := models.Editor{Login: req.Login, Password: req.Password, FirstName: req.FirstName, LastName: req.LastName}
		if err := db.Create(&e).Error; handleDBErr(c, err) {
			return
		}
		c.JSON(201, models.EditorResponseTo{ID: e.ID, Login: e.Login, FirstName: e.FirstName, LastName: e.LastName})
	})
	editorUpdate := func(c *gin.Context) {
		var req struct {
			ID int64 `json:"id"`
			models.EditorRequestTo
		}
		if err := c.ShouldBindJSON(&req); err != nil {
			handlers.SendErr(c, 400, 01, "Err")
			return
		}
		id := req.ID
		if id == 0 {
			id, _ = strconv.ParseInt(c.Param("id"), 10, 64)
		}
		var e models.Editor
		if err := db.First(&e, id).Error; err != nil {
			handlers.SendErr(c, 404, 01, "Err")
			return
		}
		e.Login, e.FirstName, e.LastName, e.Password = req.Login, req.FirstName, req.LastName, req.Password
		if err := db.Save(&e).Error; handleDBErr(c, err) {
			return
		}
		c.JSON(200, models.EditorResponseTo{ID: e.ID, Login: e.Login, FirstName: e.FirstName, LastName: e.LastName})
	}
	v1.PUT("/editors", editorUpdate)
	v1.PUT("/editors/:id", editorUpdate)
	v1.GET("/editors/:id", func(c *gin.Context) {
		id, _ := strconv.ParseInt(c.Param("id"), 10, 64)
		var v models.Editor
		if err := db.First(&v, id).Error; err != nil {
			handlers.SendErr(c, 404, 01, "Not found")
			return
		}
		c.JSON(200, models.EditorResponseTo{ID: v.ID, Login: v.Login, FirstName: v.FirstName, LastName: v.LastName})
	})
	v1.DELETE("/editors/:id", func(c *gin.Context) {
		id, _ := strconv.ParseInt(c.Param("id"), 10, 64)
		if db.Delete(&models.Editor{}, id).RowsAffected == 0 {
			handlers.SendErr(c, 404, 01, "Err")
		} else {
			c.Status(204)
		}
	})

	v1.GET("/topics", func(c *gin.Context) {
		var list []models.Topic
		db.Find(&list)
		res := []models.TopicResponseTo{}
		for _, v := range list {
			res = append(res, models.TopicResponseTo{ID: v.ID, EditorID: v.EditorID, Title: v.Title, Content: v.Content})
		}
		c.JSON(200, res)
	})
	v1.POST("/topics", func(c *gin.Context) {
		var req models.TopicRequestTo
		if err := c.ShouldBindJSON(&req); err != nil {
			handlers.SendErr(c, 400, 01, "Err")
			return
		}
		if err := db.First(&models.Editor{}, req.EditorID).Error; err != nil {
			handlers.SendErr(c, 400, 02, "FK Error")
			return
		}

		topic := models.Topic{EditorID: req.EditorID, Title: req.Title, Content: req.Content}
		for _, mName := range req.Markers {
			var m models.Marker
			db.FirstOrCreate(&m, models.Marker{Name: mName})
			topic.Markers = append(topic.Markers, m)
		}
		if err := db.Create(&topic).Error; handleDBErr(c, err) {
			return
		}
		c.JSON(201, models.TopicResponseTo{ID: topic.ID, EditorID: topic.EditorID, Title: topic.Title, Content: topic.Content})
	})
	topicUpdate := func(c *gin.Context) {
		var req struct {
			ID int64 `json:"id"`
			models.TopicRequestTo
		}
		if err := c.ShouldBindJSON(&req); err != nil {
			handlers.SendErr(c, 400, 01, "Err")
			return
		}
		id := req.ID
		if id == 0 {
			id, _ = strconv.ParseInt(c.Param("id"), 10, 64)
		}
		var e models.Topic
		if err := db.First(&e, id).Error; err != nil {
			handlers.SendErr(c, 404, 01, "Err")
			return
		}
		e.Title, e.Content, e.EditorID = req.Title, req.Content, req.EditorID
		if err := db.Save(&e).Error; handleDBErr(c, err) {
			return
		}
		c.JSON(200, models.TopicResponseTo{ID: e.ID, EditorID: e.EditorID, Title: e.Title, Content: e.Content})
	}
	v1.PUT("/topics", topicUpdate)
	v1.PUT("/topics/:id", topicUpdate)
	v1.GET("/topics/:id", func(c *gin.Context) {
		id, _ := strconv.ParseInt(c.Param("id"), 10, 64)
		var v models.Topic
		if err := db.First(&v, id).Error; err != nil {
			handlers.SendErr(c, 404, 01, "Not found")
			return
		}
		c.JSON(200, models.TopicResponseTo{ID: v.ID, EditorID: v.EditorID, Title: v.Title, Content: v.Content})
	})
	v1.DELETE("/topics/:id", func(c *gin.Context) {
		id, _ := strconv.ParseInt(c.Param("id"), 10, 64)
		db.Exec("DELETE FROM tbl_topic_marker WHERE topic_id = ?", id)
		if db.Delete(&models.Topic{}, id).RowsAffected == 0 {
			handlers.SendErr(c, 404, 01, "Err")
		} else {
			db.Exec("DELETE FROM tbl_marker WHERE id NOT IN (SELECT marker_id FROM tbl_topic_marker)")
			c.Status(204)
		}
	})

	v1.GET("/markers", func(c *gin.Context) {
		var list []models.Marker
		db.Find(&list)
		res := []models.MarkerResponseTo{}
		for _, v := range list {
			res = append(res, models.MarkerResponseTo{ID: v.ID, Name: v.Name})
		}
		c.JSON(200, res)
	})
	v1.POST("/markers", func(c *gin.Context) {
		var req models.MarkerRequestTo
		if err := c.ShouldBindJSON(&req); err != nil {
			handlers.SendErr(c, 400, 01, "Err")
			return
		}
		m := models.Marker{Name: req.Name}
		if err := db.Create(&m).Error; handleDBErr(c, err) {
			return
		}
		c.JSON(201, models.MarkerResponseTo{ID: m.ID, Name: m.Name})
	})
	markerUpdate := func(c *gin.Context) {
		var req struct {
			ID int64 `json:"id"`
			models.MarkerRequestTo
		}
		if err := c.ShouldBindJSON(&req); err != nil {
			handlers.SendErr(c, 400, 01, "Err")
			return
		}
		id := req.ID
		if id == 0 {
			id, _ = strconv.ParseInt(c.Param("id"), 10, 64)
		}
		var e models.Marker
		if err := db.First(&e, id).Error; err != nil {
			handlers.SendErr(c, 404, 01, "Err")
			return
		}
		e.Name = req.Name
		if err := db.Save(&e).Error; handleDBErr(c, err) {
			return
		}
		c.JSON(200, models.MarkerResponseTo{ID: e.ID, Name: e.Name})
	}
	v1.PUT("/markers", markerUpdate)
	v1.PUT("/markers/:id", markerUpdate)
	v1.GET("/markers/:id", func(c *gin.Context) {
		id, _ := strconv.ParseInt(c.Param("id"), 10, 64)
		var v models.Marker
		if err := db.First(&v, id).Error; err != nil {
			handlers.SendErr(c, 404, 01, "Not found")
			return
		}
		c.JSON(200, models.MarkerResponseTo{ID: v.ID, Name: v.Name})
	})
	v1.DELETE("/markers/:id", func(c *gin.Context) {
		id, _ := strconv.ParseInt(c.Param("id"), 10, 64)
		if db.Delete(&models.Marker{}, id).RowsAffected == 0 {
			handlers.SendErr(c, 404, 01, "Err")
		} else {
			c.Status(204)
		}
	})

	v1.GET("/notes", func(c *gin.Context) {
		var list []models.Note
		db.Find(&list)
		res := []models.NoteResponseTo{}
		for _, v := range list {
			res = append(res, models.NoteResponseTo{ID: v.ID, TopicID: v.TopicID, Content: v.Content})
		}
		c.JSON(200, res)
	})
	v1.POST("/notes", func(c *gin.Context) {
		var req models.NoteRequestTo
		if err := c.ShouldBindJSON(&req); err != nil {
			handlers.SendErr(c, 400, 01, "Err")
			return
		}
		if err := db.First(&models.Topic{}, req.TopicID).Error; err != nil {
			handlers.SendErr(c, 400, 02, "FK Error")
			return
		}

		n := models.Note{TopicID: req.TopicID, Content: req.Content}
		if err := db.Create(&n).Error; handleDBErr(c, err) {
			return
		}
		c.JSON(201, models.NoteResponseTo{ID: n.ID, TopicID: n.TopicID, Content: n.Content})
	})
	noteUpdate := func(c *gin.Context) {
		var req struct {
			ID int64 `json:"id"`
			models.NoteRequestTo
		}
		if err := c.ShouldBindJSON(&req); err != nil {
			handlers.SendErr(c, 400, 01, "Err")
			return
		}
		id := req.ID
		if id == 0 {
			id, _ = strconv.ParseInt(c.Param("id"), 10, 64)
		}
		var e models.Note
		if err := db.First(&e, id).Error; err != nil {
			handlers.SendErr(c, 404, 01, "Err")
			return
		}
		e.TopicID, e.Content = req.TopicID, req.Content
		if err := db.Save(&e).Error; handleDBErr(c, err) {
			return
		}
		c.JSON(200, models.NoteResponseTo{ID: e.ID, TopicID: e.TopicID, Content: e.Content})
	}
	v1.PUT("/notes", noteUpdate)
	v1.PUT("/notes/:id", noteUpdate)
	v1.GET("/notes/:id", func(c *gin.Context) {
		id, _ := strconv.ParseInt(c.Param("id"), 10, 64)
		var v models.Note
		if err := db.First(&v, id).Error; err != nil {
			handlers.SendErr(c, 404, 01, "Not found")
			return
		}
		c.JSON(200, models.NoteResponseTo{ID: v.ID, TopicID: v.TopicID, Content: v.Content})
	})
	v1.DELETE("/notes/:id", func(c *gin.Context) {
		id, _ := strconv.ParseInt(c.Param("id"), 10, 64)
		if db.Delete(&models.Note{}, id).RowsAffected == 0 {
			handlers.SendErr(c, 404, 01, "Err")
		} else {
			c.Status(204)
		}
	})

	r.Run(":24110")
}
