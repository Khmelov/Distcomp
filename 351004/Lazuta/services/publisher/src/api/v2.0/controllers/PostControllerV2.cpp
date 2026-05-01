#include "PostControllerV2.h"
#include <iostream>
#include <json/reader.h>
#include <json/writer.h>

using namespace publisher;
using namespace publisher::dto;

PostControllerV2::PostControllerV2(std::unique_ptr<PostService> service)
{
    m_service = std::move(service);
}

void PostControllerV2::CreatePost(const HttpRequestPtr& req, std::function<void(const HttpResponsePtr&)>&& callback)
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
        PostResponseTo dto = m_service->Create(PostRequestTo::fromJson(*json));
        httpResponse->setBody(Json::FastWriter().write(dto.toJson()));
        httpResponse->setStatusCode(HttpStatusCode::k201Created);
    } catch (...) {
        Json::Value error; error["errorCode"] = 40000; error["errorMessage"] = "Invalid request";
        httpResponse->setBody(Json::FastWriter().write(error));
        httpResponse->setStatusCode(HttpStatusCode::k400BadRequest);
    }
    callback(httpResponse);
}

void PostControllerV2::ReadPost(const HttpRequestPtr& req, std::function<void(const HttpResponsePtr&)>&& callback, uint64_t id)
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
        PostResponseTo dto = m_service->Read(id);
        httpResponse->setBody(Json::FastWriter().write(dto.toJson()));
        httpResponse->setStatusCode(HttpStatusCode::k200OK);
    } catch (...) {
        Json::Value error; error["errorCode"] = 40400; error["errorMessage"] = "Not found";
        httpResponse->setBody(Json::FastWriter().write(error));
        httpResponse->setStatusCode(HttpStatusCode::k404NotFound);
    }
    callback(httpResponse);
}

void PostControllerV2::UpdatePost(const HttpRequestPtr& req, std::function<void(const HttpResponsePtr&)>&& callback, uint64_t id)
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
        PostResponseTo dto = m_service->Update(PostRequestTo::fromJson(*json), id);
        httpResponse->setBody(Json::FastWriter().write(dto.toJson()));
        httpResponse->setStatusCode(HttpStatusCode::k200OK);
    } catch (...) {
        Json::Value error; error["errorCode"] = 40000; error["errorMessage"] = "Invalid request";
        httpResponse->setBody(Json::FastWriter().write(error));
        httpResponse->setStatusCode(HttpStatusCode::k400BadRequest);
    }
    callback(httpResponse);
}

void PostControllerV2::DeletePost(const HttpRequestPtr& req, std::function<void(const HttpResponsePtr&)>&& callback, uint64_t id)
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

void PostControllerV2::GetAllPosts(const HttpRequestPtr& req, std::function<void(const HttpResponsePtr&)>&& callback)
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
