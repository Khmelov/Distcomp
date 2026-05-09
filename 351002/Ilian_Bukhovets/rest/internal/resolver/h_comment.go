package resolver

import (
	"bytes"
	"encoding/json"
	"fmt"
	"github.com/gofiber/fiber/v2"
	"gridusko_rest/internal/model"
	"io"
	"net/http"
)

func (h *Handler) CreateCommentHandler(c *fiber.Ctx) error {
	var comment model.Comment
	if err := c.BodyParser(&comment); err != nil {
		return c.Status(fiber.StatusBadRequest).JSON(fiber.Map{"error": "invalid request body"})
	}

	targetURL := fmt.Sprintf("http://0.0.0.0:24130/api/v1.0/comments")

	reqBody, err := json.Marshal(comment)
	if err != nil {
		return c.Status(fiber.StatusInternalServerError).JSON(fiber.Map{"error": "failed to marshal request body"})
	}

	req, err := http.NewRequest("POST", targetURL, bytes.NewBuffer(reqBody))
	if err != nil {
		return c.Status(fiber.StatusInternalServerError).JSON(fiber.Map{"error": "failed to create request"})
	}

	req.Header.Set("Content-Type", "application/json")
	for key, value := range c.GetReqHeaders() {
		req.Header.Set(key, value[0])
	}

	client := &http.Client{}
	resp, err := client.Do(req)
	if err != nil {
		return c.Status(fiber.StatusBadGateway).JSON(fiber.Map{"error": "failed to fetch data from the target URL"})
	}
	defer resp.Body.Close()

	body, err := io.ReadAll(resp.Body)
	if err != nil {
		return c.Status(fiber.StatusInternalServerError).JSON(fiber.Map{"error": "failed to read response body"})
	}

	c.Status(resp.StatusCode)
	return c.Send(body)
}

func (h *Handler) UpdateCommentHandler(c *fiber.Ctx) error {
	var comment model.Comment
	if err := c.BodyParser(&comment); err != nil {
		return c.Status(fiber.StatusBadRequest).JSON(fiber.Map{"error": "invalid request body"})
	}

	targetURL := fmt.Sprintf("http://0.0.0.0:24130/api/v1.0/comments/%d", comment.ID)

	reqBody, err := json.Marshal(comment)
	if err != nil {
		return c.Status(fiber.StatusInternalServerError).JSON(fiber.Map{"error": "failed to marshal request body"})
	}

	req, err := http.NewRequest("PUT", targetURL, bytes.NewBuffer(reqBody))
	if err != nil {
		return c.Status(fiber.StatusInternalServerError).JSON(fiber.Map{"error": "failed to create request"})
	}

	req.Header.Set("Content-Type", "application/json")
	for key, value := range c.GetReqHeaders() {
		req.Header.Set(key, value[0])
	}

	client := &http.Client{}
	resp, err := client.Do(req)
	if err != nil {
		return c.Status(fiber.StatusBadGateway).JSON(fiber.Map{"error": "failed to fetch data from the target URL"})
	}
	defer resp.Body.Close()

	body, err := io.ReadAll(resp.Body)
	if err != nil {
		return c.Status(fiber.StatusInternalServerError).JSON(fiber.Map{"error": "failed to read response body"})
	}

	c.Status(resp.StatusCode)
	return c.Send(body)
}

func (h *Handler) DeleteCommentHandler(c *fiber.Ctx) error {
	id, err := c.ParamsInt("id")
	if err != nil || id == 0 {
		return c.Status(fiber.StatusBadRequest).JSON(fiber.Map{"error": "id is required"})
	}

	targetURL := fmt.Sprintf("http://0.0.0.0:24130/api/v1.0/comments/%d", id)

	req, err := http.NewRequest("DELETE", targetURL, nil)
	if err != nil {
		return c.Status(fiber.StatusInternalServerError).JSON(fiber.Map{"error": "failed to create request"})
	}

	for key, value := range c.GetReqHeaders() {
		req.Header.Set(key, value[0])
	}

	client := &http.Client{}
	resp, err := client.Do(req)
	if err != nil {
		return c.Status(fiber.StatusBadGateway).JSON(fiber.Map{"error": "failed to fetch data from the target URL"})
	}
	defer resp.Body.Close()

	body, err := io.ReadAll(resp.Body)
	if err != nil {
		return c.Status(fiber.StatusInternalServerError).JSON(fiber.Map{"error": "failed to read response body"})
	}

	c.Status(resp.StatusCode)
	if resp.StatusCode == fiber.StatusNoContent {
		return c.JSON(fiber.Map{"message": "comment deleted"})
	}
	return c.Send(body)
}

func (h *Handler) GetCommentByIDHandler(c *fiber.Ctx) error {
	fmt.Println("______________________")
	id, err := c.ParamsInt("id")
	if err != nil || id == 0 {
		return c.Status(fiber.StatusBadRequest).JSON(fiber.Map{"error": "id is required"})
	}

	targetURL := fmt.Sprintf("http://0.0.0.0:24130/api/v1.0/comments/%d", id)

	req, err := http.NewRequest("GET", targetURL, nil)
	if err != nil {
		return c.Status(fiber.StatusInternalServerError).JSON(fiber.Map{"error": "failed to create request"})
	}

	for key, value := range c.GetReqHeaders() {
		req.Header.Set(key, value[0])
	}

	client := &http.Client{}
	resp, err := client.Do(req)
	if err != nil {
		return c.Status(fiber.StatusBadGateway).JSON(fiber.Map{"error": "failed to fetch data from the target URL"})
	}
	defer resp.Body.Close()

	body, err := io.ReadAll(resp.Body)
	if err != nil {
		return c.Status(fiber.StatusInternalServerError).JSON(fiber.Map{"error": "failed to read response body"})
	}

	c.Status(resp.StatusCode)
	if resp.StatusCode == fiber.StatusNotFound {
		return c.JSON(fiber.Map{"error": "comment not found"})
	}
	return c.Send(body)
}

func (h *Handler) GetCommentsHandler(c *fiber.Ctx) error {
	targetURL := "http://0.0.0.0:24130/api/v1.0/comments"

	req, err := http.NewRequest("GET", targetURL, nil)
	if err != nil {
		return c.Status(fiber.StatusInternalServerError).JSON(fiber.Map{
			"error": "Failed to create request",
		})
	}

	req.Header = make(http.Header)
	for key, value := range c.GetReqHeaders() {
		req.Header.Set(key, value[0])
	}

	client := &http.Client{}
	resp, err := client.Do(req)
	if err != nil {
		return c.Status(fiber.StatusBadGateway).JSON(fiber.Map{
			"error": "Failed to fetch data from the target URL",
		})
	}
	defer resp.Body.Close()

	body, err := io.ReadAll(resp.Body)
	if err != nil {
		return c.Status(fiber.StatusInternalServerError).JSON(fiber.Map{
			"error": "Failed to read response body",
		})
	}

	c.Status(resp.StatusCode)
	return c.Send(body)
}
