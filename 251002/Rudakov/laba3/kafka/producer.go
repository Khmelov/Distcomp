package kafka

import (
	"encoding/json"
	"fmt"
	"log"
	"strconv"

	"github.com/confluentinc/confluent-kafka-go/kafka"
)

func SendId(id int, partition int32) {
	p, err := kafka.NewProducer(&kafka.ConfigMap{"bootstrap.servers": "127.0.0.1:9092"})
	if err != nil {
		log.Fatal("Failed to create producer:" + err.Error())
	}
	defer p.Close()

	log.Println("Producer created successfully")

	topic := "InTopic"

	value, err := json.Marshal(id)
	if err != nil {
		log.Fatal("Error marshaling answer: " + err.Error())
		panic(err)
	}
	message := kafka.Message{
		TopicPartition: kafka.TopicPartition{Topic: &topic},
		Value:          value,
		Key:            []byte(strconv.Itoa(id)),
	}

	deliveryChan := make(chan kafka.Event)

	err = p.Produce(&message, deliveryChan)
	if err != nil {
		log.Fatal("Failed to produce message: " + err.Error())
		panic(err)

	}

	go func() {
		event := <-deliveryChan
		switch e := event.(type) {
		case *kafka.Message:
			if e.TopicPartition.Error != nil {
				log.Println(fmt.Sprintf("Delivery failed: %v", e.TopicPartition.Error))
			} else {
				log.Println(fmt.Sprintf("Delivered message to %v [%d] at offset %v",
					*e.TopicPartition.Topic, e.TopicPartition.Partition, e.TopicPartition.Offset))
			}
		default:
			log.Println(fmt.Sprintf("Ignored event: %s", e))
		}
	}()

	p.Flush(1000)
}
