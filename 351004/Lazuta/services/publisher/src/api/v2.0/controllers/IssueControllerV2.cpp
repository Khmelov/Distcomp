#include "IssueControllerV2.h"
#include <iostream>
#include <json/reader.h>
#include <json/writer.h>

using namespace publisher;
using namespace publisher::dto;

IssueControllerV2::IssueControllerV2(std::unique_ptr<IssueService> service)
{
    m_service = std::move(service);
}

bool IssueControllerV2::validateJwt(const HttpRequestPtr& req, std::function<void(const HttpResponsePtr&)>& callback, std::string& login, std::string& role)
{
    auto token = JwtUtils::extractTokenFromHeader(req);
    if (token.empty()) {
        auto resp = HttpResponse::newHttpResponse();
        Json::Value error;
        error["errorCode"] = 40100;
        error["errorMessage"] = "Missing authorization token";
        resp->setBody(Json::FastWriter().write(error));
        resp->setContentTypeCode(ContentType::CT_APPLICATION_JSON);
        resp->setStatusCode(HttpStatusCode::k401Unauthorized);
        callback(resp);
        return false;
    }
    
    if (!JwtUtils::validateToken(token, login, role)) {
        auto resp = HttpResponse::newHttpResponse();
        Json::Value error;
        error["errorCode"] = 40100;
        error["errorMessage"] = "Invalid or expired token";
        resp->setBody(Json::FastWriter().write(error));
        resp->setContentTypeCode(ContentType::CT_APPLICATION_JSON);
        resp->setStatusCode(HttpStatusCode::k401Unauthorized);
        callback(resp);
        return false;
    }
    return true;
}

void IssueControllerV2::CreateIssue(const HttpRequestPtr& req, std::function<void(const HttpResponsePtr&)>&& callback)
{
    std::string login, role;
    if (!validateJwt(req, callback, login, role)) return;
    
    HttpResponsePtr httpResponse = HttpResponse::newHttpResponse();
    try {
        auto jsonFromRequest = req->getJsonObject();
        if (!jsonFromRequest) {
            Json::Value error;
            error["errorCode"] = 40000;
            error["errorMessage"] = "Invalid JSON";
            httpResponse->setBody(Json::FastWriter().write(error));
            httpResponse->setStatusCode(HttpStatusCode::k400BadRequest);
            callback(httpResponse);
            return;
        }
        IssueResponseTo dto = m_service->Create(IssueRequestTo::fromJson(*jsonFromRequest));
        httpResponse->setBody(Json::FastWriter().write(dto.toJson()));
        httpResponse->setStatusCode(HttpStatusCode::k201Created);
    } catch (const std::exception& e) {
        Json::Value error;
        error["errorCode"] = 50000;
        error["errorMessage"] = e.what();
        httpResponse->setBody(Json::FastWriter().write(error));
        httpResponse->setStatusCode(HttpStatusCode::k500InternalServerError);
    }
    callback(httpResponse);
}

void IssueControllerV2::ReadIssue(const HttpRequestPtr& req, std::function<void(const HttpResponsePtr&)>&& callback, uint64_t id)
{
    std::string login, role;
    if (!validateJwt(req, callback, login, role)) return;
    
    HttpResponsePtr httpResponse = HttpResponse::newHttpResponse();
    try {
        IssueResponseTo dto = m_service->Read(id);
        httpResponse->setBody(Json::FastWriter().write(dto.toJson()));
        httpResponse->setStatusCode(HttpStatusCode::k200OK);
    } catch (const NotFoundException& e) {
        Json::Value error;
        error["errorCode"] = 40400;
        error["errorMessage"] = e.what();
        httpResponse->setBody(Json::FastWriter().write(error));
        httpResponse->setStatusCode(HttpStatusCode::k404NotFound);
    } catch (const std::exception& e) {
        Json::Value error;
        error["errorCode"] = 50000;
        error["errorMessage"] = e.what();
        httpResponse->setBody(Json::FastWriter().write(error));
        httpResponse->setStatusCode(HttpStatusCode::k500InternalServerError);
    }
    callback(httpResponse);
}

