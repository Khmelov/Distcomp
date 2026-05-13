package http

import (
	"encoding/json"

	"github.com/gofiber/fiber/v2"
	"note-service/internal/entity"
	"note-service/internal/pkg/clients"
	"note-service/internal/repository"
)

type CommentProxyHandler struct {
	client *client.DiscussionClient
	topics repository.NoteRepository[entity.Topic]
}

func NewCommentProxyHandler(
	c *client.DiscussionClient,
	topics repository.NoteRepository[entity.Topic],
) *CommentProxyHandler {
	return &CommentProxyHandler{client: c, topics: topics}
}

// ForwardWithBody пересылает запрос в discussion-service с заранее прочитанным телом (например после RBAC).
func (h *CommentProxyHandler) ForwardWithBody(c *fiber.Ctx, raw []byte) error {
	return h.client.ProxyWithBody(c, raw)
}

func (h *CommentProxyHandler) Handle(c *fiber.Ctx) error {
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
		if _, err := h.topics.GetByID(c.UserContext(), req.TopicID); err != nil {
			return err
		}
		return h.client.ProxyWithBody(c, raw)
	default:
		return h.client.Proxy(c)
	}
}
