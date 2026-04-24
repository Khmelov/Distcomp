package service

import (
	"context"
	"encoding/json"
	"publisher/internal/domain"
	"strconv"

	"github.com/segmentio/kafka-go"
)

var writer = &kafka.Writer{
	Addr:     kafka.TCP("localhost:9092"),
	Topic:    "InTopic",
	Balancer: &kafka.Hash{},
}

func SendNoteToKafka(note domain.Note) {
	payload, _ := json.Marshal(note)
	writer.WriteMessages(context.Background(), kafka.Message{
		Key:   []byte(strconv.Itoa(note.TopicID)),
		Value: payload,
	})
}
