package resolver

import (
	"errors"
	"fmt"
	"github.com/gofiber/fiber/v2"
	"gridusko_rest/internal/model"
	author2 "gridusko_rest/internal/services/author"
)

func (h *Handler) CreateAuthorHandler(c *fiber.Ctx) error {
	var author model.Author
	if err := c.BodyParser(&author); err != nil {
		fmt.Println(err)
		return c.Status(fiber.StatusBadRequest).JSON(fiber.Map{"error": "invalid request body"})
	}

	fmt.Println(author)

	if err := h.authorService.CreateAuthor(&author); err != nil {
		if errors.Is(err, author2.ErrInvalidBadRequest) {
			fmt.Println(err)
			return c.Status(fiber.StatusBadRequest).JSON(fiber.Map{"error": err.Error()})
		}

		if errors.Is(err, author2.ErrAlreadyExists) {
			fmt.Println(err)
			return c.Status(fiber.StatusForbidden).JSON(fiber.Map{"error": err.Error()})
		}

		fmt.Println(err)
		return c.Status(fiber.StatusInternalServerError).JSON(fiber.Map{"error": err.Error()})
	}

	fmt.Println(author.ID)
	return c.Status(fiber.StatusCreated).JSON(author)
}

func (h *Handler) GetAuthorByIDHandler(c *fiber.Ctx) error {
	id, err := c.ParamsInt("id", 0)
	if err != nil {
		fmt.Println(err)
		return c.Status(fiber.StatusBadRequest).JSON(fiber.Map{"error": "invalid id"})
	}

	if id == 0 {
		fmt.Println(err)
		return c.Status(fiber.StatusBadRequest).JSON(fiber.Map{"error": "id is required"})
	}
	author, err := h.authorService.GetAuthorByID(int64(id))
	if err != nil {
		fmt.Println(err)
		return c.Status(fiber.StatusNotFound).JSON(fiber.Map{"error": "author not found"})
	}
	return c.JSON(author)
}

func (h *Handler) GetAuthorsHandler(c *fiber.Ctx) error {
	authors, err := h.authorService.GetAuthors()
	if err != nil {
		return c.Status(fiber.StatusInternalServerError).JSON(fiber.Map{"error": err.Error()})
	}
	return c.JSON(authors)
}

func (h *Handler) UpdateAuthorHandler(c *fiber.Ctx) error {
	var author model.Author
	if err := c.BodyParser(&author); err != nil {
		return c.Status(fiber.StatusBadRequest).JSON(fiber.Map{"error": "invalid request body"})
	}

	if err := h.authorService.UpdateAuthor(&author); err != nil {
		fmt.Println(err)
		if errors.Is(err, author2.ErrInvalidBadRequest) {
			return c.Status(fiber.StatusBadRequest).JSON(fiber.Map{"error": err.Error()})
		}

		if err.Error() == "author not found" || err.Error() == "login must be at least 2 characters long" {
			return c.Status(fiber.StatusNotFound).JSON(fiber.Map{"error": err.Error()})
		}
		return c.Status(fiber.StatusInternalServerError).JSON(fiber.Map{"error": err.Error()})
	}

	return c.JSON(author)
}

func (h *Handler) DeleteAuthorHandler(c *fiber.Ctx) error {
	id, _ := c.ParamsInt("id", 0)
	if id == 0 {
		return c.Status(fiber.StatusBadRequest).JSON(fiber.Map{"error": "id is required"})
	}
	if err := h.authorService.DeleteAuthor(int64(id)); err != nil {
		if err.Error() == "author not found" {
			return c.Status(fiber.StatusNotFound).JSON(fiber.Map{"error": err.Error()})
		}

		return c.Status(fiber.StatusInternalServerError).JSON(fiber.Map{"error": err.Error()})
	}
	return c.Status(fiber.StatusNoContent).JSON(fiber.Map{"message": "author deleted"})
}
