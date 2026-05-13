package http

import (
	"encoding/json"

	"github.com/gofiber/fiber/v2"

	"note-service/internal/entity"
	"note-service/internal/repository"
	"note-service/internal/usecase"
)

// SecuredHandlers — маршруты /api/v2.0 с JWT и RBAC (ADMIN / CUSTOMER).
type SecuredHandlers struct {
	AuthorH    *AuthorHandler
	TopicH     *TopicHandler
	MarkerH    *MarkerHandler
	CommentH   *CommentProxyHandler
	TopicsRepo repository.NoteRepository[entity.Topic]
	AuthorsUC  *usecase.AuthorUsecase
	TopicsUC   *usecase.TopicUsecase
	MarkersUC  *usecase.MarkerUsecase
}

func NewSecuredHandlers(
	authorH *AuthorHandler,
	topicH *TopicHandler,
	markerH *MarkerHandler,
	commentH *CommentProxyHandler,
	topicsRepo repository.NoteRepository[entity.Topic],
	authorsUC *usecase.AuthorUsecase,
	topicsUC *usecase.TopicUsecase,
	markersUC *usecase.MarkerUsecase,
) *SecuredHandlers {
	return &SecuredHandlers{
		AuthorH:    authorH,
		TopicH:     topicH,
		MarkerH:    markerH,
		CommentH:   commentH,
		TopicsRepo: topicsRepo,
		AuthorsUC:  authorsUC,
		TopicsUC:   topicsUC,
		MarkersUC:  markersUC,
	}
}

func roleFromLocals(c *fiber.Ctx) string {
	r, _ := c.Locals("role").(string)
	return r
}

func authorIDFromLocals(c *fiber.Ctx) int64 {
	id, _ := c.Locals("authorID").(int64)
	return id
}

func authorIDFromRequest(c *fiber.Ctx, raw []byte) (int64, error) {
	if v, err := c.ParamsInt("id"); err == nil && v != 0 {
		return int64(v), nil
	}
	var probe struct {
		ID int64 `json:"id"`
	}
	if err := json.Unmarshal(raw, &probe); err != nil {
		return 0, entity.NewErr(400, "00", "invalid json body")
	}
	if probe.ID == 0 {
		return 0, entity.NewErr(400, "00", "id is missing from URL or request body")
	}
	return probe.ID, nil
}

func markerIDFromRequest(c *fiber.Ctx, raw []byte) (int64, error) {
	return authorIDFromRequest(c, raw)
}

func topicTargetIDFromRequest(c *fiber.Ctx, raw []byte) (int64, error) {
	if v, err := c.ParamsInt("id"); err == nil && v != 0 {
		return int64(v), nil
	}
	var probe struct {
		ID int64 `json:"id"`
	}
	if err := json.Unmarshal(raw, &probe); err != nil {
		return 0, entity.NewErr(400, "00", "invalid json body")
	}
	if probe.ID == 0 {
		return 0, entity.NewErr(400, "00", "id is missing from URL or request body")
	}
	return probe.ID, nil
}

func (s *SecuredHandlers) AuthorGetAll(c *fiber.Ctx) error {
	return s.AuthorH.GetAll(c)
}

func (s *SecuredHandlers) AuthorGetByID(c *fiber.Ctx) error {
	id, err := c.ParamsInt("id")
	if err != nil {
		return entity.NewErr(400, "00", "invalid id")
	}
	dto, err := s.AuthorsUC.GetByID(c.UserContext(), int64(id))
	if err != nil {
		return err
	}
	return c.JSON(dto)
}

func (s *SecuredHandlers) AuthorUpdate(c *fiber.Ctx) error {
	raw := append([]byte(nil), c.Body()...)

	id, err := authorIDFromRequest(c, raw)
	if err != nil {
		return err
	}

	role := roleFromLocals(c)
	aid := authorIDFromLocals(c)

	if role != entity.RoleAdmin && id != aid {
		return entity.NewErr(403, "01", "forbidden")
	}

	var req entity.AuthorRequestTo
	if err := json.Unmarshal(raw, &req); err != nil {
		return entity.NewErr(400, "00", "invalid json body")
	}

	if role != entity.RoleAdmin {
		existing, err := s.AuthorsUC.GetByID(c.UserContext(), id)
		if err != nil {
			return err
		}
		req.Role = existing.Role
	}

	resp, err := s.AuthorsUC.Update(c.UserContext(), id, req)
	if err != nil {
		return err
	}
	return c.Status(fiber.StatusOK).JSON(resp)
}

func (s *SecuredHandlers) AuthorDelete(c *fiber.Ctx) error {
	id, err := c.ParamsInt("id")
	if err != nil {
		return entity.NewErr(400, "00", "invalid id")
	}

	role := roleFromLocals(c)
	aid := authorIDFromLocals(c)

	if role != entity.RoleAdmin && int64(id) != aid {
		return entity.NewErr(403, "01", "forbidden")
	}

	return s.AuthorH.Delete(c)
}

func (s *SecuredHandlers) TopicGetAll(c *fiber.Ctx) error {
	return s.TopicH.GetAll(c)
}

