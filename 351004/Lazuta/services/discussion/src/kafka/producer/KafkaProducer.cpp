#include "KafkaProducer.h"
#include <iostream>

namespace discussion
{

KafkaProducer::KafkaProducer(const std::string& brokers, const std::string& topic)
    : m_topicName(topic)
{
    RdKafka::Conf* conf = RdKafka::Conf::create(RdKafka::Conf::CONF_GLOBAL);
    std::string errstr;
    
    conf->set("bootstrap.servers", brokers, errstr);
    conf->set("acks", "all", errstr);
    conf->set("retries", "3", errstr);
    
    RdKafka::Producer* producer = RdKafka::Producer::create(conf, errstr);
    if (!producer)
    {
        std::cerr << "[ERROR] Failed to create Kafka producer: " << errstr << std::endl;
    }
    m_producer.reset(producer);
    
    RdKafka::Topic* kafkaTopic = RdKafka::Topic::create(m_producer.get(), topic, nullptr, errstr);
    if (!kafkaTopic)
    {
        std::cerr << "[ERROR] Failed to create Kafka topic: " << errstr << std::endl;
    }
    m_topic.reset(kafkaTopic);
    
    delete conf;
}

KafkaProducer::~KafkaProducer()
{
    if (m_producer)
    {
        m_producer->flush(5000);
    }
}

bool KafkaProducer::Send(const std::string& key, const std::string& message)
{
    if (!m_producer || !m_topic)
    {
        std::cerr << "[ERROR] Producer or topic not initialized" << std::endl;
        return false;
    }
    
    RdKafka::ErrorCode err = m_producer->produce(
        m_topic.get(),
        RdKafka::Topic::PARTITION_UA,
        RdKafka::Producer::RK_MSG_COPY,
        const_cast<char*>(message.c_str()), message.size(),
        key.c_str(), key.size(),
        nullptr
    );
    
    m_producer->poll(0);
    
    if (err != RdKafka::ERR_NO_ERROR)
    {
        std::cerr << "[ERROR] Failed to produce message: " << RdKafka::err2str(err) << std::endl;
        return false;
    }
    
    std::cout << "[KAFKA] Sent message to " << m_topicName << " with key: " << key << std::endl;
    return true;
}

bool KafkaProducer::Send(const std::string& message)
{
    return Send("", message);
}

}