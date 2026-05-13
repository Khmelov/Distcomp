package resolver

import (
	"errors"
	"fmt"
	"github.com/gofiber/fiber/v2"
	"gridusko_rest/internal/model"
	"gridusko_rest/internal/services/issue"
	"time"
)

func (h *Handler) CreateIssueHandler(c *fiber.Ctx) error {
	var is model.Issue
	if err := c.BodyParser(&is); err != nil {
		return c.Status(fiber.StatusBadRequest).JSON(is)
	}

	fmt.Println("Issue: ", is)

	if is.Created.IsZero() || is.Modified.IsZero() {
		now := time.Now()
		if is.Created.IsZero() {
			is.Created = now
		}
		if is.Modified.IsZero() {
			is.Modified = now
		}
	}

	if err := h.issueService.CreateIssue(&is); err != nil {
		if errors.Is(err, issue.ErrInvalidBody) {
			return c.Status(fiber.StatusBadRequest).JSON(fiber.Map{"error": err.Error()})
		}

		if errors.Is(err, issue.ErrAlreadyExists) {
			return c.Status(fiber.StatusForbidden).JSON(fiber.Map{"error": err.Error()})
		}

		if errors.Is(err, issue.ErrAuthorNotFound) {
			return c.Status(fiber.StatusForbidden).JSON(fiber.Map{"error": err.Error()})
		}

		return c.Status(fiber.StatusInternalServerError).JSON(fiber.Map{"error": err.Error()})
	}

	for _, m := range is.Markers {
		err := h.markerService.CreateMarker(&model.Marker{
			ID:   0,
			Name: m,
		})
		if err != nil {
			return c.Status(fiber.StatusInternalServerError).JSON(fiber.Map{"error": err.Error()})
		}
	}

	return c.Status(fiber.StatusCreated).JSON(is)
}

func (h *Handler) GetIssueByIDHandler(c *fiber.Ctx) error {
	id, _ := c.ParamsInt("id")
	if id == 0 {
		return c.Status(fiber.StatusBadRequest).JSON(fiber.Map{"error": "id is required"})
	}

	issue, err := h.issueService.GetIssueByID(int64(id))
	if err != nil {
		return c.Status(fiber.StatusNotFound).JSON(fiber.Map{"error": "issue not found"})
	}
	return c.JSON(issue)
}

func (h *Handler) GetIssuesHandler(c *fiber.Ctx) error {
	issues, err := h.issueService.GetIssues()
	if err != nil {
		return c.Status(fiber.StatusInternalServerError).JSON(fiber.Map{"error": err.Error()})
	}

	if len(issues) == 0 {
		return c.Status(fiber.StatusOK).JSON(fiber.Map{"error": "issues not found"})
	}

	return c.JSON(issues)
}

func (h *Handler) UpdateIssueHandler(c *fiber.Ctx) error {
	var is model.Issue
	if err := c.BodyParser(&is); err != nil {
		return c.Status(fiber.StatusBadRequest).JSON(fiber.Map{"error": "invalid request body"})
	}

	if err := h.issueService.UpdateIssue(&is); err != nil {
		if err.Error() == "is not found" {
			return c.Status(fiber.StatusNotFound).JSON(fiber.Map{"error": err.Error()})
		}
		if errors.Is(err, issue.ErrInvalidBody) {
			return c.Status(fiber.StatusBadRequest).JSON(fiber.Map{"error": err.Error()})
		}
		return c.Status(fiber.StatusInternalServerError).JSON(fiber.Map{"error": err.Error()})
	}
	return c.JSON(is)
}

func (h *Handler) DeleteIssueHandler(c *fiber.Ctx) error {
	id, _ := c.ParamsInt("id")
	if id == 0 {
		return c.Status(fiber.StatusBadRequest).JSON(fiber.Map{"error": "id is required"})
	}

	if err := h.issueService.DeleteIssue(int64(id)); err != nil {
		if err.Error() == "issue not found" {
			return c.Status(fiber.StatusNotFound).JSON(fiber.Map{"error": err.Error()})
		}
		return c.Status(fiber.StatusInternalServerError).JSON(fiber.Map{"error": err.Error()})
	}
	return c.Status(fiber.StatusNoContent).JSON(fiber.Map{"message": "issue deleted"})
}
