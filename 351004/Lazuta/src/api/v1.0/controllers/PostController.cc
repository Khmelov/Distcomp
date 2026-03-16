// PostController.cc
#include "PostController.h"
#include <iostream>

PostController::PostController(std::unique_ptr<PostService> service)
{
    m_service = std::move(service);
    std::cout << "[INFO] PostController initialized" << std::endl;
}

void PostController::CreatePost(const HttpRequestPtr& req, std::function<void(const HttpResponsePtr&)>&& callback)
{
    HttpResponsePtr httpResponse = HttpResponse::newHttpResponse();
    std::cout << "[INFO] CreatePost called" << std::endl;

    try
    {
        auto jsonFromRequest = req->getJsonObject();
        if (!jsonFromRequest)
        {
            std::cout << "[ERROR] Invalid JSON in CreatePost" << std::endl;
            Json::Value errorResponse;
            errorResponse["message"] = "Invalid JSON format";
            httpResponse->setBody(Json::FastWriter().write(errorResponse));
            httpResponse->setContentTypeCode(ContentType::CT_APPLICATION_JSON);
            httpResponse->setStatusCode(HttpStatusCode::k400BadRequest);
            callback(httpResponse);
            return;
        }

        PostResponseTo dto = m_service->Create(PostRequestTo::fromJson(*jsonFromRequest));
        Json::Value jsonResponse = dto.toJson();
        
        std::string responseBody = Json::FastWriter().write(jsonResponse);
        std::cout << "[RESPONSE] " << responseBody << std::endl;
        
        httpResponse->setContentTypeCode(ContentType::CT_APPLICATION_JSON);
        httpResponse->setBody(responseBody);
        httpResponse->setStatusCode(HttpStatusCode::k201Created);
        std::cout << "[INFO] Post created successfully" << std::endl;
    }
    catch(const ValidationException& e)
    {
        std::cout << "[ERROR] Validation error: " << e.what() << std::endl;
        Json::Value errorResponse;
        errorResponse["message"] = e.what();
        httpResponse->setBody(Json::FastWriter().write(errorResponse));
        httpResponse->setContentTypeCode(ContentType::CT_APPLICATION_JSON);
        httpResponse->setStatusCode(HttpStatusCode::k400BadRequest);
    }
    catch(const NotFoundException& e)
    {
        std::cout << "[ERROR] Not found: " << e.what() << std::endl;
        Json::Value errorResponse;
        errorResponse["message"] = e.what();
        httpResponse->setBody(Json::FastWriter().write(errorResponse));
        httpResponse->setContentTypeCode(ContentType::CT_APPLICATION_JSON);
        httpResponse->setStatusCode(HttpStatusCode::k404NotFound);
    }
    catch(const DatabaseException& e)
    {
        std::cout << "[ERROR] Database error: " << e.what() << std::endl;
        Json::Value errorResponse;
        errorResponse["message"] = "Database error occurred";
        httpResponse->setBody(Json::FastWriter().write(errorResponse));
        httpResponse->setContentTypeCode(ContentType::CT_APPLICATION_JSON);
        httpResponse->setStatusCode(HttpStatusCode::k500InternalServerError);
    }
    catch(const std::exception& e)
    {
        std::cout << "[ERROR] Unknown error: " << e.what() << std::endl;
        Json::Value errorResponse;
        errorResponse["message"] = "Internal server error";
        httpResponse->setBody(Json::FastWriter().write(errorResponse));
        httpResponse->setContentTypeCode(ContentType::CT_APPLICATION_JSON);
        httpResponse->setStatusCode(HttpStatusCode::k500InternalServerError);
    }

    callback(httpResponse);
}

