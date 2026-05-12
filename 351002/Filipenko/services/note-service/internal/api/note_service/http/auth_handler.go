package http

import (
	"encoding/json"

	"github.com/gofiber/fiber/v2"

	"note-service/internal/entity"
	"note-service/internal/pkg/auth"
	"note-service/internal/usecase"
)
type AuthHandler struct {
	uc  *usecase.AuthorUsecase
	jwt *auth.JWTService
}

func NewAuthHandler(uc *usecase.AuthorUsecase, jwt *auth.JWTService) *AuthHandler {
	return &AuthHandler{uc: uc, jwt: jwt}
}

func (h *AuthHandler) Login(c *fiber.Ctx) error {
	body := c.Body()
	if len(body) == 0 {
		return entity.NewErr(400, "00", "invalid json")
	}
	var req entity.LoginRequestTo
	if err := json.Unmarshal(body, &req); err != nil {
		return entity.NewErr(400, "00", "invalid json")
	}
	a, err := h.uc.Login(c.UserContext(), req)
	if err != nil {
		return err
	}

	token, err := h.jwt.Mint(a.Login, a.Role)
	if err != nil {
		return entity.NewErr(500, "00", "failed to issue token")
	}

	return c.JSON(entity.LoginResponseTo{
		AccessToken: token,
		TypeToken:   "Bearer",
	})
}

func (h *AuthHandler) Register(c *fiber.Ctx) error {
	body := c.Body()
	if len(body) == 0 {
		return entity.NewErr(400, "00", "invalid json")
	}

	var req entity.AuthorRegisterRequestTo
	// encoding/json вызывает UnmarshalJSON (firstname vs firstName); Fiber BodyParser — не всегда.
	if err := json.Unmarshal(body, &req); err != nil {
		return entity.NewErr(400, "00", "invalid json")
	}

	dto, err := h.uc.Register(c.UserContext(), req)
	if err != nil {
		return err
	}

	return c.Status(fiber.StatusCreated).JSON(dto)
}
func (h *AuthHandler) Me(c *fiber.Ctx) error {
	aid, ok := c.Locals("authorID").(int64)
	if !ok || aid == 0 {
		return c.Status(401).JSON(entity.ErrorResponse{
			ErrorCode:    "40105",
			ErrorMessage: "unauthorized",
		})
	}

	dto, err := h.uc.GetByID(c.UserContext(), aid)
	if err != nil {
		return err
	}

	return c.JSON(dto)
}
