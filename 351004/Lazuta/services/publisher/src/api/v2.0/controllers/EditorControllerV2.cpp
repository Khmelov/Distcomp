#include "EditorControllerV2.h"
#include <iostream>
#include <json/reader.h>
#include <json/writer.h>

using namespace publisher;
using namespace publisher::dto;

EditorControllerV2::EditorControllerV2(std::unique_ptr<EditorService> service)
{
    m_service = std::move(service);
    m_client  = drogon::HttpClient::newHttpClient("http://127.0.0.1:24111");
    std::cout << "[INFO] EditorControllerV2 initialized" << std::endl;
}

bool EditorControllerV2::validateJwt(const HttpRequestPtr& req, std::function<void(const HttpResponsePtr&)>& callback, std::string& login, std::string& role)
{
    auto token = JwtUtils::extractTokenFromHeader(req);
    if (token.empty()) {
        auto resp = HttpResponse::newHttpResponse();
        Json::Value error;
        error["errorCode"] = k401Unauthorized;
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

void EditorControllerV2::CreateEditor(const HttpRequestPtr& req, std::function<void(const HttpResponsePtr&)>&& callback)
{
    std::cout << "[INFO] CreateEditor V2: proxying registration to auth service" << std::endl;

    HttpResponsePtr httpResponse = HttpResponse::newHttpResponse();

    try
    {

    auto jsonFromRequest = req->getJsonObject();
    if (!jsonFromRequest)
    {
        auto resp = HttpResponse::newHttpResponse();
        Json::Value error;
        error["errorCode"] = 40000;
        error["errorMessage"] = "Invalid JSON format";
        resp->setBody(Json::FastWriter().write(error));
        resp->setContentTypeCode(ContentType::CT_APPLICATION_JSON);
        resp->setStatusCode(HttpStatusCode::k400BadRequest);
        callback(resp);
        return;
    }
    
    std::cout << "[INFO] Registration request: " << *jsonFromRequest << std::endl;
    
    // Proxy to auth service
    req->setPath("/api/v2.0/editors");
    req->addHeader("Accept", "application/json");
    req->setContentTypeCode(drogon::CT_APPLICATION_JSON);
    
    auto response = m_client->sendRequest(req, 2).second;
    callback(response);
    }
    catch(const ValidationException& e)
    {
        Json::Value errorResponse;
        errorResponse["errorCode"] = 40300;
        errorResponse["errorMessage"] = e.what();
        httpResponse->setBody(Json::FastWriter().write(errorResponse));
        httpResponse->setContentTypeCode(ContentType::CT_APPLICATION_JSON);
        httpResponse->setStatusCode(HttpStatusCode::k403Forbidden);
    }
    catch(const DatabaseException& e)
    {
        Json::Value errorResponse;
        errorResponse["errorCode"] = 50000;
        errorResponse["errorMessage"] = "Database error occurred";
        httpResponse->setBody(Json::FastWriter().write(errorResponse));
        httpResponse->setContentTypeCode(ContentType::CT_APPLICATION_JSON);
        httpResponse->setStatusCode(HttpStatusCode::k500InternalServerError);
    }
    catch(const std::exception& e)
    {
        Json::Value errorResponse;
        errorResponse["errorCode"] = 50000;
        errorResponse["errorMessage"] = "Internal server error";
        httpResponse->setBody(Json::FastWriter().write(errorResponse));
        httpResponse->setContentTypeCode(ContentType::CT_APPLICATION_JSON);
        httpResponse->setStatusCode(HttpStatusCode::k500InternalServerError);    
    }

    callback(httpResponse);
}

void EditorControllerV2::ReadEditor(const HttpRequestPtr& req, std::function<void(const HttpResponsePtr&)>&& callback, uint64_t id)
{
    std::string login, role;
    if (!validateJwt(req, callback, login, role)) return;
    
    HttpResponsePtr httpResponse = HttpResponse::newHttpResponse();

    try
    {           
        EditorResponseTo dto = m_service->Read(id);
        Json::Value jsonResponse = dto.toJson();
        
        std::string responseBody = Json::FastWriter().write(jsonResponse);
        httpResponse->setContentTypeCode(ContentType::CT_APPLICATION_JSON);
        httpResponse->setBody(responseBody);
        httpResponse->setStatusCode(HttpStatusCode::k200OK);
    }
    catch(const ValidationException& e)
    {
        Json::Value errorResponse;
        errorResponse["errorCode"] = 40000;
        errorResponse["errorMessage"] = e.what();
        httpResponse->setBody(Json::FastWriter().write(errorResponse));
        httpResponse->setContentTypeCode(ContentType::CT_APPLICATION_JSON);
        httpResponse->setStatusCode(HttpStatusCode::k400BadRequest);
    }
    catch(const NotFoundException& e)
    {
        Json::Value errorResponse;
        errorResponse["errorCode"] = 40400;
        errorResponse["errorMessage"] = e.what();
        httpResponse->setBody(Json::FastWriter().write(errorResponse));
        httpResponse->setContentTypeCode(ContentType::CT_APPLICATION_JSON);
        httpResponse->setStatusCode(HttpStatusCode::k404NotFound);
    }
    catch(const DatabaseException& e)
    {
        Json::Value errorResponse;
        errorResponse["errorCode"] = 50000;
        errorResponse["errorMessage"] = "Database error occurred";
        httpResponse->setBody(Json::FastWriter().write(errorResponse));
        httpResponse->setContentTypeCode(ContentType::CT_APPLICATION_JSON);
        httpResponse->setStatusCode(HttpStatusCode::k500InternalServerError);
    }
    catch(const std::exception& e)
    {
        Json::Value errorResponse;
        errorResponse["errorCode"] = 50000;
        errorResponse["errorMessage"] = "Internal server error";
        httpResponse->setBody(Json::FastWriter().write(errorResponse));
        httpResponse->setContentTypeCode(ContentType::CT_APPLICATION_JSON);
        httpResponse->setStatusCode(HttpStatusCode::k500InternalServerError);    
    }
    
    callback(httpResponse);
}

void EditorControllerV2::UpdateEditorIdFromRoute(const HttpRequestPtr& req, std::function<void(const HttpResponsePtr&)>&& callback, uint64_t id)
{
    std::string login, role;
    if (!validateJwt(req, callback, login, role)) return;
    
    HttpResponsePtr httpResponse = HttpResponse::newHttpResponse();

    try
    {           
        auto jsonFromRequest = req->getJsonObject();
        if (!jsonFromRequest)
        {
            Json::Value errorResponse;
            errorResponse["errorCode"] = 40000;
            errorResponse["errorMessage"] = "Invalid JSON format";
            httpResponse->setBody(Json::FastWriter().write(errorResponse));
            httpResponse->setContentTypeCode(ContentType::CT_APPLICATION_JSON);
            httpResponse->setStatusCode(HttpStatusCode::k400BadRequest);
            callback(httpResponse);
            return;
        }

        auto requestDto = EditorRequestTo::fromJson(*jsonFromRequest);
        EditorResponseTo dto = m_service->Update(requestDto, id);
        
        Json::Value jsonResponse = dto.toJson();
        std::string responseBody = Json::FastWriter().write(jsonResponse);
        httpResponse->setContentTypeCode(ContentType::CT_APPLICATION_JSON);
        httpResponse->setBody(responseBody);
        httpResponse->setStatusCode(HttpStatusCode::k200OK);
    }
    catch(const ValidationException& e)
    {
        Json::Value errorResponse;
        errorResponse["errorCode"] = 40000;
        errorResponse["errorMessage"] = e.what();
        httpResponse->setBody(Json::FastWriter().write(errorResponse));
        httpResponse->setContentTypeCode(ContentType::CT_APPLICATION_JSON);
        httpResponse->setStatusCode(HttpStatusCode::k400BadRequest);
    }
    catch(const NotFoundException& e)
    {
        Json::Value errorResponse;
        errorResponse["errorCode"] = 40400;
        errorResponse["errorMessage"] = e.what();
        httpResponse->setBody(Json::FastWriter().write(errorResponse));
        httpResponse->setContentTypeCode(ContentType::CT_APPLICATION_JSON);
        httpResponse->setStatusCode(HttpStatusCode::k404NotFound);
    }
    catch(const DatabaseException& e)
    {
        Json::Value errorResponse;
        errorResponse["errorCode"] = 50000;
        errorResponse["errorMessage"] = "Database error occurred";
        httpResponse->setBody(Json::FastWriter().write(errorResponse));
        httpResponse->setContentTypeCode(ContentType::CT_APPLICATION_JSON);
        httpResponse->setStatusCode(HttpStatusCode::k500InternalServerError);
    }
    catch(const std::exception& e)
    {
        Json::Value errorResponse;
        errorResponse["errorCode"] = 50000;
        errorResponse["errorMessage"] = "Internal server error";
        httpResponse->setBody(Json::FastWriter().write(errorResponse));
        httpResponse->setContentTypeCode(ContentType::CT_APPLICATION_JSON);
        httpResponse->setStatusCode(HttpStatusCode::k500InternalServerError);    
    }
    
    callback(httpResponse);
}

void EditorControllerV2::UpdateEditorIdFromBody(const HttpRequestPtr& req, std::function<void(const HttpResponsePtr&)>&& callback)
{
    std::string login, role;
    if (!validateJwt(req, callback, login, role)) return;
    
    HttpResponsePtr httpResponse = HttpResponse::newHttpResponse();

    try
    {           
        auto jsonFromRequest = req->getJsonObject();
        if (!jsonFromRequest)
        {
            Json::Value errorResponse;
            errorResponse["errorCode"] = 40000;
            errorResponse["errorMessage"] = "Invalid JSON format";
            httpResponse->setBody(Json::FastWriter().write(errorResponse));
            httpResponse->setContentTypeCode(ContentType::CT_APPLICATION_JSON);
            httpResponse->setStatusCode(HttpStatusCode::k400BadRequest);
            callback(httpResponse);
            return;
        }

        auto requestDto = EditorRequestTo::fromJson(*jsonFromRequest);
        if (!requestDto.id.has_value())
        {
            Json::Value errorResponse;
            errorResponse["errorCode"] = 40000;
            errorResponse["errorMessage"] = "ID is required in request body";
            httpResponse->setBody(Json::FastWriter().write(errorResponse));
            httpResponse->setContentTypeCode(ContentType::CT_APPLICATION_JSON);
            httpResponse->setStatusCode(HttpStatusCode::k400BadRequest);
            callback(httpResponse);
            return;
        }

        EditorResponseTo dto = m_service->Update(requestDto, requestDto.id.value());
        
        Json::Value jsonResponse = dto.toJson();
        std::string responseBody = Json::FastWriter().write(jsonResponse);
        httpResponse->setContentTypeCode(ContentType::CT_APPLICATION_JSON);
        httpResponse->setBody(responseBody);
        httpResponse->setStatusCode(HttpStatusCode::k200OK);
    }
    catch(const ValidationException& e)
    {
        Json::Value errorResponse;
        errorResponse["errorCode"] = 40000;
        errorResponse["errorMessage"] = e.what();
        httpResponse->setBody(Json::FastWriter().write(errorResponse));
        httpResponse->setContentTypeCode(ContentType::CT_APPLICATION_JSON);
        httpResponse->setStatusCode(HttpStatusCode::k400BadRequest);
    }
    catch(const NotFoundException& e)
    {
        Json::Value errorResponse;
        errorResponse["errorCode"] = 40400;
        errorResponse["errorMessage"] = e.what();
        httpResponse->setBody(Json::FastWriter().write(errorResponse));
        httpResponse->setContentTypeCode(ContentType::CT_APPLICATION_JSON);
        httpResponse->setStatusCode(HttpStatusCode::k404NotFound);
    }
    catch(const DatabaseException& e)
    {
        Json::Value errorResponse;
        errorResponse["errorCode"] = 50000;
        errorResponse["errorMessage"] = "Database error occurred";
        httpResponse->setBody(Json::FastWriter().write(errorResponse));
        httpResponse->setContentTypeCode(ContentType::CT_APPLICATION_JSON);
        httpResponse->setStatusCode(HttpStatusCode::k500InternalServerError);
    }
    catch(const std::exception& e)
    {
        Json::Value errorResponse;
        errorResponse["errorCode"] = 50000;
        errorResponse["errorMessage"] = "Internal server error";
        httpResponse->setBody(Json::FastWriter().write(errorResponse));
        httpResponse->setContentTypeCode(ContentType::CT_APPLICATION_JSON);
        httpResponse->setStatusCode(HttpStatusCode::k500InternalServerError);    
    }
    
    callback(httpResponse);
}

void EditorControllerV2::DeleteEditor(const HttpRequestPtr& req, std::function<void(const HttpResponsePtr&)>&& callback, uint64_t id)
{
    std::string login, role;
    HttpResponsePtr httpResponse = HttpResponse::newHttpResponse();


    if (!validateJwt(req, callback, login, role))
    {
        LOG_DEBUG << login << " was not authorised";
        Json::Value errorResponse;
        errorResponse["errorCode"] = 40400;
        errorResponse["errorMessage"] = "Editor not found";
        httpResponse->setBody(Json::FastWriter().write(errorResponse));
        httpResponse->setContentTypeCode(ContentType::CT_APPLICATION_JSON);
        httpResponse->setStatusCode(HttpStatusCode::k401Unauthorized);
    }



    if (role != "ADMIN")
    {
        Json::Value errorResponse;
        errorResponse["errorCode"] = k403Forbidden;
        errorResponse["errorMessage"] = "This endpoint is only accaessible by admins";
        httpResponse->setBody(Json::FastWriter().write(errorResponse));
        httpResponse->setContentTypeCode(ContentType::CT_APPLICATION_JSON);
        httpResponse->setStatusCode(HttpStatusCode::k403Forbidden);
        callback(httpResponse);
        return;
    }

    try
    {           
        if (m_service->Delete(id))
        {
            httpResponse->setStatusCode(HttpStatusCode::k204NoContent);
        }
        else
        {
            Json::Value errorResponse;
            errorResponse["errorCode"] = 40400;
            errorResponse["errorMessage"] = "Editor not found";
            httpResponse->setBody(Json::FastWriter().write(errorResponse));
            httpResponse->setContentTypeCode(ContentType::CT_APPLICATION_JSON);
            httpResponse->setStatusCode(HttpStatusCode::k404NotFound);
        }
    }
    catch(const ValidationException& e)
    {
        Json::Value errorResponse;
        errorResponse["errorCode"] = 40000;
        errorResponse["errorMessage"] = e.what();
        httpResponse->setBody(Json::FastWriter().write(errorResponse));
        httpResponse->setContentTypeCode(ContentType::CT_APPLICATION_JSON);
        httpResponse->setStatusCode(HttpStatusCode::k400BadRequest);
    }
    catch(const NotFoundException& e)
    {
        Json::Value errorResponse;
        errorResponse["errorCode"] = 40400;
        errorResponse["errorMessage"] = e.what();
        httpResponse->setBody(Json::FastWriter().write(errorResponse));
        httpResponse->setContentTypeCode(ContentType::CT_APPLICATION_JSON);
        httpResponse->setStatusCode(HttpStatusCode::k404NotFound);
    }
    catch(const DatabaseException& e)
    {
        Json::Value errorResponse;
        errorResponse["errorCode"] = 50000;
        errorResponse["errorMessage"] = "Database error occurred";
        httpResponse->setBody(Json::FastWriter().write(errorResponse));
        httpResponse->setContentTypeCode(ContentType::CT_APPLICATION_JSON);
        httpResponse->setStatusCode(HttpStatusCode::k500InternalServerError);
    }
    catch(const std::exception& e)
    {
        Json::Value errorResponse;
        errorResponse["errorCode"] = 50000;
        errorResponse["errorMessage"] = "Internal server error";
        httpResponse->setBody(Json::FastWriter().write(errorResponse));
        httpResponse->setContentTypeCode(ContentType::CT_APPLICATION_JSON);
        httpResponse->setStatusCode(HttpStatusCode::k500InternalServerError);    
    }
    
    callback(httpResponse);
}

void EditorControllerV2::GetAllEditors(const HttpRequestPtr& req, std::function<void(const HttpResponsePtr&)>&& callback)
{
    std::string login, role;
    if (!validateJwt(req, callback, login, role)) return;
    
    HttpResponsePtr httpResponse = HttpResponse::newHttpResponse();

    try
    {           
        std::vector<EditorResponseTo> dtos = m_service->GetAll();
        Json::Value jsonResponse(Json::arrayValue);  
        for (auto& dto: dtos)
        {
            jsonResponse.append(dto.toJson());
        }
        
        std::string responseBody = Json::FastWriter().write(jsonResponse);
        httpResponse->setContentTypeCode(ContentType::CT_APPLICATION_JSON);
        httpResponse->setBody(responseBody);
        httpResponse->setStatusCode(HttpStatusCode::k200OK);
    }
    catch(const ValidationException& e)
    {
        Json::Value errorResponse;
        errorResponse["errorCode"] = 40000;
        errorResponse["errorMessage"] = e.what();
        httpResponse->setBody(Json::FastWriter().write(errorResponse));
        httpResponse->setContentTypeCode(ContentType::CT_APPLICATION_JSON);
        httpResponse->setStatusCode(HttpStatusCode::k400BadRequest);
    }
    catch(const NotFoundException& e)
    {
        Json::Value errorResponse;
        errorResponse["errorCode"] = 40400;
        errorResponse["errorMessage"] = e.what();
        httpResponse->setBody(Json::FastWriter().write(errorResponse));
        httpResponse->setContentTypeCode(ContentType::CT_APPLICATION_JSON);
        httpResponse->setStatusCode(HttpStatusCode::k404NotFound);
    }
    catch(const DatabaseException& e)
    {
        Json::Value errorResponse;
        errorResponse["errorCode"] = 50000;
        errorResponse["errorMessage"] = "Database error occurred";
        httpResponse->setBody(Json::FastWriter().write(errorResponse));
        httpResponse->setContentTypeCode(ContentType::CT_APPLICATION_JSON);
        httpResponse->setStatusCode(HttpStatusCode::k500InternalServerError);
    }
    catch(const std::exception& e)
    {
        Json::Value errorResponse;
        errorResponse["errorCode"] = 50000;
        errorResponse["errorMessage"] = "Internal server error";
        httpResponse->setBody(Json::FastWriter().write(errorResponse));
        httpResponse->setContentTypeCode(ContentType::CT_APPLICATION_JSON);
        httpResponse->setStatusCode(HttpStatusCode::k500InternalServerError);    
    }
    
    callback(httpResponse);
}

void EditorControllerV2::Login(const HttpRequestPtr& req, std::function<void (const HttpResponsePtr &)> &&callback)
{
    auto jsonFromRequest = req->getJsonObject();
    std::cout << "[INFO] Login request: " << *jsonFromRequest << std::endl;

    req->setPath("/api/v2.0/login");
    req->addHeader("Accept", "application/json");
    req->setContentTypeCode(drogon::CT_APPLICATION_JSON);

    auto response = m_client->sendRequest(req, 2).second;
    
    callback(response);
}

void EditorControllerV2::GetCurrentUser(const HttpRequestPtr& req, std::function<void (const HttpResponsePtr &)> &&callback)
{
    std::cout << "[INFO] GetCurrentUser request to auth service" << std::endl;
    
    req->setPath("/api/v2.0/editors/me");
    req->addHeader("Accept", "application/json");
    
    auto response = m_client->sendRequest(req, 2).second;
    callback(response);
}