void PostController::ReadPost(const HttpRequestPtr& req, std::function<void(const HttpResponsePtr&)>&& callback, uint64_t id)
{
    HttpResponsePtr httpResponse = HttpResponse::newHttpResponse();
    std::cout << "[INFO] ReadPost called for id: " << id << std::endl;

    try
    {
        PostResponseTo dto = m_service->Read(id);
        Json::Value jsonResponse = dto.toJson();
        
        std::string responseBody = Json::FastWriter().write(jsonResponse);
        std::cout << "[RESPONSE] " << responseBody << std::endl;
        
        httpResponse->setContentTypeCode(ContentType::CT_APPLICATION_JSON);
        httpResponse->setBody(responseBody);
        httpResponse->setStatusCode(HttpStatusCode::k200OK);
        std::cout << "[INFO] Post retrieved successfully" << std::endl;
    }
    catch(const ValidationException& e)
    {
        std::cout << "[ERROR] Validation error: " << e.what() << std::endl;
        Json::Value errorResponse;
        errorResponse["message"] = e.what();
        httpResponse->setBody(Json::FastWriter().write(errorResponse));
        httpResponse->setContentTypeCode(ContentType::CT_APPLICATION_JSON);
        httpResponse->setStatusCode(HttpStatusCode::k400BadRequest);
    }
    catch(const NotFoundException& e)
    {
        std::cout << "[ERROR] Post not found: " << e.what() << std::endl;
        Json::Value errorResponse;
        errorResponse["message"] = e.what();
        httpResponse->setBody(Json::FastWriter().write(errorResponse));
        httpResponse->setContentTypeCode(ContentType::CT_APPLICATION_JSON);
        httpResponse->setStatusCode(HttpStatusCode::k404NotFound);
    }
    catch(const DatabaseException& e)
    {
        std::cout << "[ERROR] Database error: " << e.what() << std::endl;
        Json::Value errorResponse;
        errorResponse["message"] = "Database error occurred";
        httpResponse->setBody(Json::FastWriter().write(errorResponse));
        httpResponse->setContentTypeCode(ContentType::CT_APPLICATION_JSON);
        httpResponse->setStatusCode(HttpStatusCode::k500InternalServerError);
    }
    catch(const std::exception& e)
    {
        std::cout << "[ERROR] Unknown error: " << e.what() << std::endl;
        Json::Value errorResponse;
        errorResponse["message"] = "Internal server error";
        httpResponse->setBody(Json::FastWriter().write(errorResponse));
        httpResponse->setContentTypeCode(ContentType::CT_APPLICATION_JSON);
        httpResponse->setStatusCode(HttpStatusCode::k500InternalServerError);
    }
    
    callback(httpResponse);
}

void PostController::UpdatePostIdFromRoute(const HttpRequestPtr& req, std::function<void(const HttpResponsePtr&)>&& callback, uint64_t id)
{
    HttpResponsePtr httpResponse = HttpResponse::newHttpResponse();
    std::cout << "[INFO] UpdatePostWithId called for id: " << id << std::endl;

    try
    {
        auto jsonFromRequest = req->getJsonObject();
        if (!jsonFromRequest)
        {
            std::cout << "[ERROR] Invalid JSON in UpdatePostWithId" << std::endl;
            Json::Value errorResponse;
            errorResponse["message"] = "Invalid JSON format";
            httpResponse->setBody(Json::FastWriter().write(errorResponse));
            httpResponse->setContentTypeCode(ContentType::CT_APPLICATION_JSON);
            httpResponse->setStatusCode(HttpStatusCode::k400BadRequest);
            callback(httpResponse);
            return;
        }

        auto requestDto = PostRequestTo::fromJson(*jsonFromRequest);
        PostResponseTo dto = m_service->Update(requestDto, id);
        
        Json::Value jsonResponse = dto.toJson();
        std::string responseBody = Json::FastWriter().write(jsonResponse);
        std::cout << "[RESPONSE] " << responseBody << std::endl;
        
        httpResponse->setContentTypeCode(ContentType::CT_APPLICATION_JSON);
        httpResponse->setBody(responseBody);
        httpResponse->setStatusCode(HttpStatusCode::k200OK);
        std::cout << "[INFO] Post updated successfully" << std::endl;
    }
    catch(const ValidationException& e)
    {
        std::cout << "[ERROR] Validation error: " << e.what() << std::endl;
        Json::Value errorResponse;
        errorResponse["message"] = e.what();
        httpResponse->setBody(Json::FastWriter().write(errorResponse));
        httpResponse->setContentTypeCode(ContentType::CT_APPLICATION_JSON);
        httpResponse->setStatusCode(HttpStatusCode::k400BadRequest);
    }
    catch(const NotFoundException& e)
    {
        std::cout << "[ERROR] Post not found for update: " << e.what() << std::endl;
        Json::Value errorResponse;
        errorResponse["message"] = e.what();
        httpResponse->setBody(Json::FastWriter().write(errorResponse));
        httpResponse->setContentTypeCode(ContentType::CT_APPLICATION_JSON);
        httpResponse->setStatusCode(HttpStatusCode::k404NotFound);
    }
    catch(const DatabaseException& e)
    {
        std::cout << "[ERROR] Database error: " << e.what() << std::endl;
        Json::Value errorResponse;
        errorResponse["message"] = "Database error occurred";
        httpResponse->setBody(Json::FastWriter().write(errorResponse));
        httpResponse->setContentTypeCode(ContentType::CT_APPLICATION_JSON);
        httpResponse->setStatusCode(HttpStatusCode::k500InternalServerError);
    }
    catch(const std::exception& e)
    {
        std::cout << "[ERROR] Unknown error: " << e.what() << std::endl;
        Json::Value errorResponse;
        errorResponse["message"] = "Internal server error";
        httpResponse->setBody(Json::FastWriter().write(errorResponse));
        httpResponse->setContentTypeCode(ContentType::CT_APPLICATION_JSON);
        httpResponse->setStatusCode(HttpStatusCode::k500InternalServerError);
    }
    
    callback(httpResponse);
}

