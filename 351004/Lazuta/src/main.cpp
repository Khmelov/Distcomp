#include <drogon/HttpAppFramework.h>
#include <api/v1.0/controllers/EditorController.h>
#include <storage/BaseMemoryStorage.h>
#include "api/v1.0/controllers/IssueController.h"
#include "api/v1.0/controllers/LabelController.h"
#include "api/v1.0/controllers/PostController.h"

int main() 
{
    auto editorDAO = std::make_unique<BaseInMemoryStorage<Editor>>();
    auto issueDAO = std::make_unique<BaseInMemoryStorage<Issue>>();
    auto labelDAO = std::make_unique<BaseInMemoryStorage<Label>>();
    auto postDAO = std::make_unique<BaseInMemoryStorage<::Post>>();
    
    // 2. Создаём сервисы и передаём им DAO
    auto editorService = std::make_unique<EditorService>(std::move(editorDAO));
    auto issueService = std::make_unique<IssueService>(std::move(issueDAO));
    auto labelService = std::make_unique<LabelService>(std::move(labelDAO));
    auto postService = std::make_unique<PostService>(std::move(postDAO));
    
    // 3. Создаём контроллеры и передаём им СЕРВИСЫ
    auto editorController = std::make_shared<EditorController>(std::move(editorService));
    auto issueController = std::make_shared<IssueController>(std::move(issueService));
    auto labelController = std::make_shared<LabelController>(std::move(labelService));
    auto postController = std::make_shared<PostController>(std::move(postService));

    //std::cout << "zzz";
    drogon::app().registerController(editorController);
    drogon::app().registerController(issueController);
    drogon::app().registerController(labelController);
    drogon::app().registerController(postController);

    drogon::app().loadConfigFile("/home/dmitry/Distcomp/351004/Lazuta/config.json");
    drogon::app().run();
    return 0;
}