func (s *SecuredHandlers) TopicGetByID(c *fiber.Ctx) error {
	return s.TopicH.GetByID(c)
}

func (s *SecuredHandlers) TopicCreate(c *fiber.Ctx) error {
	var req entity.TopicRequestTo
	if err := c.BodyParser(&req); err != nil {
		return entity.NewErr(400, "00", "invalid json")
	}

	role := roleFromLocals(c)
	aid := authorIDFromLocals(c)

	if role != entity.RoleAdmin && req.AuthorID != aid {
		return entity.NewErr(403, "01", "forbidden")
	}

	resp, err := s.TopicsUC.Create(c.UserContext(), req)
	if err != nil {
		return err
	}
	return c.Status(fiber.StatusCreated).JSON(resp)
}

func (s *SecuredHandlers) TopicUpdate(c *fiber.Ctx) error {
	raw := append([]byte(nil), c.Body()...)

	id, err := topicTargetIDFromRequest(c, raw)
	if err != nil {
		return err
	}

	topicRow, err := s.TopicsRepo.GetByID(c.UserContext(), id)
	if err != nil {
		return err
	}

	role := roleFromLocals(c)
	aid := authorIDFromLocals(c)

	if role != entity.RoleAdmin && topicRow.AuthorID != aid {
		return entity.NewErr(403, "01", "forbidden")
	}

	var req entity.TopicRequestTo
	if err := json.Unmarshal(raw, &req); err != nil {
		return entity.NewErr(400, "00", "invalid json body")
	}

	resp, err := s.TopicsUC.Update(c.UserContext(), id, req)
	if err != nil {
		return err
	}
	return c.Status(fiber.StatusOK).JSON(resp)
}

func (s *SecuredHandlers) TopicDelete(c *fiber.Ctx) error {
	id, err := c.ParamsInt("id")
	if err != nil {
		return entity.NewErr(400, "00", "invalid id")
	}

	topicRow, err := s.TopicsRepo.GetByID(c.UserContext(), int64(id))
	if err != nil {
		return err
	}

	role := roleFromLocals(c)
	aid := authorIDFromLocals(c)

	if role != entity.RoleAdmin && topicRow.AuthorID != aid {
		return entity.NewErr(403, "01", "forbidden")
	}

	return s.TopicH.Delete(c)
}

func (s *SecuredHandlers) MarkerGetAll(c *fiber.Ctx) error {
	return s.MarkerH.GetAll(c)
}

func (s *SecuredHandlers) MarkerGetByID(c *fiber.Ctx) error {
	return s.MarkerH.GetByID(c)
}

func (s *SecuredHandlers) MarkerCreate(c *fiber.Ctx) error {
	if roleFromLocals(c) != entity.RoleAdmin {
		return entity.NewErr(403, "02", "admin role required")
	}

	var req entity.MarkerRequestTo
	if err := c.BodyParser(&req); err != nil {
		return entity.NewErr(400, "00", "invalid json")
	}

	resp, err := s.MarkersUC.Create(c.UserContext(), req)
	if err != nil {
		return err
	}
	return c.Status(fiber.StatusCreated).JSON(resp)
}

func (s *SecuredHandlers) MarkerUpdate(c *fiber.Ctx) error {
	if roleFromLocals(c) != entity.RoleAdmin {
		return entity.NewErr(403, "02", "admin role required")
	}

	raw := append([]byte(nil), c.Body()...)

	id, err := markerIDFromRequest(c, raw)
	if err != nil {
		return err
	}

	var req entity.MarkerRequestTo
	if err := json.Unmarshal(raw, &req); err != nil {
		return entity.NewErr(400, "00", "invalid json body")
	}

	resp, err := s.MarkersUC.Update(c.UserContext(), id, req)
	if err != nil {
		return err
	}
	return c.Status(fiber.StatusOK).JSON(resp)
}

func (s *SecuredHandlers) MarkerDelete(c *fiber.Ctx) error {
	if roleFromLocals(c) != entity.RoleAdmin {
		return entity.NewErr(403, "02", "admin role required")
	}

	return s.MarkerH.Delete(c)
}

func (s *SecuredHandlers) CommentsRoute(c *fiber.Ctx) error {
	switch c.Method() {
	case fiber.MethodPost, fiber.MethodPut:
		raw := append([]byte(nil), c.Body()...)
		if len(raw) == 0 {
			return entity.NewErr(400, "00", "empty body")
		}
		var req entity.CommentRequestTo
		if err := json.Unmarshal(raw, &req); err != nil {
			return entity.NewErr(400, "00", "invalid json")
		}

		topicRow, err := s.TopicsRepo.GetByID(c.UserContext(), req.TopicID)
		if err != nil {
			return err
		}

		role := roleFromLocals(c)
		aid := authorIDFromLocals(c)

		if role != entity.RoleAdmin && topicRow.AuthorID != aid {
			return entity.NewErr(403, "01", "forbidden")
		}

		return s.CommentH.ForwardWithBody(c, raw)
	default:
		return s.CommentH.Handle(c)
	}
}
