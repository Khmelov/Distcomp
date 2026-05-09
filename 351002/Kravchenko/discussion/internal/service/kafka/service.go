package kafka_note

import (
	"context"
	"strings"

	"labs/discussion/internal/repository"
	kafkarepo "labs/discussion/internal/repository/kafka"
	kafkamodel "labs/shared/dto/kafka"
	notemodel "labs/shared/model/note"
)

type Service interface {
	ProcessMessage(ctx context.Context, msg *kafkamodel.KafkaMessage)
}

type serviceImpl struct {
	dbRepo    repository.AppRepository
	replyRepo kafkarepo.ReplyRepository
}

func NewService(dbRepo repository.AppRepository, replyRepo kafkarepo.ReplyRepository) Service {
	return &serviceImpl{
		dbRepo:    dbRepo,
		replyRepo: replyRepo,
	}
}

func (s *serviceImpl) ProcessMessage(ctx context.Context, msg *kafkamodel.KafkaMessage) {
	resp := &kafkamodel.KafkaResponse{
		CorrelationID: msg.CorrelationID,
	}

	switch msg.Operation {
	case kafkamodel.OpCreate:
		msg.Note.State = s.moderateContent(msg.Note.Content)
		_, err := s.dbRepo.NoteRepo().Create(ctx, msg.Note)
		if err != nil {
			resp.Error = err.Error()
		}
		_ = s.replyRepo.SendReply(ctx, resp)

	case kafkamodel.OpUpdate:
		note, err := s.dbRepo.NoteRepo().GetByID(ctx, msg.Note.ID)
		if err != nil {
			resp.Error = err.Error()
		} else {
			note.Content = msg.Note.Content
			note.State = s.moderateContent(note.Content)

			if err := s.dbRepo.NoteRepo().Update(ctx, note); err != nil {
				resp.Error = err.Error()
			} else {
				resp.Note = note
			}
		}
		_ = s.replyRepo.SendReply(ctx, resp)

	case kafkamodel.OpGet:
		note, err := s.dbRepo.NoteRepo().GetByID(ctx, msg.NoteID)
		if err != nil {
			resp.Error = err.Error()
		} else {
			resp.Note = note
		}
		_ = s.replyRepo.SendReply(ctx, resp)

	case kafkamodel.OpDelete:
		err := s.dbRepo.NoteRepo().Delete(ctx, msg.NoteID)
		if err != nil {
			resp.Error = err.Error()
		}
		_ = s.replyRepo.SendReply(ctx, resp)

	case kafkamodel.OpList:
		notes, err := s.dbRepo.NoteRepo().List(ctx, 100, 0)
		if err != nil {
			resp.Error = err.Error()
		} else {
			resp.Notes = notes
		}
		_ = s.replyRepo.SendReply(ctx, resp)
	}
}

func (s *serviceImpl) moderateContent(content string) string {
	stopWords := []string{"спам", "реклама"}
	lowerContent := strings.ToLower(content)

	for _, word := range stopWords {
		if strings.Contains(lowerContent, word) {
			return notemodel.StateDecline
		}
	}
	return notemodel.StateApprove
}
