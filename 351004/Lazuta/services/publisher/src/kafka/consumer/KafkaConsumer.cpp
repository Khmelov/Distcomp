#include "KafkaConsumer.h"
#include <iostream>

namespace publisher
{

KafkaConsumer::KafkaConsumer(const std::string& brokers, const std::string& topic, const std::string& groupId)
    : m_topicName(topic)
    , m_running(false)
{
    RdKafka::Conf* conf = RdKafka::Conf::create(RdKafka::Conf::CONF_GLOBAL);
    std::string errstr;
    
    conf->set("bootstrap.servers", brokers, errstr);
    conf->set("group.id", groupId, errstr);
    conf->set("auto.offset.reset", "earliest", errstr);
    conf->set("enable.auto.commit", "true", errstr);
    
    RdKafka::Consumer* consumer = RdKafka::Consumer::create(conf, errstr);
    if (!consumer)
    {
        std::cerr << "[ERROR] Failed to create Kafka consumer: " << errstr << std::endl;
    }
    m_consumer.reset(consumer);
    
    RdKafka::Topic* kafkaTopic = RdKafka::Topic::create(m_consumer.get(), topic, nullptr, errstr);
    if (!kafkaTopic)
    {
        std::cerr << "[ERROR] Failed to create Kafka topic: " << errstr << std::endl;
    }
    m_topic.reset(kafkaTopic);
    
    delete conf;
}

KafkaConsumer::~KafkaConsumer()
{
    StopConsuming();
}

void KafkaConsumer::StartConsuming(Callback callback)
{
    if (m_running)
    {
        return;
    }
    
    m_callback = callback;
    m_running = true;
    
    RdKafka::ErrorCode err = m_consumer->start(m_topic.get(), 0, RdKafka::Topic::OFFSET_BEGINNING);
    if (err != RdKafka::ERR_NO_ERROR)
    {
        std::cerr << "[ERROR] Failed to start consumer: " << RdKafka::err2str(err) << std::endl;
        return;
    }
    
    m_consumerThread = std::make_unique<std::thread>(&KafkaConsumer::ConsumeLoop, this);
    std::cout << "[KAFKA] Consumer started for topic: " << m_topicName << std::endl;
}

void KafkaConsumer::StopConsuming()
{
    if (!m_running)
    {
        return;
    }
    
    m_running = false;
    if (m_consumer)
    {
        m_consumer->stop(m_topic.get(), 0);
    }
    
    if (m_consumerThread && m_consumerThread->joinable())
    {
        m_consumerThread->join();
    }
    
    std::cout << "[KAFKA] Consumer stopped for topic: " << m_topicName << std::endl;
}

void KafkaConsumer::ConsumeLoop()
{
    while (m_running)
    {
        RdKafka::Message* msg = m_consumer->consume(m_topic.get(), 0, 1000);
        
        if (msg->err() == RdKafka::ERR_NO_ERROR)
        {
            std::string key;
            if (msg->key())
            {
                key = *msg->key();
            }
            
            std::string payload(static_cast<const char*>(msg->payload()), msg->len());
            
            std::cout << "[KAFKA] Received message from " << m_topicName 
                      << " with key: " << key << std::endl;
            
            if (m_callback)
            {
                m_callback(key, payload);
            }
        }
        else if (msg->err() != RdKafka::ERR__TIMED_OUT)
        {
            std::cerr << "[ERROR] Consumer error: " << msg->errstr() << std::endl;
        }
        
        delete msg;
    }
}

}