void PostController::UpdatePostIdFromBody(const HttpRequestPtr& req, std::function<void(const HttpResponsePtr&)>&& callback)
{
    HttpResponsePtr httpResponse = HttpResponse::newHttpResponse();
    std::cout << "[INFO] UpdatePost called (without ID in path)" << std::endl;

    try
    {
        auto jsonFromRequest = req->getJsonObject();
        if (!jsonFromRequest)
        {
            std::cout << "[ERROR] Invalid JSON in UpdatePost" << std::endl;
            Json::Value errorResponse;
            errorResponse["message"] = "Invalid JSON format";
            httpResponse->setBody(Json::FastWriter().write(errorResponse));
            httpResponse->setContentTypeCode(ContentType::CT_APPLICATION_JSON);
            httpResponse->setStatusCode(HttpStatusCode::k400BadRequest);
            callback(httpResponse);
            return;
        }

        auto requestDto = PostRequestTo::fromJson(*jsonFromRequest);
        if (!requestDto.id.has_value())
        {
            std::cout << "[ERROR] No ID in JSON" << std::endl;
            Json::Value errorResponse;
            errorResponse["message"] = "ID is required in request body";
            httpResponse->setBody(Json::FastWriter().write(errorResponse));
            httpResponse->setContentTypeCode(ContentType::CT_APPLICATION_JSON);
            httpResponse->setStatusCode(HttpStatusCode::k400BadRequest);
            callback(httpResponse);
            return;
        }

        PostResponseTo dto = m_service->Update(requestDto, requestDto.id.value());
        
        Json::Value jsonResponse = dto.toJson();
        std::string responseBody = Json::FastWriter().write(jsonResponse);
        std::cout << "[RESPONSE] " << responseBody << std::endl;
        
        httpResponse->setContentTypeCode(ContentType::CT_APPLICATION_JSON);
        httpResponse->setBody(responseBody);
        httpResponse->setStatusCode(HttpStatusCode::k200OK);
        std::cout << "[INFO] Post updated successfully" << std::endl;
    }
    catch(const ValidationException& e)
    {
        std::cout << "[ERROR] Validation error: " << e.what() << std::endl;
        Json::Value errorResponse;
        errorResponse["message"] = e.what();
        httpResponse->setBody(Json::FastWriter().write(errorResponse));
        httpResponse->setContentTypeCode(ContentType::CT_APPLICATION_JSON);
        httpResponse->setStatusCode(HttpStatusCode::k400BadRequest);
    }
    catch(const NotFoundException& e)
    {
        std::cout << "[ERROR] Post not found for update: " << e.what() << std::endl;
        Json::Value errorResponse;
        errorResponse["message"] = e.what();
        httpResponse->setBody(Json::FastWriter().write(errorResponse));
        httpResponse->setContentTypeCode(ContentType::CT_APPLICATION_JSON);
        httpResponse->setStatusCode(HttpStatusCode::k404NotFound);
    }
    catch(const DatabaseException& e)
    {
        std::cout << "[ERROR] Database error: " << e.what() << std::endl;
        Json::Value errorResponse;
        errorResponse["message"] = "Database error occurred";
        httpResponse->setBody(Json::FastWriter().write(errorResponse));
        httpResponse->setContentTypeCode(ContentType::CT_APPLICATION_JSON);
        httpResponse->setStatusCode(HttpStatusCode::k500InternalServerError);
    }
    catch(const std::exception& e)
    {
        std::cout << "[ERROR] Unknown error: " << e.what() << std::endl;
        Json::Value errorResponse;
        errorResponse["message"] = "Internal server error";
        httpResponse->setBody(Json::FastWriter().write(errorResponse));
        httpResponse->setContentTypeCode(ContentType::CT_APPLICATION_JSON);
        httpResponse->setStatusCode(HttpStatusCode::k500InternalServerError);
    }
    
    callback(httpResponse);
}

