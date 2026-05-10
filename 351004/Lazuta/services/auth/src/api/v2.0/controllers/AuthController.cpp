#include <api/v2.0/controllers/AuthController.h>
#include <iostream>

namespace auth
{
using namespace auth::dto;

void AuthController::login(const drogon::HttpRequestPtr& req, std::function<void(const drogon::HttpResponsePtr&)>&& callback) {
    try {
        auto jsonFromRequest = req->getJsonObject();

        if (!jsonFromRequest) {
            std::cout << "[AUTH CONTROLLER] Login failed: invalid JSON" << std::endl;
            auto resp = createErrorResponse(40000, "Invalid JSON");
            callback(resp);
            return;
        }

        std::cout << "[AUTH CONTROLLER] Login request: " << *jsonFromRequest << std::endl;
        
        auto loginReq = LoginRequestTo::fromJson(*jsonFromRequest);
        auto response = service_->login(loginReq);
        
        std::cout << "[AUTH CONTROLLER] Login successful for: " << loginReq.login << std::endl;
        
        auto resp = drogon::HttpResponse::newHttpJsonResponse(response.toJson());
        resp->setStatusCode(drogon::k200OK);
        callback(resp);
    } catch (const UnauthorizedException& e) {
        std::cout << "[AUTH CONTROLLER] Login unauthorized: " << e.what() << std::endl;
        callback(createErrorResponse(e.getErrorCode(), e.what()));
    } catch (const AuthException& e) {
        std::cout << "[AUTH CONTROLLER] Login error: " << e.what() << std::endl;
        callback(createErrorResponse(e.getErrorCode(), e.what()));
    }
}

void AuthController::registerEditor(const drogon::HttpRequestPtr& req, std::function<void(const drogon::HttpResponsePtr&)>&& callback) {
    try {
        auto jsonFromRequest = req->getJsonObject();
        if (!jsonFromRequest) {
            std::cout << "[AUTH CONTROLLER] Registration failed: invalid JSON" << std::endl;
            auto resp = createErrorResponse(40000, "Invalid JSON");
            callback(resp);
            return;
        }
        
        std::cout << "[AUTH CONTROLLER] Registration request: " << *jsonFromRequest << std::endl;
        
        auto registerReq = RegisterRequestTo::fromJson(*jsonFromRequest);
        auto response = service_->registerEditor(registerReq);
        
        std::cout << "[AUTH CONTROLLER] Registration successful for: " << registerReq.login << std::endl;
        
        auto resp = drogon::HttpResponse::newHttpJsonResponse(response.toJson());
        resp->setStatusCode(drogon::k201Created);
        callback(resp);
    } catch (const ValidationException& e) {
        std::cout << "[AUTH CONTROLLER] Registration validation error: " << e.what() << std::endl;
        callback(createErrorResponse(e.getErrorCode(), e.what()));
    } catch (const AuthException& e) {
        std::cout << "[AUTH CONTROLLER] Registration error: " << e.what() << std::endl;
        callback(createErrorResponse(400100, e.what()));
    }
}

void AuthController::getCurrentUser(const drogon::HttpRequestPtr& req, std::function<void(const drogon::HttpResponsePtr&)>&& callback) {
    try {
        std::string login, role;
        auto token = JwtUtils::extractTokenFromHeader(req);
        
        std::cout << "[AUTH CONTROLLER] GetCurrentUser request" << std::endl;
        
        if (token.empty() || !JwtUtils::validateToken(token, login, role)) {
            std::cout << "[AUTH CONTROLLER] GetCurrentUser failed: invalid token" << std::endl;
            callback(createErrorResponse(40100, "Invalid or missing token"));
            return;
        }
        
        std::cout << "[AUTH CONTROLLER] GetCurrentUser for: " << login << std::endl;
        
        auto editor = service_->getEditorByLogin(login);
        if (!editor) {
            std::cout << "[AUTH CONTROLLER] GetCurrentUser failed: user not found - " << login << std::endl;
            callback(createErrorResponse(40400, "User not found"));
            return;
        }
        
        std::cout << "[AUTH CONTROLLER] GetCurrentUser successful for: " << login << std::endl;
        
        auto resp = drogon::HttpResponse::newHttpJsonResponse(editor->toJson());
        resp->setStatusCode(drogon::k200OK);
        callback(resp);
    } catch (const AuthException& e) {
        std::cout << "[AUTH CONTROLLER] GetCurrentUser error: " << e.what() << std::endl;
        callback(createErrorResponse(400100, e.what()));
    }
}

drogon::HttpResponsePtr AuthController::createErrorResponse(int errorCode, const std::string& errorMessage) {
    Json::Value json;
    json["errorCode"] = errorCode;
    json["errorMessage"] = errorMessage;
    
    auto resp = drogon::HttpResponse::newHttpJsonResponse(json);
    int httpCode = errorCode / 100;
    resp->setStatusCode(static_cast<drogon::HttpStatusCode>(httpCode));
    return resp;
}

}
