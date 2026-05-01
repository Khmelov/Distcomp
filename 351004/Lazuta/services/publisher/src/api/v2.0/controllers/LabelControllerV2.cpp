#include "LabelControllerV2.h"
#include <iostream>
#include <json/reader.h>
#include <json/writer.h>

using namespace publisher;
using namespace publisher::dto;

LabelControllerV2::LabelControllerV2(std::unique_ptr<LabelService> service)
{
    m_service = std::move(service);
}

void LabelControllerV2::CreateLabel(const HttpRequestPtr& req, std::function<void(const HttpResponsePtr&)>&& callback)
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
        LabelResponseTo dto = m_service->Create(LabelRequestTo::fromJson(*json));
        httpResponse->setBody(Json::FastWriter().write(dto.toJson()));
        httpResponse->setStatusCode(HttpStatusCode::k201Created);
    } catch (...) {
        Json::Value error; error["errorCode"] = 40000; error["errorMessage"] = "Invalid request";
        httpResponse->setBody(Json::FastWriter().write(error));
        httpResponse->setStatusCode(HttpStatusCode::k400BadRequest);
    }
    callback(httpResponse);
}

void LabelControllerV2::ReadLabel(const HttpRequestPtr& req, std::function<void(const HttpResponsePtr&)>&& callback, uint64_t id)
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
        LabelResponseTo dto = m_service->Read(id);
        httpResponse->setBody(Json::FastWriter().write(dto.toJson()));
        httpResponse->setStatusCode(HttpStatusCode::k200OK);
    } catch (...) {
        Json::Value error; error["errorCode"] = 40400; error["errorMessage"] = "Not found";
        httpResponse->setBody(Json::FastWriter().write(error));
        httpResponse->setStatusCode(HttpStatusCode::k404NotFound);
    }
    callback(httpResponse);
}

void LabelControllerV2::UpdateLabel(const HttpRequestPtr& req, std::function<void(const HttpResponsePtr&)>&& callback, uint64_t id)
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
        LabelResponseTo dto = m_service->Update(LabelRequestTo::fromJson(*json), id);
        httpResponse->setBody(Json::FastWriter().write(dto.toJson()));
        httpResponse->setStatusCode(HttpStatusCode::k200OK);
    } catch (...) {
        Json::Value error; error["errorCode"] = 40000; error["errorMessage"] = "Invalid request";
        httpResponse->setBody(Json::FastWriter().write(error));
        httpResponse->setStatusCode(HttpStatusCode::k400BadRequest);
    }
    callback(httpResponse);
}

void LabelControllerV2::DeleteLabel(const HttpRequestPtr& req, std::function<void(const HttpResponsePtr&)>&& callback, uint64_t id)
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

void LabelControllerV2::GetAllLabels(const HttpRequestPtr& req, std::function<void(const HttpResponsePtr&)>&& callback)
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
