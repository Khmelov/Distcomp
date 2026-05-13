#include <drogon/HttpAppFramework.h>
#include <storage/database/EditorRepository.h>
#include <services/AuthService.h>
#include <api/v2.0/controllers/AuthController.h>

using namespace auth;

int main() {
    drogon::app().loadConfigFile("/home/dmitry/Distcomp/351004/Lazuta/services/auth/config/app/config.json");
    
    auto editorRepo = std::make_unique<EditorRepository>();
    auto authService = std::make_unique<AuthService>(std::move(editorRepo));
    auto authController = std::make_shared<AuthController>(std::move(authService));
    
    drogon::app().registerController(authController);
    drogon::app().run();
    return 0;
}
