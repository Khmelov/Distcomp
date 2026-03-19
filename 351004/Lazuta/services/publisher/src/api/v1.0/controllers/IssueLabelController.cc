#include "IssueLabelController.h"
#include <iostream>

using namespace publisher;
using namespace publisher::dto;

IssueLabelController::IssueLabelController(std::unique_ptr<IssueLabelService> service)
{
    m_service = std::move(service);
    std::cout << "[INFO] IssueLabelController initialized" << std::endl;
}

void IssueLabelController::CreateIssueLabel(const HttpRequestPtr& req, std::function<void(const HttpResponsePtr&)>&& callback)
{
    HttpResponsePtr httpResponse = HttpResponse::newHttpResponse();
    std::cout << "[INFO] CreateIssueLabel called" << std::endl;

    try
    {
        auto jsonFromRequest = req->getJsonObject();
        if (!jsonFromRequest)
        {
            std::cout << "[ERROR] Invalid JSON in CreateIssueLabel" << std::endl;
            Json::Value errorResponse;
            errorResponse["message"] = "Invalid JSON format";
            httpResponse->setBody(Json::FastWriter().write(errorResponse));
            httpResponse->setContentTypeCode(ContentType::CT_APPLICATION_JSON);
            httpResponse->setStatusCode(HttpStatusCode::k400BadRequest);
            callback(httpResponse);
            return;
        }

        IssueLabelResponseTo dto = m_service->Create(IssueLabelRequestTo::fromJson(*jsonFromRequest));
        Json::Value jsonResponse = dto.toJson();
        
        std::string responseBody = Json::FastWriter().write(jsonResponse);
        std::cout << "[RESPONSE] " << responseBody << std::endl;
        
        httpResponse->setContentTypeCode(ContentType::CT_APPLICATION_JSON);
        httpResponse->setBody(responseBody);
        httpResponse->setStatusCode(HttpStatusCode::k201Created);
        std::cout << "[INFO] IssueLabel created successfully" << std::endl;
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

void IssueLabelController::ReadIssueLabel(const HttpRequestPtr& req, std::function<void(const HttpResponsePtr&)>&& callback, uint64_t id)
{
    HttpResponsePtr httpResponse = HttpResponse::newHttpResponse();
    std::cout << "[INFO] ReadIssueLabel called for id: " << id << std::endl;

    try
    {
        IssueLabelResponseTo dto = m_service->Read(id);
        Json::Value jsonResponse = dto.toJson();
        
        std::string responseBody = Json::FastWriter().write(jsonResponse);
        std::cout << "[RESPONSE] " << responseBody << std::endl;
        
        httpResponse->setContentTypeCode(ContentType::CT_APPLICATION_JSON);
        httpResponse->setBody(responseBody);
        httpResponse->setStatusCode(HttpStatusCode::k200OK);
        std::cout << "[INFO] IssueLabel retrieved successfully" << std::endl;
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
        std::cout << "[ERROR] IssueLabel not found: " << e.what() << std::endl;
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

void IssueLabelController::UpdateIssueLabelIdFromRoute(const HttpRequestPtr& req, std::function<void(const HttpResponsePtr&)>&& callback, uint64_t id)
{
    HttpResponsePtr httpResponse = HttpResponse::newHttpResponse();
    std::cout << "[INFO] UpdateIssueLabelWithId called for id: " << id << std::endl;

    try
    {
        auto jsonFromRequest = req->getJsonObject();
        if (!jsonFromRequest)
        {
            std::cout << "[ERROR] Invalid JSON in UpdateIssueLabelWithId" << std::endl;
            Json::Value errorResponse;
            errorResponse["message"] = "Invalid JSON format";
            httpResponse->setBody(Json::FastWriter().write(errorResponse));
            httpResponse->setContentTypeCode(ContentType::CT_APPLICATION_JSON);
            httpResponse->setStatusCode(HttpStatusCode::k400BadRequest);
            callback(httpResponse);
            return;
        }

        auto requestDto = IssueLabelRequestTo::fromJson(*jsonFromRequest);
        IssueLabelResponseTo dto = m_service->Update(requestDto, id);
        
        Json::Value jsonResponse = dto.toJson();
        std::string responseBody = Json::FastWriter().write(jsonResponse);
        std::cout << "[RESPONSE] " << responseBody << std::endl;
        
        httpResponse->setContentTypeCode(ContentType::CT_APPLICATION_JSON);
        httpResponse->setBody(responseBody);
        httpResponse->setStatusCode(HttpStatusCode::k200OK);
        std::cout << "[INFO] IssueLabel updated successfully" << std::endl;
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
        std::cout << "[ERROR] IssueLabel not found for update: " << e.what() << std::endl;
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

void IssueLabelController::UpdateIssueLabelIdFromBody(const HttpRequestPtr& req, std::function<void(const HttpResponsePtr&)>&& callback)
{
    HttpResponsePtr httpResponse = HttpResponse::newHttpResponse();
    std::cout << "[INFO] UpdateIssueLabel called (without ID in path)" << std::endl;

    try
    {
        auto jsonFromRequest = req->getJsonObject();
        if (!jsonFromRequest)
        {
            std::cout << "[ERROR] Invalid JSON in UpdateIssueLabel" << std::endl;
            Json::Value errorResponse;
            errorResponse["message"] = "Invalid JSON format";
            httpResponse->setBody(Json::FastWriter().write(errorResponse));
            httpResponse->setContentTypeCode(ContentType::CT_APPLICATION_JSON);
            httpResponse->setStatusCode(HttpStatusCode::k400BadRequest);
            callback(httpResponse);
            return;
        }

        auto requestDto = IssueLabelRequestTo::fromJson(*jsonFromRequest);
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

        IssueLabelResponseTo dto = m_service->Update(requestDto, requestDto.id.value());
        
        Json::Value jsonResponse = dto.toJson();
        std::string responseBody = Json::FastWriter().write(jsonResponse);
        std::cout << "[RESPONSE] " << responseBody << std::endl;
        
        httpResponse->setContentTypeCode(ContentType::CT_APPLICATION_JSON);
        httpResponse->setBody(responseBody);
        httpResponse->setStatusCode(HttpStatusCode::k200OK);
        std::cout << "[INFO] IssueLabel updated successfully" << std::endl;
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
        std::cout << "[ERROR] IssueLabel not found for update: " << e.what() << std::endl;
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

void IssueLabelController::DeleteIssueLabel(const HttpRequestPtr& req, std::function<void(const HttpResponsePtr&)>&& callback, uint64_t id)
{
    HttpResponsePtr httpResponse = HttpResponse::newHttpResponse();
    std::cout << "[INFO] DeleteIssueLabel called for id: " << id << std::endl;

    try
    {
        if (m_service->Delete(id))
        {
            httpResponse->setStatusCode(HttpStatusCode::k204NoContent);
            std::cout << "[INFO] IssueLabel deleted successfully" << std::endl;
            std::cout << "[RESPONSE] No content (204)" << std::endl;
        }
        else
        {
            std::cout << "[ERROR] IssueLabel not found for deletion" << std::endl;
            Json::Value errorResponse;
            errorResponse["message"] = "IssueLabel not found";
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
        std::cout << "[ERROR] IssueLabel not found: " << e.what() << std::endl;
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

void IssueLabelController::GetAllIssueLabels(const HttpRequestPtr& req, std::function<void(const HttpResponsePtr&)>&& callback)
{
    HttpResponsePtr httpResponse = HttpResponse::newHttpResponse();
    std::cout << "[INFO] GetAllIssueLabels called" << std::endl;

    try
    {
        std::vector<IssueLabelResponseTo> dtos = m_service->GetAll();
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
        std::cout << "[INFO] Retrieved " << dtos.size() << " issue labels" << std::endl;
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

void IssueLabelController::GetByIssueId(const HttpRequestPtr& req, std::function<void(const HttpResponsePtr&)>&& callback, uint64_t issueId)
{
    HttpResponsePtr httpResponse = HttpResponse::newHttpResponse();
    std::cout << "[INFO] GetByIssueId called for issueId: " << issueId << std::endl;

    try
    {
        std::vector<IssueLabelResponseTo> dtos = m_service->GetByIssueId(issueId);
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
        std::cout << "[INFO] Retrieved " << dtos.size() << " issue labels for issue " << issueId << std::endl;
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

void IssueLabelController::GetByLabelId(const HttpRequestPtr& req, std::function<void(const HttpResponsePtr&)>&& callback, uint64_t labelId)
{
    HttpResponsePtr httpResponse = HttpResponse::newHttpResponse();
    std::cout << "[INFO] GetByLabelId called for labelId: " << labelId << std::endl;

    try
    {
        std::vector<IssueLabelResponseTo> dtos = m_service->GetByLabelId(labelId);
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
        std::cout << "[INFO] Retrieved " << dtos.size() << " issue labels for label " << labelId << std::endl;
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

void IssueLabelController::DeleteByIssueAndLabel(const HttpRequestPtr& req, std::function<void(const HttpResponsePtr&)>&& callback, uint64_t issueId, uint64_t labelId)
{
    HttpResponsePtr httpResponse = HttpResponse::newHttpResponse();
    std::cout << "[INFO] DeleteByIssueAndLabel called for issueId: " << issueId << ", labelId: " << labelId << std::endl;

    try
    {
        if (m_service->DeleteByIssueAndLabel(issueId, labelId))
        {
            httpResponse->setStatusCode(HttpStatusCode::k204NoContent);
            std::cout << "[INFO] IssueLabel combination deleted successfully" << std::endl;
            std::cout << "[RESPONSE] No content (204)" << std::endl;
        }
        else
        {
            std::cout << "[ERROR] IssueLabel combination not found for deletion" << std::endl;
            Json::Value errorResponse;
            errorResponse["message"] = "IssueLabel combination not found";
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
        std::cout << "[ERROR] IssueLabel combination not found: " << e.what() << std::endl;
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