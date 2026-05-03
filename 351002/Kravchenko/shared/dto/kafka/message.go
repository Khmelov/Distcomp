package kafka

import notemodel "labs/shared/model/note"

const (
	OpCreate = "CREATE"
	OpUpdate = "UPDATE"
	OpDelete = "DELETE"
	OpGet    = "GET"
	OpList   = "LIST"
)

type KafkaMessage struct {
	CorrelationID string          `json:"correlation_id,omitempty"`
	Operation     string          `json:"operation"`
	Note          *notemodel.Note `json:"note,omitempty"`
	NoteID        int64           `json:"note_id,omitempty"`
}

type KafkaResponse struct {
	CorrelationID string            `json:"correlation_id"`
	Note          *notemodel.Note   `json:"note,omitempty"`
	Notes         []*notemodel.Note `json:"notes,omitempty"`
	Error         string            `json:"error,omitempty"`
}
