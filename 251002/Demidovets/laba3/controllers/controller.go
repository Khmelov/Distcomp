package controllers

import (
	"encoding/json"
	"fmt"
	"io"
	"laba3/kafka"
	"laba3/models"
	"laba3/redis"
	"net/http"
	"strconv"

	"github.com/gofiber/fiber/v2"
)

func GetCommentByID(c *fiber.Ctx) error {
	target_url := "http://127.0.0.1:24110" + c.OriginalURL()
	fmt.Printf("target_url: %v\n", target_url)

	idParam := c.Params("id")

	comment, err := redis.GetComment(idParam)
	if err == nil {
		return c.JSON(comment)
	}

	commentID, _ := strconv.Atoi(idParam)

	kafka.SendId(commentID, 0)

	entity, err := kafka.ConsumerComment()

	if err != nil {
		fmt.Println(err.Error())
	}

	fmt.Println(entity)

	if entity.ID == 0 {
		c.Status(fiber.StatusBadRequest)
		// return fiber.NewError(fiber.StatusBadRequest, "error")
	}

	return c.JSON(entity)
}

func GetComment(c *fiber.Ctx) error {
	target_url := "http://127.0.0.1:24110" + c.OriginalURL()

	resp, err := http.Get(target_url)
	if err != nil {
		return fiber.NewError(400, err.Error())
	}
	defer resp.Body.Close()

	body, err := io.ReadAll(resp.Body)
	if err != nil {
		return fiber.NewError(400, err.Error())
	}

	var data CommentData
	if err := json.Unmarshal(body, &data); err != nil {
		return fiber.NewError(400, err.Error())
	}

	return c.JSON(data)
}

func PutCommentByID(c *fiber.Ctx) error {
	var commentData models.CommentData

	// Получаем ID из параметров URL
	id := c.Params("id")

	// Читаем данные из тела запроса
	if err := c.BodyParser(&commentData); err != nil {
		return c.Status(fiber.StatusBadRequest).JSON(fiber.Map{
			"error": "Invalid request data",
		})
	}

	if err := redis.StoreComment(id, commentData); err != nil {
		return fiber.NewError(fiber.StatusInternalServerError, err.Error())
	}

	// Здесь вы можете обработать данные, например, сохранить их в базе данных
	fmt.Printf("Received comment: %+v, ID: %s", commentData, id)

	return c.JSON(commentData)
}
