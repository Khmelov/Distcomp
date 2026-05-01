#include <api/v2.0/controllers/AuthController.h>
#include <iostream>

namespace auth
{
using namespace auth::dto;

void AuthController::login(const drogon::HttpRequestPtr& req, std::function<void(const drogon::HttpResponsePtr&)>&& callback) {
    try {
        auto jsonFromRequest = req->getJsonObject();
        if (!jsonFromRequest) {
            auto resp = createErrorResponse(40000, "Invalid JSON");
            callback(resp);
            return;
        }
        
        auto loginReq = LoginRequestTo::fromJson(*jsonFromRequest);
        auto response = service_->login(loginReq);
        
        auto resp = drogon::HttpResponse::newHttpJsonResponse(response.toJson());
        resp->setStatusCode(drogon::k200OK);
        callback(resp);
    } catch (const UnauthorizedException& e) {
        callback(createErrorResponse(e.getErrorCode(), e.what()));
    } catch (const AuthException& e) {
        callback(createErrorResponse(e.getErrorCode(), e.what()));
    }
}

void AuthController::registerEditor(const drogon::HttpRequestPtr& req, std::function<void(const drogon::HttpResponsePtr&)>&& callback) {
    try {
        auto jsonFromRequest = req->getJsonObject();
        if (!jsonFromRequest) {
            auto resp = createErrorResponse(40000, "Invalid JSON");
            callback(resp);
            return;
        }
        
        auto registerReq = RegisterRequestTo::fromJson(*jsonFromRequest);
        auto response = service_->registerEditor(registerReq);
        
        auto resp = drogon::HttpResponse::newHttpJsonResponse(response.toJson());
        resp->setStatusCode(drogon::k201Created);
        callback(resp);
    } catch (const ValidationException& e) {
        callback(createErrorResponse(e.getErrorCode(), e.what()));
    } catch (const AuthException& e) {
        callback(createErrorResponse(e.getErrorCode(), e.what()));
    }
}

void AuthController::getCurrentUser(const drogon::HttpRequestPtr& req, std::function<void(const drogon::HttpResponsePtr&)>&& callback) {
    try {
        std::string login, role;
        auto token = JwtUtils::extractTokenFromHeader(req);
        if (token.empty() || !JwtUtils::validateToken(token, login, role)) {
            callback(createErrorResponse(40100, "Invalid or missing token"));
            return;
        }
        
        auto editor = service_->getEditorByLogin(login);
        if (!editor) {
            callback(createErrorResponse(40400, "User not found"));
            return;
        }
        
        auto resp = drogon::HttpResponse::newHttpJsonResponse(editor->toJson());
        resp->setStatusCode(drogon::k200OK);
        callback(resp);
    } catch (const AuthException& e) {
        callback(createErrorResponse(e.getErrorCode(), e.what()));
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
