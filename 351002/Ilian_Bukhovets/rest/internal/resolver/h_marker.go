package resolver

import (
	"errors"
	"github.com/gofiber/fiber/v2"
	"gridusko_rest/internal/model"
	marker2 "gridusko_rest/internal/services/marker"
)

func (h *Handler) CreateMarkerHandler(c *fiber.Ctx) error {
	var marker model.Marker
	if err := c.BodyParser(&marker); err != nil {
		return c.Status(fiber.StatusBadRequest).JSON(fiber.Map{"error": "invalid request body"})
	}

	if err := h.markerService.CreateMarker(&marker); err != nil {
		if errors.Is(err, marker2.ErrInvalidBody) {
			return c.Status(fiber.StatusBadRequest).JSON(fiber.Map{"error": err.Error()})
		}

		return c.Status(fiber.StatusInternalServerError).JSON(fiber.Map{"error": err.Error()})
	}

	return c.Status(fiber.StatusCreated).JSON(marker)
}

func (h *Handler) GetMarkerByIDHandler(c *fiber.Ctx) error {
	id, _ := c.ParamsInt("id")
	if id == 0 {
		return c.Status(fiber.StatusBadRequest).JSON(fiber.Map{"error": "id is required"})
	}

	marker, err := h.markerService.GetMarkerByID(int64(id))
	if err != nil {
		return c.Status(fiber.StatusNotFound).JSON(fiber.Map{"error": "marker not found"})
	}
	return c.JSON(marker)
}

func (h *Handler) GetMarkersHandler(c *fiber.Ctx) error {
	markers, err := h.markerService.GetMarkers()
	if err != nil {
		return c.Status(fiber.StatusInternalServerError).JSON(fiber.Map{"error": err.Error()})
	}

	if len(markers) == 0 {
		return c.Status(fiber.StatusOK).JSON(fiber.Map{"error": "markers not found"})
	}

	return c.JSON(markers)
}

func (h *Handler) UpdateMarkerHandler(c *fiber.Ctx) error {
	var marker model.Marker
	if err := c.BodyParser(&marker); err != nil {
		return c.Status(fiber.StatusBadRequest).JSON(fiber.Map{"error": "invalid request body"})
	}

	if err := h.markerService.UpdateMarker(&marker); err != nil {
		if err.Error() == "marker not found" {
			return c.Status(fiber.StatusNotFound).JSON(fiber.Map{"error": err.Error()})
		}

		if errors.Is(err, marker2.ErrInvalidBody) {
			return c.Status(fiber.StatusBadRequest).JSON(fiber.Map{"error": err.Error()})
		}

		return c.Status(fiber.StatusInternalServerError).JSON(fiber.Map{"error": err.Error()})
	}
	return c.JSON(marker)
}

func (h *Handler) DeleteMarkerHandler(c *fiber.Ctx) error {
	id, _ := c.ParamsInt("id")
	if id == 0 {
		return c.Status(fiber.StatusBadRequest).JSON(fiber.Map{"error": "id is required"})
	}

	if err := h.markerService.DeleteMarker(int64(id)); err != nil {
		if err.Error() == "marker not found" {
			return c.Status(fiber.StatusNotFound).JSON(fiber.Map{"error": err.Error()})
		}
		return c.Status(fiber.StatusInternalServerError).JSON(fiber.Map{"error": err.Error()})
	}
	return c.Status(fiber.StatusNoContent).JSON(fiber.Map{"message": "marker deleted"})
}
