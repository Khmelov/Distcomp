#include <drogon/HttpAppFramework.h>

#include <storage/database/IssueLabelRepository.h>
#include <storage/database/EditorRepository.h>
#include <storage/database/IssueRepository.h>
#include <storage/database/LabelRepository.h>
#include <storage/database/PostRepository.h>

#include <api/v1.0/controllers/IssueLabelController.h>
#include <api/v1.0/controllers/EditorController.h>
#include "api/v1.0/controllers/IssueController.h"
#include "api/v1.0/controllers/LabelController.h"
#include "api/v1.0/controllers/PostController.h"

#include <kafka/producer/KafkaProducer.h>
#include <kafka/consumer/KafkaConsumer.h>
#include <json/json.h>

int main() 
{
    drogon::app().loadConfigFile("/home/dmitry/Distcomp/351004/Lazuta/services/publisher/config/app/config.json");
    
    auto issueLabelDAO = std::make_shared<IssueLabelRepository>();
    auto editorDAO = std::make_shared<EditorRepository>();
    auto issueDAO = std::make_shared<IssueRepository>();
    auto labelDAO = std::make_shared<LabelRepository>();
    auto postDAO = std::make_shared<PostRepository>();

    auto kafkaProducer = std::make_unique<publisher::KafkaProducer>("localhost:9092", "InTopic");
    
    auto issueLabelService = std::make_unique<IssueLabelService>(issueLabelDAO, issueDAO, labelDAO);
    auto issueService = std::make_unique<IssueService>(issueDAO, editorDAO, labelDAO, issueLabelDAO);
    auto postService = std::make_unique<PostService>(postDAO, issueDAO, std::move(kafkaProducer));
    auto editorService = std::make_unique<EditorService>(editorDAO);
    auto labelService = std::make_unique<LabelService>(labelDAO);
    
    auto issueLabelController = std::make_shared<IssueLabelController>(std::move(issueLabelService));
    auto editorController = std::make_shared<EditorController>(std::move(editorService));
    auto issueController = std::make_shared<IssueController>(std::move(issueService));
    auto labelController = std::make_shared<LabelController>(std::move(labelService));
    auto postController = std::make_shared<PostController>(std::move(postService));

    auto outConsumer = std::make_unique<publisher::KafkaConsumer>("localhost:9092", "OutTopic", "publisher-group");
    
    outConsumer->StartConsuming([](const std::string& key, const std::string& message) 
    {
        Json::Value json;
        Json::Reader reader;
        if (reader.parse(message, json))
        {
            int64_t postId = json["id"].asInt64();
            std::string state = json["state"].asString();
            std::cout << "[KAFKA] Post " << postId << " moderation result: " << state << std::endl;
        }
    });

    drogon::app().registerController(editorController);
    drogon::app().registerController(issueController);
    drogon::app().registerController(labelController);
    drogon::app().registerController(postController);

    drogon::app().run();
    return 0;
}