package config

type KafkaConfig struct {
	Brokers       []string `envconfig:"KAFKA_BROKERS" default:"localhost:9092"`
	InTopic       string   `envconfig:"KAFKA_IN_TOPIC" default:"InTopic"`
	OutTopic      string   `envconfig:"KAFKA_OUT_TOPIC" default:"OutTopic"`
	ConsumerGroup string   `envconfig:"KAFKA_CONSUMER_GROUP" default:"publisher-group"`
}
