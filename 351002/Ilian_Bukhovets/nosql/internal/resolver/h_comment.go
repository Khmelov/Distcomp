package resolver

import (
	"errors"
	"fmt"
	"github.com/gofiber/fiber/v2"
	"gridusko_rest/internal/model"
	comment2 "gridusko_rest/internal/services/comment"
)

func (h *Handler) CreateCommentHandler(c *fiber.Ctx) error {
	var comment model.Comment
	if err := c.BodyParser(&comment); err != nil {
		return c.Status(fiber.StatusBadRequest).JSON(fiber.Map{"error": "invalid request body"})
	}

	if err := h.commentService.CreateComment(&comment); err != nil {
		if errors.Is(err, comment2.ErrInvalidBody) {
			return c.Status(fiber.StatusBadRequest).JSON(fiber.Map{"error": err.Error()})
		}

		if errors.Is(err, comment2.ErrIssueNotFound) {
			return c.Status(fiber.StatusForbidden).JSON(fiber.Map{"error": err.Error()})
		}

		return c.Status(fiber.StatusInternalServerError).JSON(fiber.Map{"error": err.Error()})
	}

	fmt.Println("Reeived create request")

	return c.Status(fiber.StatusCreated).JSON(comment)
}

func (h *Handler) UpdateCommentHandler(c *fiber.Ctx) error {
	id, _ := c.ParamsInt("id")
	if id == 0 {
		return c.Status(fiber.StatusBadRequest).JSON(fiber.Map{"error": "id is required"})
	}

	var comment model.Comment
	if err := c.BodyParser(&comment); err != nil {
		return c.Status(fiber.StatusBadRequest).JSON(fiber.Map{"error": "invalid request body"})
	}

	comment.ID = int64(id)

	fmt.Println("Update comment: ", comment)

	if err := h.commentService.UpdateComment(&comment); err != nil {
		return c.Status(fiber.StatusInternalServerError).JSON(fiber.Map{"error": err.Error()})
	}

	fmt.Println("Reeived update request")

	return c.JSON(comment)
}

func (h *Handler) DeleteCommentHandler(c *fiber.Ctx) error {
	id, _ := c.ParamsInt("id")
	if id == 0 {
		return c.Status(fiber.StatusBadRequest).JSON(fiber.Map{"error": "id is required"})
	}

	if err := h.commentService.DeleteComment(int64(id)); err != nil {
		if err.Error() == "comment not found" {
			return c.Status(fiber.StatusNotFound).JSON(fiber.Map{"error": err.Error()})
		}
		return c.Status(fiber.StatusInternalServerError).JSON(fiber.Map{"error": err.Error()})
	}
	return c.Status(fiber.StatusNoContent).JSON(fiber.Map{"message": "comment deleted"})
}

func (h *Handler) GetCommentByIDHandler(c *fiber.Ctx) error {
	id, _ := c.ParamsInt("id")
	if id == 0 {
		return c.Status(fiber.StatusBadRequest).JSON(fiber.Map{"error": "id is required"})
	}

	comment, err := h.commentService.GetCommentByID(int64(id))
	if err != nil {
		return c.Status(fiber.StatusNotFound).JSON(fiber.Map{"error": "comment not found"})
	}

	fmt.Println("Reeived get by id request")

	return c.JSON(comment)
}

func (h *Handler) GetCommentsHandler(c *fiber.Ctx) error {
	comments, err := h.commentService.GetComments()
	if err != nil {
		return c.Status(fiber.StatusInternalServerError).JSON(fiber.Map{"error": err.Error()})
	}

	if len(comments) == 0 {
		return c.Status(fiber.StatusOK).JSON(fiber.Map{"error": "comments not found"})
	}

	return c.Status(fiber.StatusOK).JSON(comments)
}
