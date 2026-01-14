package kafka

import (
	"encoding/json"
	"fmt"
	"laba3/models"
	"log"

	"github.com/confluentinc/confluent-kafka-go/kafka"
)

func ConsumerComment() (models.CommentData, error) {

	var err error
	var commentRequest models.CommentData

	c, err := kafka.NewConsumer(&kafka.ConfigMap{
		"bootstrap.servers": "127.0.0.1:9092",
		"group.id":          "authService",
		"auto.offset.reset": "earliest",
	})
	if err != nil {
		log.Fatal("Failed to create consumer: " + err.Error())
	}
	defer c.Close()

	log.Println("Kafka connected successdully")

	topic := "OutTopic"

	// Вместо Subscribe используем Assign для конкретной партиции
	topicPartition := kafka.TopicPartition{
		Topic:     &topic,
		Partition: 0,
		Offset:    kafka.OffsetStored, // или kafka.OffsetStored
	}

	err = c.Assign([]kafka.TopicPartition{topicPartition})
	if err != nil {
		log.Println("Failed to assign partition: " + err.Error())
		return commentRequest, err
	}

	for {
		fmt.Println("Waiting")
		msg, err := c.ReadComment(-1)
		if err == nil {

			err := json.Unmarshal(msg.Value, &commentRequest)
			log.Println("Request recieved: " + fmt.Sprintln(commentRequest))
			if err != nil {
				log.Println("Error while consuming: " + err.Error())
				return commentRequest, err
			}

			if err != nil {
				log.Println("Error while consuming: " + err.Error())
				return commentRequest, err

			}

			log.Println("Received comment: " + string(msg.Value) + " from topic:" + fmt.Sprintln(msg.TopicPartition))

			return commentRequest, err

			// go brokercheck.BrokerCheck(authRequest)
		} else {
			log.Println("Error while consuming: " + err.Error())
			return commentRequest, err
		}
	}
}
