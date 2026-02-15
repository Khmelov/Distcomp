// LabelController.cc
#include "LabelController.h"
#include <iostream>

LabelController::LabelController(std::unique_ptr<LabelService> service)
{
    m_service = std::move(service);
    std::cout << "[INFO] LabelController initialized" << std::endl;
}

void LabelController::CreateLabel(const HttpRequestPtr& req, std::function<void(const HttpResponsePtr&)>&& callback)
{
    HttpResponsePtr httpResponse = HttpResponse::newHttpResponse();
    std::cout << "[INFO] CreateLabel called" << std::endl;

    try
    {
        auto jsonFromRequest = req->getJsonObject();
        if (!jsonFromRequest)
        {
            std::cout << "[ERROR] Invalid JSON in CreateLabel" << std::endl;
            Json::Value errorResponse;
            errorResponse["message"] = "Invalid JSON format";
            httpResponse->setBody(Json::FastWriter().write(errorResponse));
            httpResponse->setContentTypeCode(ContentType::CT_APPLICATION_JSON);
            httpResponse->setStatusCode(HttpStatusCode::k400BadRequest);
            callback(httpResponse);
            return;
        }

        LabelResponseTo dto = m_service->Create(LabelRequestTo::fromJson(*jsonFromRequest));
        Json::Value jsonResponse = dto.toJson();
        
        std::string responseBody = Json::FastWriter().write(jsonResponse);
        std::cout << "[RESPONSE] " << responseBody << std::endl;
        
        httpResponse->setContentTypeCode(ContentType::CT_APPLICATION_JSON);
        httpResponse->setBody(responseBody);
        httpResponse->setStatusCode(HttpStatusCode::k201Created);
        std::cout << "[INFO] Label created successfully" << std::endl;
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

void LabelController::ReadLabel(const HttpRequestPtr& req, std::function<void(const HttpResponsePtr&)>&& callback, uint64_t id)
{
    HttpResponsePtr httpResponse = HttpResponse::newHttpResponse();
    std::cout << "[INFO] ReadLabel called for id: " << id << std::endl;

    try
    {
        LabelResponseTo dto = m_service->Read(id);
        Json::Value jsonResponse = dto.toJson();
        
        std::string responseBody = Json::FastWriter().write(jsonResponse);
        std::cout << "[RESPONSE] " << responseBody << std::endl;
        
        httpResponse->setContentTypeCode(ContentType::CT_APPLICATION_JSON);
        httpResponse->setBody(responseBody);
        httpResponse->setStatusCode(HttpStatusCode::k200OK);
        std::cout << "[INFO] Label retrieved successfully" << std::endl;
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
        std::cout << "[ERROR] Label not found: " << e.what() << std::endl;
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

void LabelController::UpdateLabelIdFromRoute(const HttpRequestPtr& req, std::function<void(const HttpResponsePtr&)>&& callback, uint64_t id)
{
    HttpResponsePtr httpResponse = HttpResponse::newHttpResponse();
    std::cout << "[INFO] UpdateLabelWithId called for id: " << id << std::endl;

    try
    {
        auto jsonFromRequest = req->getJsonObject();
        if (!jsonFromRequest)
        {
            std::cout << "[ERROR] Invalid JSON in UpdateLabelWithId" << std::endl;
            Json::Value errorResponse;
            errorResponse["message"] = "Invalid JSON format";
            httpResponse->setBody(Json::FastWriter().write(errorResponse));
            httpResponse->setContentTypeCode(ContentType::CT_APPLICATION_JSON);
            httpResponse->setStatusCode(HttpStatusCode::k400BadRequest);
            callback(httpResponse);
            return;
        }

        auto requestDto = LabelRequestTo::fromJson(*jsonFromRequest);
        LabelResponseTo dto = m_service->Update(requestDto, id);
        
        Json::Value jsonResponse = dto.toJson();
        std::string responseBody = Json::FastWriter().write(jsonResponse);
        std::cout << "[RESPONSE] " << responseBody << std::endl;
        
        httpResponse->setContentTypeCode(ContentType::CT_APPLICATION_JSON);
        httpResponse->setBody(responseBody);
        httpResponse->setStatusCode(HttpStatusCode::k200OK);
        std::cout << "[INFO] Label updated successfully" << std::endl;
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
        std::cout << "[ERROR] Label not found for update: " << e.what() << std::endl;
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

void LabelController::UpdateLabelIdFromBody(const HttpRequestPtr& req, std::function<void(const HttpResponsePtr&)>&& callback)
{
    HttpResponsePtr httpResponse = HttpResponse::newHttpResponse();
    std::cout << "[INFO] UpdateLabel called (without ID in path)" << std::endl;

    try
    {
        auto jsonFromRequest = req->getJsonObject();
        if (!jsonFromRequest)
        {
            std::cout << "[ERROR] Invalid JSON in UpdateLabel" << std::endl;
            Json::Value errorResponse;
            errorResponse["message"] = "Invalid JSON format";
            httpResponse->setBody(Json::FastWriter().write(errorResponse));
            httpResponse->setContentTypeCode(ContentType::CT_APPLICATION_JSON);
            httpResponse->setStatusCode(HttpStatusCode::k400BadRequest);
            callback(httpResponse);
            return;
        }

        auto requestDto = LabelRequestTo::fromJson(*jsonFromRequest);
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

        LabelResponseTo dto = m_service->Update(requestDto, requestDto.id.value());
        
        Json::Value jsonResponse = dto.toJson();
        std::string responseBody = Json::FastWriter().write(jsonResponse);
        std::cout << "[RESPONSE] " << responseBody << std::endl;
        
        httpResponse->setContentTypeCode(ContentType::CT_APPLICATION_JSON);
        httpResponse->setBody(responseBody);
        httpResponse->setStatusCode(HttpStatusCode::k200OK);
        std::cout << "[INFO] Label updated successfully" << std::endl;
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
        std::cout << "[ERROR] Label not found for update: " << e.what() << std::endl;
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

void LabelController::DeleteLabel(const HttpRequestPtr& req, std::function<void(const HttpResponsePtr&)>&& callback, uint64_t id)
{
    HttpResponsePtr httpResponse = HttpResponse::newHttpResponse();
    std::cout << "[INFO] DeleteLabel called for id: " << id << std::endl;

    try
    {
        if (m_service->Delete(id))
        {
            httpResponse->setStatusCode(HttpStatusCode::k204NoContent);
            std::cout << "[INFO] Label deleted successfully" << std::endl;
            std::cout << "[RESPONSE] No content (204)" << std::endl;
        }
        else
        {
            std::cout << "[ERROR] Label not found for deletion" << std::endl;
            Json::Value errorResponse;
            errorResponse["message"] = "Label not found";
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
        std::cout << "[ERROR] Label not found: " << e.what() << std::endl;
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

void LabelController::GetAllLabels(const HttpRequestPtr& req, std::function<void(const HttpResponsePtr&)>&& callback)
{
    HttpResponsePtr httpResponse = HttpResponse::newHttpResponse();
    std::cout << "[INFO] GetAllLabels called" << std::endl;

    try
    {
        std::vector<LabelResponseTo> dtos = m_service->GetAll();
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
        std::cout << "[INFO] Retrieved " << dtos.size() << " labels" << std::endl;
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