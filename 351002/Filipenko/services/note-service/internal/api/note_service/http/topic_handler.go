package http

import (
	"github.com/gofiber/fiber/v2"
	"note-service/internal/entity"
	"note-service/internal/usecase"
)

type TopicHandler struct {
	uc *usecase.TopicUsecase
}

func NewTopicHandler(uc *usecase.TopicUsecase) *TopicHandler {
	return &TopicHandler{uc: uc}
}

func (h *TopicHandler) Create(c *fiber.Ctx) error {
	var req entity.TopicRequestTo
	if err := c.BodyParser(&req); err != nil {
		return entity.NewErr(400, "00", "invalid json")
	}
	resp, err := h.uc.Create(c.Context(), req)
	if err != nil {
		return err
	}
	return c.Status(fiber.StatusCreated).JSON(resp)
}

func (h *TopicHandler) GetByID(c *fiber.Ctx) error {
	id, err := c.ParamsInt("id")
	if err != nil {
		return entity.NewErr(400, "00", "invalid id")
	}
	resp, err := h.uc.GetByID(c.Context(), int64(id))
	if err != nil {
		return err
	}
	return c.JSON(resp)
}

func (h *TopicHandler) GetAll(c *fiber.Ctx) error {
	limit := c.QueryInt("limit", 20)
	offset := c.QueryInt("offset", 0)
	resp, err := h.uc.GetAll(c.Context(), limit, offset)
	if err != nil {
		return err
	}
	return c.JSON(resp)
}

func (h *TopicHandler) Update(c *fiber.Ctx) error {
	id, _ := c.ParamsInt("id")
	if id == 0 {
		var bodyWithId struct {
			ID int64 `json:"id"`
		}
		if err := c.BodyParser(&bodyWithId); err == nil && bodyWithId.ID != 0 {
			id = int(bodyWithId.ID)
		}
	}
	if id == 0 {
		return entity.NewErr(400, "00", "id is missing from URL or request body")
	}

	var req entity.TopicRequestTo
	if err := c.BodyParser(&req); err != nil {
		return entity.NewErr(400, "00", "invalid json body")
	}

	resp, err := h.uc.Update(c.Context(), int64(id), req)
	if err != nil {
		return err
	}
	return c.Status(200).JSON(resp)
}

func (h *TopicHandler) Delete(c *fiber.Ctx) error {
	id, err := c.ParamsInt("id")
	if err != nil {
		return entity.NewErr(400, "00", "invalid id")
	}
	if err := h.uc.Delete(c.Context(), int64(id)); err != nil {
		return err
	}
	return c.SendStatus(fiber.StatusNoContent)
}
