package handler

import (
	"discussion-service/internal/entity"
	"discussion-service/internal/usecase"
	"github.com/gofiber/fiber/v2"
	"strconv"
)

type CommentHandler struct {
	uc *usecase.CommentUsecase
}

func NewCommentHandler(uc *usecase.CommentUsecase) *CommentHandler {
	return &CommentHandler{uc: uc}
}

func (h *CommentHandler) Create(c *fiber.Ctx) error {
	var req entity.CommentRequestTo
	if err := c.BodyParser(&req); err != nil {
		return entity.NewErr(400, "00", "invalid json")
	}
	resp, err := h.uc.Create(c.Context(), req)
	if err != nil {
		return err
	}
	return c.Status(fiber.StatusCreated).JSON(resp)
}

func (h *CommentHandler) GetAll(c *fiber.Ctx) error {
	resp, err := h.uc.GetAll(c.Context())
	if err != nil {
		return err
	}
	return c.JSON(resp)
}

func (h *CommentHandler) GetByTopicID(c *fiber.Ctx) error {
	id, err := c.ParamsInt("topicId")
	if err != nil {
		return entity.NewErr(400, "00", "invalid topicId")
	}
	resp, err := h.uc.GetByTopicID(c.Context(), int64(id))
	if err != nil {
		return err
	}
	return c.JSON(resp)
}

func (h *CommentHandler) GetByID(c *fiber.Ctx) error {
	id, _ := strconv.ParseInt(c.Params("id"), 10, 64)
	resp, err := h.uc.GetByID(c.Context(), id)
	if err != nil {
		return err
	}
	return c.JSON(resp)
}

func (h *CommentHandler) Update(c *fiber.Ctx) error {
	var req entity.CommentRequestTo
	c.BodyParser(&req)
	id, _ := strconv.ParseInt(c.Params("id"), 10, 64)
	resp, err := h.uc.Update(c.Context(), id, req)
	if err != nil {
		return err
	}
	return c.JSON(resp)
}

func (h *CommentHandler) Delete(c *fiber.Ctx) error {
	id, err := strconv.ParseInt(c.Params("id"), 10, 64)
	if err != nil {
		return entity.NewErr(400, "00", "invalid comment id")
	}
	if err := h.uc.Delete(c.Context(), id); err != nil {
		return err
	}
	return c.SendStatus(fiber.StatusNoContent)
}
