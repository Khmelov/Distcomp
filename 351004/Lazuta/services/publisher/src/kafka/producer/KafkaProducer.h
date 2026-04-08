#pragma once

#include <librdkafka/rdkafkacpp.h>
#include <string>
#include <memory>

namespace publisher
{

class KafkaProducer
{
public:
    KafkaProducer(const std::string& brokers, const std::string& topic);
    ~KafkaProducer();
    
    bool Send(const std::string& key, const std::string& message);
    bool Send(const std::string& message);
    
private:
    std::unique_ptr<RdKafka::Producer> m_producer;
    std::unique_ptr<RdKafka::Topic> m_topic;
    std::string m_topicName;
};

}