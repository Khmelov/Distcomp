package service

import (
	"encoding/json"
	"time"
)

type EventType string

const (
	EventTypeNoticeCreated EventType = "NOTICE_CREATED"
	EventTypeNoticeUpdated EventType = "NOTICE_UPDATED"
	EventTypeNoticeDeleted EventType = "NOTICE_DELETED"

	InTopic  = "notice-requests"
	OutTopic = "notice-responses"
)

type NoticeEvent struct {
	CorrelationID string     `json:"correlationId"`
	Type          EventType  `json:"type"`
	Timestamp     time.Time  `json:"timestamp"`
	Data          NoticeData `json:"data"`
	Error         string     `json:"error,omitempty"`
}

type NoticeData struct {
	ID      int64  `json:"id"`
	NewsID  int64  `json:"newsId"`
	Content string `json:"content"`
}

func (e *NoticeEvent) Marshal() ([]byte, error) {
	return json.Marshal(e)
}

func UnmarshalNoticeEvent(data []byte) (*NoticeEvent, error) {
	var event NoticeEvent
	if err := json.Unmarshal(data, &event); err != nil {
		return nil, err
	}
	return &event, nil
}

func NewNoticeCreatedEvent(correlationID string, id, newsID int64, content string) *NoticeEvent {
	return &NoticeEvent{
		CorrelationID: correlationID,
		Type:          EventTypeNoticeCreated,
		Timestamp:     time.Now(),
		Data:          NoticeData{ID: id, NewsID: newsID, Content: content},
	}
}

func NewNoticeUpdatedEvent(correlationID string, id, newsID int64, content string) *NoticeEvent {
	return &NoticeEvent{
		CorrelationID: correlationID,
		Type:          EventTypeNoticeUpdated,
		Timestamp:     time.Now(),
		Data:          NoticeData{ID: id, NewsID: newsID, Content: content},
	}
}

func NewNoticeDeletedEvent(correlationID string, id, newsID int64) *NoticeEvent {
	return &NoticeEvent{
		CorrelationID: correlationID,
		Type:          EventTypeNoticeDeleted,
		Timestamp:     time.Now(),
		Data:          NoticeData{ID: id, NewsID: newsID},
	}
}
