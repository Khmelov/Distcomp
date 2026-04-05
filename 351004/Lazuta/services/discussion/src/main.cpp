#include <drogon/HttpAppFramework.h>
#include <mongocxx/instance.hpp>

#include <storage/database/PostRepository.h>
#include <services/PostService.h>
#include <api/v1.0/controllers/PostController.h>

int main() 
{
    mongocxx::instance mongo_instance{};
    
    drogon::app().loadConfigFile("/home/dmitry/Distcomp/351004/Lazuta/services/discussion/config/app/config.json");
    
    auto postDAO = std::make_shared<discussion::PostRepository>();
    auto postService = std::make_unique<discussion::PostService>(postDAO);
    auto postController = std::make_shared<PostController>(std::move(postService));

    drogon::app().registerController(postController);
    drogon::app().run();
    
    return 0;
}