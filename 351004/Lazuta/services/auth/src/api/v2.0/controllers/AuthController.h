#ifndef AUTHCONTROLLER_H
#define AUTHCONTROLLER_H

#include <services/AuthService.h>
#include <dto/requests/LoginRequestTo.h>
#include <dto/requests/RegisterRequestTo.h>
#include <dto/responses/AuthResponseTo.h>
#include <dto/responses/EditorResponseTo.h>
#include <exceptions/AuthException.h>
#include <drogon/HttpController.h>
#include <memory>
#include <json/json.h>

namespace auth
{

class AuthController : public drogon::HttpController<AuthController, false> {
public:
    AuthController(std::unique_ptr<AuthService> service) : service_(std::move(service)) {}
    
    METHOD_LIST_BEGIN
        ADD_METHOD_TO(AuthController::login, "/api/v2.0/login", drogon::Post);
        ADD_METHOD_TO(AuthController::registerEditor, "/api/v2.0/editors", drogon::Post);
        ADD_METHOD_TO(AuthController::getCurrentUser, "/api/v2.0/editors/me", drogon::Get);
    METHOD_LIST_END
    
    void login(const drogon::HttpRequestPtr& req, std::function<void(const drogon::HttpResponsePtr&)>&& callback);
    void registerEditor(const drogon::HttpRequestPtr& req, std::function<void(const drogon::HttpResponsePtr&)>&& callback);
    void getCurrentUser(const drogon::HttpRequestPtr& req, std::function<void(const drogon::HttpResponsePtr&)>&& callback);
    
private:
    std::unique_ptr<AuthService> service_;
    
    drogon::HttpResponsePtr createErrorResponse(int errorCode, const std::string& errorMessage);
};

}

#endif
