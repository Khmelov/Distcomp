#include "IssueLabelControllerV2.h"
#include <json/reader.h>
#include <json/writer.h>

using namespace publisher;
using namespace publisher::dto;

IssueLabelControllerV2::IssueLabelControllerV2(std::unique_ptr<IssueLabelService> service)
{
    m_service = std::move(service);
}

void IssueLabelControllerV2::CreateIssueLabel(const HttpRequestPtr& req, std::function<void(const HttpResponsePtr&)>&& callback)
{
    std::string login, role;
    auto token = JwtUtils::extractTokenFromHeader(req);
    if (token.empty() || !JwtUtils::validateToken(token, login, role)) {
        auto resp = HttpResponse::newHttpResponse();
        Json::Value error; error["errorCode"] = 40100; error["errorMessage"] = "Unauthorized";
        resp->setBody(Json::FastWriter().write(error));
        resp->setStatusCode(HttpStatusCode::k401Unauthorized);
        callback(resp); return;
    }
    
    HttpResponsePtr httpResponse = HttpResponse::newHttpResponse();
    try {
        auto json = req->getJsonObject();
        if (!json) throw std::exception();
        IssueLabelResponseTo dto = m_service->Create(IssueLabelRequestTo::fromJson(*json));
        httpResponse->setBody(Json::FastWriter().write(dto.toJson()));
        httpResponse->setStatusCode(HttpStatusCode::k201Created);
    } catch (...) {
        Json::Value error; error["errorCode"] = 40000; error["errorMessage"] = "Invalid request";
        httpResponse->setBody(Json::FastWriter().write(error));
        httpResponse->setStatusCode(HttpStatusCode::k400BadRequest);
    }
    callback(httpResponse);
}

void IssueLabelControllerV2::ReadIssueLabel(const HttpRequestPtr& req, std::function<void(const HttpResponsePtr&)>&& callback, uint64_t id)
{
    std::string login, role;
    auto token = JwtUtils::extractTokenFromHeader(req);
    if (token.empty() || !JwtUtils::validateToken(token, login, role)) {
        auto resp = HttpResponse::newHttpResponse();
        Json::Value error; error["errorCode"] = 40100; error["errorMessage"] = "Unauthorized";
        resp->setBody(Json::FastWriter().write(error));
        resp->setStatusCode(HttpStatusCode::k401Unauthorized);
        callback(resp); return;
    }
    
    HttpResponsePtr httpResponse = HttpResponse::newHttpResponse();
    try {
        IssueLabelResponseTo dto = m_service->Read(id);
        httpResponse->setBody(Json::FastWriter().write(dto.toJson()));
        httpResponse->setStatusCode(HttpStatusCode::k200OK);
    } catch (...) {
        Json::Value error; error["errorCode"] = 40400; error["errorMessage"] = "Not found";
        httpResponse->setBody(Json::FastWriter().write(error));
        httpResponse->setStatusCode(HttpStatusCode::k404NotFound);
    }
    callback(httpResponse);
}

void IssueLabelControllerV2::DeleteIssueLabel(const HttpRequestPtr& req, std::function<void(const HttpResponsePtr&)>&& callback, uint64_t id)
{
    std::string login, role;
    auto token = JwtUtils::extractTokenFromHeader(req);
    if (token.empty() || !JwtUtils::validateToken(token, login, role)) {
        auto resp = HttpResponse::newHttpResponse();
        Json::Value error; error["errorCode"] = 40100; error["errorMessage"] = "Unauthorized";
        resp->setBody(Json::FastWriter().write(error));
        resp->setStatusCode(HttpStatusCode::k401Unauthorized);
        callback(resp); return;
    }
    
    HttpResponsePtr httpResponse = HttpResponse::newHttpResponse();
    try {
        m_service->Delete(id);
        httpResponse->setStatusCode(HttpStatusCode::k204NoContent);
    } catch (...) {
        Json::Value error; error["errorCode"] = 40400; error["errorMessage"] = "Not found";
        httpResponse->setBody(Json::FastWriter().write(error));
        httpResponse->setStatusCode(HttpStatusCode::k404NotFound);
    }
    callback(httpResponse);
}

void IssueLabelControllerV2::GetAllIssueLabels(const HttpRequestPtr& req, std::function<void(const HttpResponsePtr&)>&& callback)
{
    std::string login, role;
    auto token = JwtUtils::extractTokenFromHeader(req);
    if (token.empty() || !JwtUtils::validateToken(token, login, role)) {
        auto resp = HttpResponse::newHttpResponse();
        Json::Value error; error["errorCode"] = 40100; error["errorMessage"] = "Unauthorized";
        resp->setBody(Json::FastWriter().write(error));
        resp->setStatusCode(HttpStatusCode::k401Unauthorized);
        callback(resp); return;
    }
    
    HttpResponsePtr httpResponse = HttpResponse::newHttpResponse();
    auto dtos = m_service->GetAll();
    Json::Value jsonResponse(Json::arrayValue);
    for (auto& dto : dtos) jsonResponse.append(dto.toJson());
    httpResponse->setBody(Json::FastWriter().write(jsonResponse));
    httpResponse->setStatusCode(HttpStatusCode::k200OK);
    callback(httpResponse);
}
