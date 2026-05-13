package http

import (
	"strings"

	"github.com/gofiber/fiber/v2"

	"note-service/internal/entity"
	"note-service/internal/pkg/auth"
	"note-service/internal/repository"
)

func JWTProtect(repo repository.AuthorRepository, jwtSvc *auth.JWTService) fiber.Handler {
	return func(c *fiber.Ctx) error {
		header := strings.TrimSpace(c.Get("Authorization"))
		if header == "" || !strings.HasPrefix(header, "Bearer ") {
			return c.Status(401).JSON(entity.ErrorResponse{
				ErrorCode:    "40101",
				ErrorMessage: "Authorization Bearer token is required",
			})
		}
		raw := strings.TrimSpace(strings.TrimPrefix(header, "Bearer"))
		claims, err := jwtSvc.Parse(raw)
		if err != nil {
			return c.Status(401).JSON(entity.ErrorResponse{
				ErrorCode:    "40102",
				ErrorMessage: "invalid or expired token",
			})
		}

		login := strings.TrimSpace(claims.Subject)
		dbAuthor, err := repo.GetByLogin(c.UserContext(), login)
		if err != nil {
			return c.Status(401).JSON(entity.ErrorResponse{
				ErrorCode:    "40103",
				ErrorMessage: "user not found or token is no longer valid",
			})
		}

		if dbAuthor.Role != claims.Role {
			return c.Status(401).JSON(entity.ErrorResponse{
				ErrorCode:    "40104",
				ErrorMessage: "invalid token",
			})
		}

		c.Locals("authorID", dbAuthor.ID)
		c.Locals("login", login)
		c.Locals("role", dbAuthor.Role)
		return c.Next()
	}
}
