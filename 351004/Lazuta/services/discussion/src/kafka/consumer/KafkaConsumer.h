#pragma once

#include <librdkafka/rdkafkacpp.h>
#include <string>
#include <functional>
#include <memory>
#include <atomic>
#include <thread>

namespace discussion
{

class KafkaConsumer
{
public:
    using Callback = std::function<void(const std::string& key, const std::string& message)>;
    
    KafkaConsumer(const std::string& brokers, const std::string& topic, const std::string& groupId);
    ~KafkaConsumer();
    
    void StartConsuming(Callback callback);
    void StopConsuming();
    
private:
    void ConsumeLoop();
    
    std::unique_ptr<RdKafka::Consumer> m_consumer;
    std::unique_ptr<RdKafka::Topic> m_topic;
    std::string m_topicName;
    std::atomic<bool> m_running;
    std::unique_ptr<std::thread> m_consumerThread;
    Callback m_callback;
};

}