// IssueController.cc
#include "IssueController.h"
#include <iostream>

IssueController::IssueController(std::unique_ptr<IssueService> service)
{
    m_service = std::move(service);
    std::cout << "[INFO] IssueController initialized" << std::endl;
}

void IssueController::CreateIssue(const HttpRequestPtr& req, std::function<void(const HttpResponsePtr&)>&& callback)
{
    HttpResponsePtr httpResponse = HttpResponse::newHttpResponse();
    std::cout << "[INFO] CreateIssue called" << std::endl;

    try
    {
        auto jsonFromRequest = req->getJsonObject();
        if (!jsonFromRequest)
        {
            std::cout << "[ERROR] Invalid JSON in CreateIssue" << std::endl;
            Json::Value errorResponse;
            errorResponse["message"] = "Invalid JSON format";
            httpResponse->setBody(Json::FastWriter().write(errorResponse));
            httpResponse->setContentTypeCode(ContentType::CT_APPLICATION_JSON);
            httpResponse->setStatusCode(HttpStatusCode::k400BadRequest);
            callback(httpResponse);
            return;
        }

        IssueResponseTo dto = m_service->Create(IssueRequestTo::fromJson(*jsonFromRequest));
        Json::Value jsonResponse = dto.toJson();
        
        std::string responseBody = Json::FastWriter().write(jsonResponse);
        std::cout << "[RESPONSE] " << responseBody << std::endl;
        
        httpResponse->setContentTypeCode(ContentType::CT_APPLICATION_JSON);
        httpResponse->setBody(responseBody);
        httpResponse->setStatusCode(HttpStatusCode::k201Created);
        std::cout << "[INFO] Issue created successfully" << std::endl;
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

void IssueController::ReadIssue(const HttpRequestPtr& req, std::function<void(const HttpResponsePtr&)>&& callback, uint64_t id)
{
    HttpResponsePtr httpResponse = HttpResponse::newHttpResponse();
    std::cout << "[INFO] ReadIssue called for id: " << id << std::endl;

    try
    {
        IssueResponseTo dto = m_service->Read(id);
        Json::Value jsonResponse = dto.toJson();
        
        std::string responseBody = Json::FastWriter().write(jsonResponse);
        std::cout << "[RESPONSE] " << responseBody << std::endl;
        
        httpResponse->setContentTypeCode(ContentType::CT_APPLICATION_JSON);
        httpResponse->setBody(responseBody);
        httpResponse->setStatusCode(HttpStatusCode::k200OK);
        std::cout << "[INFO] Issue retrieved successfully" << std::endl;
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
        std::cout << "[ERROR] Issue not found: " << e.what() << std::endl;
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

void IssueController::UpdateIssueIdFromRoute(const HttpRequestPtr& req, std::function<void(const HttpResponsePtr&)>&& callback, uint64_t id)
{
    HttpResponsePtr httpResponse = HttpResponse::newHttpResponse();
    std::cout << "[INFO] UpdateIssueWithId called for id: " << id << std::endl;

    try
    {
        auto jsonFromRequest = req->getJsonObject();
        if (!jsonFromRequest)
        {
            std::cout << "[ERROR] Invalid JSON in UpdateIssueWithId" << std::endl;
            Json::Value errorResponse;
            errorResponse["message"] = "Invalid JSON format";
            httpResponse->setBody(Json::FastWriter().write(errorResponse));
            httpResponse->setContentTypeCode(ContentType::CT_APPLICATION_JSON);
            httpResponse->setStatusCode(HttpStatusCode::k400BadRequest);
            callback(httpResponse);
            return;
        }

        auto requestDto = IssueRequestTo::fromJson(*jsonFromRequest);
        IssueResponseTo dto = m_service->Update(requestDto, id);
        
        Json::Value jsonResponse = dto.toJson();
        std::string responseBody = Json::FastWriter().write(jsonResponse);
        std::cout << "[RESPONSE] " << responseBody << std::endl;
        
        httpResponse->setContentTypeCode(ContentType::CT_APPLICATION_JSON);
        httpResponse->setBody(responseBody);
        httpResponse->setStatusCode(HttpStatusCode::k200OK);
        std::cout << "[INFO] Issue updated successfully" << std::endl;
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
        std::cout << "[ERROR] Issue not found for update: " << e.what() << std::endl;
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

void IssueController::UpdateIssueIdFromBody(const HttpRequestPtr& req, std::function<void(const HttpResponsePtr&)>&& callback)
{
    HttpResponsePtr httpResponse = HttpResponse::newHttpResponse();
    std::cout << "[INFO] UpdateIssue called (without ID in path)" << std::endl;

    try
    {
        auto jsonFromRequest = req->getJsonObject();
        if (!jsonFromRequest)
        {
            std::cout << "[ERROR] Invalid JSON in UpdateIssue" << std::endl;
            Json::Value errorResponse;
            errorResponse["message"] = "Invalid JSON format";
            httpResponse->setBody(Json::FastWriter().write(errorResponse));
            httpResponse->setContentTypeCode(ContentType::CT_APPLICATION_JSON);
            httpResponse->setStatusCode(HttpStatusCode::k400BadRequest);
            callback(httpResponse);
            return;
        }

        auto requestDto = IssueRequestTo::fromJson(*jsonFromRequest);
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

        IssueResponseTo dto = m_service->Update(requestDto, requestDto.id.value());
        
        Json::Value jsonResponse = dto.toJson();
        std::string responseBody = Json::FastWriter().write(jsonResponse);
        std::cout << "[RESPONSE] " << responseBody << std::endl;
        
        httpResponse->setContentTypeCode(ContentType::CT_APPLICATION_JSON);
        httpResponse->setBody(responseBody);
        httpResponse->setStatusCode(HttpStatusCode::k200OK);
        std::cout << "[INFO] Issue updated successfully" << std::endl;
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
        std::cout << "[ERROR] Issue not found for update: " << e.what() << std::endl;
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

void IssueController::DeleteIssue(const HttpRequestPtr& req, std::function<void(const HttpResponsePtr&)>&& callback, uint64_t id)
{
    HttpResponsePtr httpResponse = HttpResponse::newHttpResponse();
    std::cout << "[INFO] DeleteIssue called for id: " << id << std::endl;

    try
    {
        if (m_service->Delete(id))
        {
            httpResponse->setStatusCode(HttpStatusCode::k204NoContent);
            std::cout << "[INFO] Issue deleted successfully" << std::endl;
            std::cout << "[RESPONSE] No content (204)" << std::endl;
        }
        else
        {
            std::cout << "[ERROR] Issue not found for deletion" << std::endl;
            Json::Value errorResponse;
            errorResponse["message"] = "Issue not found";
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
        std::cout << "[ERROR] Issue not found: " << e.what() << std::endl;
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

void IssueController::GetAllIssues(const HttpRequestPtr& req, std::function<void(const HttpResponsePtr&)>&& callback)
{
    HttpResponsePtr httpResponse = HttpResponse::newHttpResponse();
    std::cout << "[INFO] GetAllIssues called" << std::endl;

    try
    {
        std::vector<IssueResponseTo> dtos = m_service->GetAll();
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
        std::cout << "[INFO] Retrieved " << dtos.size() << " issues" << std::endl;
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

void IssueController::GetEditorByIssueId(const HttpRequestPtr& req, std::function<void(const HttpResponsePtr&)>&& callback, uint64_t id)
{
    HttpResponsePtr httpResponse = HttpResponse::newHttpResponse();
    std::cout << "[INFO] GetEditorByIssueId called for issue id: " << id << std::endl;

    try
    {
        IssueResponseTo dto = m_service->Read(id);
        Json::Value jsonResponse = dto.toJson();
        
        std::string responseBody = Json::FastWriter().write(jsonResponse);
        std::cout << "[RESPONSE] " << responseBody << std::endl;
        
        httpResponse->setContentTypeCode(ContentType::CT_APPLICATION_JSON);
        httpResponse->setBody(responseBody);
        httpResponse->setStatusCode(HttpStatusCode::k200OK);
        std::cout << "[INFO] Editor retrieved successfully for issue" << std::endl;
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
        std::cout << "[ERROR] Issue not found: " << e.what() << std::endl;
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

void IssueController::GetLabelsByIssueId(const HttpRequestPtr& req, std::function<void(const HttpResponsePtr&)>&& callback, uint64_t id)
{
    HttpResponsePtr httpResponse = HttpResponse::newHttpResponse();
    std::cout << "[INFO] GetLabelsByIssueId called for issue id: " << id << std::endl;

    try
    {
        IssueResponseTo dto = m_service->Read(id);
        Json::Value jsonResponse = dto.toJson();
        
        std::string responseBody = Json::FastWriter().write(jsonResponse);
        std::cout << "[RESPONSE] " << responseBody << std::endl;
        
        httpResponse->setContentTypeCode(ContentType::CT_APPLICATION_JSON);
        httpResponse->setBody(responseBody);
        httpResponse->setStatusCode(HttpStatusCode::k200OK);
        std::cout << "[INFO] Labels retrieved successfully for issue" << std::endl;
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
        std::cout << "[ERROR] Issue not found: " << e.what() << std::endl;
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

void IssueController::GetPostsByIssueId(const HttpRequestPtr& req, std::function<void(const HttpResponsePtr&)>&& callback, uint64_t id)
{
    HttpResponsePtr httpResponse = HttpResponse::newHttpResponse();
    std::cout << "[INFO] GetPostsByIssueId called for issue id: " << id << std::endl;

    try
    {
        IssueResponseTo dto = m_service->Read(id);
        Json::Value jsonResponse = dto.toJson();
        
        std::string responseBody = Json::FastWriter().write(jsonResponse);
        std::cout << "[RESPONSE] " << responseBody << std::endl;
        
        httpResponse->setContentTypeCode(ContentType::CT_APPLICATION_JSON);
        httpResponse->setBody(responseBody);
        httpResponse->setStatusCode(HttpStatusCode::k200OK);
        std::cout << "[INFO] Posts retrieved successfully for issue" << std::endl;
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
        std::cout << "[ERROR] Issue not found: " << e.what() << std::endl;
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