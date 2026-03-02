#include "EditorController.h"
#include <iostream>

EditorController::EditorController(std::unique_ptr<EditorService> service)
{
    m_service = std::move(service);
    std::cout << "[INFO] EditorController initialized" << std::endl;
}

void EditorController::CreateEditor(const HttpRequestPtr& req, std::function<void(const HttpResponsePtr&)>&& callback)
{
    HttpResponsePtr httpResponse = HttpResponse::newHttpResponse();
    std::cout << "[INFO] CreateEditor called" << std::endl;

    try
    {           
        auto jsonFromRequest = req->getJsonObject();
        if (!jsonFromRequest)
        {
            std::cout << "[ERROR] Invalid JSON in CreateEditor" << std::endl;
            Json::Value errorResponse;
            errorResponse["message"] = "Invalid JSON format";
            httpResponse->setBody(Json::FastWriter().write(errorResponse));
            httpResponse->setContentTypeCode(ContentType::CT_APPLICATION_JSON);
            httpResponse->setStatusCode(HttpStatusCode::k400BadRequest);
            callback(httpResponse);
            return;
        }

        EditorResponseTo dto = m_service->Create(EditorRequestTo::fromJson(*jsonFromRequest));
        Json::Value jsonResponse = dto.toJson();
        
        std::string responseBody = Json::FastWriter().write(jsonResponse);
        std::cout << "[RESPONSE] " << responseBody << std::endl;
        
        httpResponse->setContentTypeCode(ContentType::CT_APPLICATION_JSON);
        httpResponse->setBody(responseBody);
        httpResponse->setStatusCode(HttpStatusCode::k201Created);
        std::cout << "[INFO] Editor created successfully" << std::endl;
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

void EditorController::ReadEditor(const HttpRequestPtr& req, std::function<void(const HttpResponsePtr&)>&& callback, uint64_t id)
{
    HttpResponsePtr httpResponse = HttpResponse::newHttpResponse();
    std::cout << "[INFO] ReadEditor called for id: " << id << std::endl;

    try
    {           
        EditorResponseTo dto = m_service->Read(id);
        Json::Value jsonResponse = dto.toJson();
        
        std::string responseBody = Json::FastWriter().write(jsonResponse);
        std::cout << "[RESPONSE] " << responseBody << std::endl;
        
        httpResponse->setContentTypeCode(ContentType::CT_APPLICATION_JSON);
        httpResponse->setBody(responseBody);
        httpResponse->setStatusCode(HttpStatusCode::k200OK);
        std::cout << "[INFO] Editor retrieved successfully" << std::endl;
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
        std::cout << "[ERROR] Editor not found: " << e.what() << std::endl;
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

void EditorController::UpdateEditorIdFromRoute(const HttpRequestPtr& req, std::function<void(const HttpResponsePtr&)>&& callback, uint64_t id)
{
    HttpResponsePtr httpResponse = HttpResponse::newHttpResponse();
    std::cout << "[INFO] UpdateEditorWithId called for id: " << id << std::endl;

    try
    {           
        auto jsonFromRequest = req->getJsonObject();
        if (!jsonFromRequest)
        {
            std::cout << "[ERROR] Invalid JSON in UpdateEditorWithId" << std::endl;
            Json::Value errorResponse;
            errorResponse["message"] = "Invalid JSON format";
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
        std::cout << "[RESPONSE] " << responseBody << std::endl;
        
        httpResponse->setContentTypeCode(ContentType::CT_APPLICATION_JSON);
        httpResponse->setBody(responseBody);
        httpResponse->setStatusCode(HttpStatusCode::k200OK);
        std::cout << "[INFO] Editor updated successfully" << std::endl;
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
        std::cout << "[ERROR] Editor not found for update: " << e.what() << std::endl;
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

void EditorController::UpdateEditorIdFromBody(const HttpRequestPtr& req, std::function<void(const HttpResponsePtr&)>&& callback)
{
    HttpResponsePtr httpResponse = HttpResponse::newHttpResponse();
    std::cout << "[INFO] UpdateEditor called (without ID in path)" << std::endl;

    try
    {           
        auto jsonFromRequest = req->getJsonObject();
        if (!jsonFromRequest)
        {
            std::cout << "[ERROR] Invalid JSON in UpdateEditor" << std::endl;
            Json::Value errorResponse;
            errorResponse["message"] = "Invalid JSON format";
            httpResponse->setBody(Json::FastWriter().write(errorResponse));
            httpResponse->setContentTypeCode(ContentType::CT_APPLICATION_JSON);
            httpResponse->setStatusCode(HttpStatusCode::k400BadRequest);
            callback(httpResponse);
            return;
        }

        auto requestDto = EditorRequestTo::fromJson(*jsonFromRequest);
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

        EditorResponseTo dto = m_service->Update(requestDto, requestDto.id.value());
        
        Json::Value jsonResponse = dto.toJson();
        std::string responseBody = Json::FastWriter().write(jsonResponse);
        std::cout << "[RESPONSE] " << responseBody << std::endl;
        
        httpResponse->setContentTypeCode(ContentType::CT_APPLICATION_JSON);
        httpResponse->setBody(responseBody);
        httpResponse->setStatusCode(HttpStatusCode::k200OK);
        std::cout << "[INFO] Editor updated successfully" << std::endl;
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
        std::cout << "[ERROR] Editor not found for update: " << e.what() << std::endl;
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

void EditorController::DeleteEditor(const HttpRequestPtr& req, std::function<void(const HttpResponsePtr&)>&& callback, uint64_t id)
{
    HttpResponsePtr httpResponse = HttpResponse::newHttpResponse();
    std::cout << "[INFO] DeleteEditor called for id: " << id << std::endl;

    try
    {           
        if (m_service->Delete(id))
        {
            httpResponse->setStatusCode(HttpStatusCode::k204NoContent);
            std::cout << "[INFO] Editor deleted successfully" << std::endl;
            std::cout << "[RESPONSE] No content (204)" << std::endl;
        }
        else
        {
            std::cout << "[ERROR] Editor not found for deletion" << std::endl;
            Json::Value errorResponse;
            errorResponse["message"] = "Editor not found";
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
        std::cout << "[ERROR] Editor not found: " << e.what() << std::endl;
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

void EditorController::GetAllEditors(const HttpRequestPtr& req, std::function<void(const HttpResponsePtr&)>&& callback)
{
    HttpResponsePtr httpResponse = HttpResponse::newHttpResponse();
    std::cout << "[INFO] GetAllEditors called" << std::endl;

    try
    {           
        std::vector<EditorResponseTo> dtos = m_service->GetAll();
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
        std::cout << "[INFO] Retrieved " << dtos.size() << " editors" << std::endl;
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