#include <drogon/HttpAppFramework.h>
#include <mongocxx/instance.hpp>

#include <storage/database/PostRepository.h>
#include <services/PostService.h>
#include <api/v1.0/controllers/PostController.h>

#include <kafka/consumer/KafkaConsumer.h>
#include <kafka/producer/KafkaProducer.h>
#include <services/moderation/PostModerationService.h>

int main() 
{
    mongocxx::instance mongo_instance{};

    drogon::app().loadConfigFile("/home/dmitry/Distcomp/351004/Lazuta/services/discussion/config/app/config.json");

    auto postDAO = std::make_shared<discussion::PostRepository>();
    auto postService = std::make_unique<discussion::PostService>(postDAO);
    auto postController = std::make_shared<PostController>(std::move(postService));
    
    auto inConsumer = std::make_unique<discussion::KafkaConsumer>
    (
        "localhost:9092", "InTopic", "discussion-group"
    );
    
    auto moderationService = std::make_unique<discussion::PostModerationService>();
    auto outProducer = std::make_unique<discussion::KafkaProducer>("localhost:9092", "OutTopic");
    
    inConsumer->StartConsuming([&](const std::string& m_key, const std::string& m_message) 
    {
        Json::Value json;
        Json::Reader reader;
        if (reader.parse(m_message, json)) 
        {
            int64_t postId = json["id"].asInt64();
            std::string content = json["content"].asString();
            
            auto state = moderationService->Moderate(content);
            
            postDAO->UpdateState(postId, state);
            
            Json::Value response;
            response["id"] = postId;
            response["state"] = state == discussion::PostState::APPROVE ? "APPROVE" : "DECLINE";
            outProducer->Send(std::to_string(postId), Json::FastWriter().write(response));
        }
    });
    
    drogon::app().registerController(postController);
    drogon::app().run();
    
    return 0;
}