void PostController::DeletePost(const HttpRequestPtr& req, std::function<void(const HttpResponsePtr&)>&& callback, uint64_t id)
{
    HttpResponsePtr httpResponse = HttpResponse::newHttpResponse();
    std::cout << "[INFO] DeletePost called for id: " << id << std::endl;

    try
    {
        if (m_service->Delete(id))
        {
            httpResponse->setStatusCode(HttpStatusCode::k204NoContent);
            std::cout << "[INFO] Post deleted successfully" << std::endl;
            std::cout << "[RESPONSE] No content (204)" << std::endl;
        }
        else
        {
            std::cout << "[ERROR] Post not found for deletion" << std::endl;
            Json::Value errorResponse;
            errorResponse["message"] = "Post not found";
            httpResponse->setBody(Json::FastWriter().write(errorResponse));
            httpResponse->setContentTypeCode(ContentType::CT_APPLICATION_JSON);
            httpResponse->setStatusCode(HttpStatusCode::k404NotFound);
        }
    }
    catch(const ValidationException& e)
    {
        std::cout << "[ERROR] Validation error: " << e.what() << std::endl;
        Json::Value errorResponse;
        errorResponse["message"] = e.what();
        httpResponse->setBody(Json::FastWriter().write(errorResponse));
        httpResponse->setContentTypeCode(ContentType::CT_APPLICATION_JSON);
        httpResponse->setStatusCode(HttpStatusCode::k400BadRequest);
    }
    catch(const NotFoundException& e)
    {
        std::cout << "[ERROR] Post not found: " << e.what() << std::endl;
        Json::Value errorResponse;
        errorResponse["message"] = e.what();
        httpResponse->setBody(Json::FastWriter().write(errorResponse));
        httpResponse->setContentTypeCode(ContentType::CT_APPLICATION_JSON);
        httpResponse->setStatusCode(HttpStatusCode::k404NotFound);
    }
    catch(const DatabaseException& e)
    {
        std::cout << "[ERROR] Database error: " << e.what() << std::endl;
        Json::Value errorResponse;
        errorResponse["message"] = "Database error occurred";
        httpResponse->setBody(Json::FastWriter().write(errorResponse));
        httpResponse->setContentTypeCode(ContentType::CT_APPLICATION_JSON);
        httpResponse->setStatusCode(HttpStatusCode::k500InternalServerError);
    }
    catch(const std::exception& e)
    {
        std::cout << "[ERROR] Unknown error: " << e.what() << std::endl;
        Json::Value errorResponse;
        errorResponse["message"] = "Internal server error";
        httpResponse->setBody(Json::FastWriter().write(errorResponse));
        httpResponse->setContentTypeCode(ContentType::CT_APPLICATION_JSON);
        httpResponse->setStatusCode(HttpStatusCode::k500InternalServerError);
    }
    
    callback(httpResponse);
}

void PostController::GetAllPosts(const HttpRequestPtr& req, std::function<void(const HttpResponsePtr&)>&& callback)
{
    HttpResponsePtr httpResponse = HttpResponse::newHttpResponse();
    std::cout << "[INFO] GetAllPosts called" << std::endl;

    try
    {
        std::vector<PostResponseTo> dtos = m_service->GetAll();
        Json::Value jsonResponse(Json::arrayValue);
        for (auto& dto: dtos)
        {
            jsonResponse.append(dto.toJson());
        }
        
        std::string responseBody = Json::FastWriter().write(jsonResponse);
        std::cout << "[RESPONSE] " << responseBody << std::endl;
            
        httpResponse->setContentTypeCode(ContentType::CT_APPLICATION_JSON);
        httpResponse->setBody(responseBody);
        httpResponse->setStatusCode(HttpStatusCode::k200OK);
        std::cout << "[INFO] Retrieved " << dtos.size() << " posts" << std::endl;
    }
    catch(const ValidationException& e)
    {
        std::cout << "[ERROR] Validation error: " << e.what() << std::endl;
        Json::Value errorResponse;
        errorResponse["message"] = e.what();
        httpResponse->setBody(Json::FastWriter().write(errorResponse));
        httpResponse->setContentTypeCode(ContentType::CT_APPLICATION_JSON);
        httpResponse->setStatusCode(HttpStatusCode::k400BadRequest);
    }
    catch(const NotFoundException& e)
    {
        std::cout << "[ERROR] Not found: " << e.what() << std::endl;
        Json::Value errorResponse;
        errorResponse["message"] = e.what();
        httpResponse->setBody(Json::FastWriter().write(errorResponse));
        httpResponse->setContentTypeCode(ContentType::CT_APPLICATION_JSON);
        httpResponse->setStatusCode(HttpStatusCode::k404NotFound);
    }
    catch(const DatabaseException& e)
    {
        std::cout << "[ERROR] Database error: " << e.what() << std::endl;
        Json::Value errorResponse;
        errorResponse["message"] = "Database error occurred";
        httpResponse->setBody(Json::FastWriter().write(errorResponse));
        httpResponse->setContentTypeCode(ContentType::CT_APPLICATION_JSON);
        httpResponse->setStatusCode(HttpStatusCode::k500InternalServerError);
    }
    catch(const std::exception& e)
    {
        std::cout << "[ERROR] Unknown error: " << e.what() << std::endl;
        Json::Value errorResponse;
        errorResponse["message"] = "Internal server error";
        httpResponse->setBody(Json::FastWriter().write(errorResponse));
        httpResponse->setContentTypeCode(ContentType::CT_APPLICATION_JSON);
        httpResponse->setStatusCode(HttpStatusCode::k500InternalServerError);
    }
    
    callback(httpResponse);
}