void IssueControllerV2::UpdateIssueIdFromRoute(const HttpRequestPtr& req, std::function<void(const HttpResponsePtr&)>&& callback, uint64_t id)
{
    std::string login, role;
    if (!validateJwt(req, callback, login, role)) return;
    
    HttpResponsePtr httpResponse = HttpResponse::newHttpResponse();
    try {
        auto jsonFromRequest = req->getJsonObject();
        if (!jsonFromRequest) {
            Json::Value error;
            error["errorCode"] = 40000;
            error["errorMessage"] = "Invalid JSON";
            httpResponse->setBody(Json::FastWriter().write(error));
            httpResponse->setStatusCode(HttpStatusCode::k400BadRequest);
            callback(httpResponse);
            return;
        }
        IssueResponseTo dto = m_service->Update(IssueRequestTo::fromJson(*jsonFromRequest), id);
        httpResponse->setBody(Json::FastWriter().write(dto.toJson()));
        httpResponse->setStatusCode(HttpStatusCode::k200OK);
    } catch (const std::exception& e) {
        Json::Value error;
        error["errorCode"] = 50000;
        error["errorMessage"] = e.what();
        httpResponse->setBody(Json::FastWriter().write(error));
        httpResponse->setStatusCode(HttpStatusCode::k500InternalServerError);
    }
    callback(httpResponse);
}

void IssueControllerV2::UpdateIssueIdFromBody(const HttpRequestPtr& req, std::function<void(const HttpResponsePtr&)>&& callback)
{
    std::string login, role;
    if (!validateJwt(req, callback, login, role)) return;
    
    HttpResponsePtr httpResponse = HttpResponse::newHttpResponse();
    try {
        auto jsonFromRequest = req->getJsonObject();
        if (!jsonFromRequest) {
            Json::Value error;
            error["errorCode"] = 40000;
            error["errorMessage"] = "Invalid JSON";
            httpResponse->setBody(Json::FastWriter().write(error));
            httpResponse->setStatusCode(HttpStatusCode::k400BadRequest);
            callback(httpResponse);
            return;
        }
        auto requestDto = IssueRequestTo::fromJson(*jsonFromRequest);
        if (!requestDto.id.has_value()) {
            Json::Value error;
            error["errorCode"] = 40000;
            error["errorMessage"] = "ID required";
            httpResponse->setBody(Json::FastWriter().write(error));
            httpResponse->setStatusCode(HttpStatusCode::k400BadRequest);
            callback(httpResponse);
            return;
        }
        IssueResponseTo dto = m_service->Update(requestDto, requestDto.id.value());
        httpResponse->setBody(Json::FastWriter().write(dto.toJson()));
        httpResponse->setStatusCode(HttpStatusCode::k200OK);
    } catch (const std::exception& e) {
        Json::Value error;
        error["errorCode"] = 50000;
        error["errorMessage"] = e.what();
        httpResponse->setBody(Json::FastWriter().write(error));
        httpResponse->setStatusCode(HttpStatusCode::k500InternalServerError);
    }
    callback(httpResponse);
}

void IssueControllerV2::DeleteIssue(const HttpRequestPtr& req, std::function<void(const HttpResponsePtr&)>&& callback, uint64_t id)
{
    std::string login, role;
    if (!validateJwt(req, callback, login, role)) return;
    
    HttpResponsePtr httpResponse = HttpResponse::newHttpResponse();
    try {
        m_service->Delete(id);
        httpResponse->setStatusCode(HttpStatusCode::k204NoContent);
    } catch (const std::exception& e) {
        Json::Value error;
        error["errorCode"] = 50000;
        error["errorMessage"] = e.what();
        httpResponse->setBody(Json::FastWriter().write(error));
        httpResponse->setStatusCode(HttpStatusCode::k500InternalServerError);
    }
    callback(httpResponse);
}

void IssueControllerV2::GetAllIssues(const HttpRequestPtr& req, std::function<void(const HttpResponsePtr&)>&& callback)
{
    std::string login, role;
    if (!validateJwt(req, callback, login, role)) return;
    
    HttpResponsePtr httpResponse = HttpResponse::newHttpResponse();
    try {
        auto dtos = m_service->GetAll();
        Json::Value jsonResponse(Json::arrayValue);
        for (auto& dto : dtos) {
            jsonResponse.append(dto.toJson());
        }
        httpResponse->setBody(Json::FastWriter().write(jsonResponse));
        httpResponse->setStatusCode(HttpStatusCode::k200OK);
    } catch (const std::exception& e) {
        Json::Value error;
        error["errorCode"] = 50000;
        error["errorMessage"] = e.what();
        httpResponse->setBody(Json::FastWriter().write(error));
        httpResponse->setStatusCode(HttpStatusCode::k500InternalServerError);
    }
    callback(httpResponse);
}
