// PostRepository.cpp
#include "PostRepository.h"
#include <drogon/HttpClient.h>
#include <drogon/HttpRequest.h>
#include <iostream>

namespace publisher
{

PostRepository::PostRepository()
{
    m_client = drogon::HttpClient::newHttpClient("http://127.0.0.1:24130");
    std::cout << "[DEBUG] PostRepository initialized with client: http://localhost:24130" << std::endl;
}

Json::Value PostRepository::ModelToJson(const drogon_model::distcomp::TblPost& entity)
{
    Json::Value json;
    json["issueId"] = entity.getValueOfIssueId();
    json["content"] = entity.getValueOfContent();
    
    if (entity.getValueOfId() != 0)
    {
        json["id"] = entity.getValueOfId();
    }
    
    std::cout << "[DEBUG] ModelToJson: id=" << entity.getValueOfId() 
              << ", issueId=" << entity.getValueOfIssueId()
              << ", content=" << entity.getValueOfContent() << std::endl;
    
    return json;
}

drogon_model::distcomp::TblPost PostRepository::JsonToModel(const Json::Value& json)
{
    drogon_model::distcomp::TblPost entity;
    
    if (json.isMember("id"))
    {
        entity.setId(json["id"].asInt64());
        std::cout << "[DEBUG] JsonToModel: id=" << json["id"].asInt64() << std::endl;
    }
    
    if (json.isMember("issueId"))
    {
        entity.setIssueId(json["issueId"].asInt64());
        std::cout << "[DEBUG] JsonToModel: issueId=" << json["issueId"].asInt64() << std::endl;
    }
    
    if (json.isMember("content"))
    {
        entity.setContent(json["content"].asString());
        std::cout << "[DEBUG] JsonToModel: content=" << json["content"].asString() << std::endl;
    }
    
    return entity;
}

std::vector<drogon_model::distcomp::TblPost> PostRepository::JsonArrayToModelVector(const Json::Value& jsonArray)
{
    std::vector<drogon_model::distcomp::TblPost> result;
    
    std::cout << "[DEBUG] JsonArrayToModelVector: isArray=" << jsonArray.isArray() 
              << ", size=" << jsonArray.size() << std::endl;
    
    if (!jsonArray.isArray())
    {
        return result;
    }
    
    for (const auto& item : jsonArray)
    {
        result.push_back(JsonToModel(item));
    }
    
    std::cout << "[DEBUG] JsonArrayToModelVector: result size=" << result.size() << std::endl;
    
    return result;
}

std::variant<int64_t, DatabaseError> PostRepository::Create(const drogon_model::distcomp::TblPost& entity)
{
    std::cout << "[DEBUG] Create: START" << std::endl;
    
    try
    {
        auto req = drogon::HttpRequest::newHttpRequest();
        req->setMethod(drogon::Post);
        req->setPath("/api/v1.0/posts");
        req->addHeader("Content-Type", "application/json");
        req->addHeader("Accept", "application/json");
        req->setContentTypeCode(drogon::CT_APPLICATION_JSON);
        
        std::string body = Json::FastWriter().write(ModelToJson(entity));
        req->setBody(body);
        
        std::cout << "[DEBUG] Create: Sending request to " << req->getPath() << std::endl;
        std::cout << "[DEBUG] Create: Request body: " << body << std::endl;
        
        auto resp = m_client->sendRequest(req, 5.0);
        
        std::cout << "[DEBUG] Create: Response received, ReqResult: " << static_cast<int>(resp.first) << std::endl;
        
        if (resp.first != drogon::ReqResult::Ok)
        {
            switch (resp.first)
            {
                case drogon::ReqResult::Timeout:
                    std::cerr << "[ERROR] Create: Timeout (discussion not responding)" << std::endl;
                    break;
                case drogon::ReqResult::BadResponse:
                    std::cerr << "[ERROR] Create: Bad response from discussion" << std::endl;
                    if (resp.second)
                    {
                        std::cerr << "[ERROR] Create: Response body: " << resp.second->getBody() << std::endl;
                    }
                    break;
                case drogon::ReqResult::NetworkFailure:
                    std::cerr << "[ERROR] Create: Network failure (discussion unreachable)" << std::endl;
                    break;
                default:
                    std::cerr << "[ERROR] Create: Unknown error: " << static_cast<int>(resp.first) << std::endl;
                    break;
            }
            return DatabaseError::DatabaseError;
        }
        
        auto httpResp = resp.second;
        std::cout << "[DEBUG] Create: HTTP status: " << httpResp->getStatusCode() << std::endl;
        std::cout << "[DEBUG] Create: Response body: " << httpResp->getBody() << std::endl;
        
        if (httpResp->getStatusCode() == drogon::k201Created)
        {
            auto json = httpResp->getJsonObject();
            if (json && json->isMember("id"))
            {
                int64_t id = (*json)["id"].asInt64();
                std::cout << "[DEBUG] Create: Success, id=" << id << std::endl;
                return id;
            }
            else
            {
                std::cerr << "[ERROR] Create: No id in response" << std::endl;
            }
        }
        else if (httpResp->getStatusCode() == drogon::k400BadRequest)
        {
            std::cerr << "[ERROR] Create: BadRequest (400)" << std::endl;
            return DatabaseError::InvalidData;
        }
        else
        {
            std::cerr << "[ERROR] Create: Unexpected status code: " << httpResp->getStatusCode() << std::endl;
        }
        
        return DatabaseError::DatabaseError;
    }
    catch (const std::exception& e)
    {
        std::cerr << "[ERROR] Create exception: " << e.what() << std::endl;
        return DatabaseError::DatabaseError;
    }
}

std::variant<drogon_model::distcomp::TblPost, DatabaseError> PostRepository::GetByID(int64_t id)
{
    std::cout << "[DEBUG] GetByID: START, id=" << id << std::endl;
    
    try
    {
        auto req = drogon::HttpRequest::newHttpRequest();
        req->setMethod(drogon::Get);
        req->setPath("/api/v1.0/posts/" + std::to_string(id));
        
        std::cout << "[DEBUG] GetByID: Sending request to " << req->getPath() << std::endl;
        
        auto resp = m_client->sendRequest(req, 5.0);
        
        std::cout << "[DEBUG] GetByID: ReqResult=" << static_cast<int>(resp.first) << std::endl;
        
        if (resp.first != drogon::ReqResult::Ok)
        {
            return DatabaseError::DatabaseError;
        }
        
        auto httpResp = resp.second;
        std::cout << "[DEBUG] GetByID: HTTP status=" << httpResp->getStatusCode() << std::endl;
        
        if (httpResp->getStatusCode() == drogon::k200OK)
        {
            auto json = httpResp->getJsonObject();
            if (json)
            {
                auto result = JsonToModel(*json);
                std::cout << "[DEBUG] GetByID: Success" << std::endl;
                return result;
            }
        }
        else if (httpResp->getStatusCode() == drogon::k404NotFound)
        {
            std::cout << "[DEBUG] GetByID: Not found" << std::endl;
            return DatabaseError::NotFound;
        }
        
        return DatabaseError::DatabaseError;
    }
    catch (const std::exception& e)
    {
        std::cerr << "[ERROR] GetByID exception: " << e.what() << std::endl;
        return DatabaseError::DatabaseError;
    }
}

std::variant<bool, DatabaseError> PostRepository::Update(int64_t id, const drogon_model::distcomp::TblPost& entity)
{
    std::cout << "[DEBUG] Update: START, id=" << id << std::endl;
    
    try
    {
        auto req = drogon::HttpRequest::newHttpRequest();
        req->setMethod(drogon::Put);
        req->setPath("/api/v1.0/posts/" + std::to_string(id));
        req->setContentTypeCode(drogon::CT_APPLICATION_JSON);
        
        std::string body = Json::FastWriter().write(ModelToJson(entity));
        req->setBody(body);
        
        std::cout << "[DEBUG] Update: Sending to " << req->getPath() << std::endl;
        std::cout << "[DEBUG] Update: Body: " << body << std::endl;
        
        auto resp = m_client->sendRequest(req, 5.0);
        
        std::cout << "[DEBUG] Update: ReqResult=" << static_cast<int>(resp.first) << std::endl;
        
        if (resp.first != drogon::ReqResult::Ok)
        {
            return DatabaseError::DatabaseError;
        }
        
        auto httpResp = resp.second;
        std::cout << "[DEBUG] Update: HTTP status=" << httpResp->getStatusCode() << std::endl;
        
        if (httpResp->getStatusCode() == drogon::k200OK)
        {
            std::cout << "[DEBUG] Update: Success" << std::endl;
            return true;
        }
        else if (httpResp->getStatusCode() == drogon::k404NotFound)
        {
            std::cout << "[DEBUG] Update: Not found" << std::endl;
            return DatabaseError::NotFound;
        }
        
        return DatabaseError::DatabaseError;
    }
    catch (const std::exception& e)
    {
        std::cerr << "[ERROR] Update exception: " << e.what() << std::endl;
        return DatabaseError::DatabaseError;
    }
}

std::variant<bool, DatabaseError> PostRepository::Delete(int64_t id)
{
    std::cout << "[DEBUG] Delete: START, id=" << id << std::endl;
    
    try
    {
        auto req = drogon::HttpRequest::newHttpRequest();
        req->setMethod(drogon::Delete);
        req->setPath("/api/v1.0/posts/" + std::to_string(id));
        
        std::cout << "[DEBUG] Delete: Sending to " << req->getPath() << std::endl;
        
        auto resp = m_client->sendRequest(req, 5.0);
        
        std::cout << "[DEBUG] Delete: ReqResult=" << static_cast<int>(resp.first) << std::endl;
        
        if (resp.first != drogon::ReqResult::Ok)
        {
            return DatabaseError::DatabaseError;
        }
        
        auto httpResp = resp.second;
        std::cout << "[DEBUG] Delete: HTTP status=" << httpResp->getStatusCode() << std::endl;
        
        if (httpResp->getStatusCode() == drogon::k204NoContent)
        {
            std::cout << "[DEBUG] Delete: Success" << std::endl;
            return true;
        }
        else if (httpResp->getStatusCode() == drogon::k404NotFound)
        {
            std::cout << "[DEBUG] Delete: Not found" << std::endl;
            return DatabaseError::NotFound;
        }
        
        return DatabaseError::DatabaseError;
    }
    catch (const std::exception& e)
    {
        std::cerr << "[ERROR] Delete exception: " << e.what() << std::endl;
        return DatabaseError::DatabaseError;
    }
}

std::variant<std::vector<drogon_model::distcomp::TblPost>, DatabaseError> PostRepository::ReadAll()
{
    std::cout << "[DEBUG] ReadAll: START" << std::endl;
    
    try
    {
        auto req = drogon::HttpRequest::newHttpRequest();
        req->setMethod(drogon::Get);
        req->setPath("/api/v1.0/posts");
        
        std::cout << "[DEBUG] ReadAll: Sending request to " << req->getPath() << std::endl;
        
        auto resp = m_client->sendRequest(req, 5.0);
        
        std::cout << "[DEBUG] ReadAll: ReqResult: " << static_cast<int>(resp.first) << std::endl;
        
        if (resp.first != drogon::ReqResult::Ok)
        {
            switch (resp.first)
            {
                case drogon::ReqResult::Timeout:
                    std::cerr << "[ERROR] ReadAll: Timeout (discussion not responding)" << std::endl;
                    break;
                case drogon::ReqResult::BadResponse:
                    std::cerr << "[ERROR] ReadAll: Bad response from discussion" << std::endl;
                    if (resp.second)
                    {
                        std::cerr << "[ERROR] ReadAll: Response body: " << resp.second->getBody() << std::endl;
                    }
                    break;
                case drogon::ReqResult::NetworkFailure:
                    std::cerr << "[ERROR] ReadAll: Network failure (discussion unreachable)" << std::endl;
                    break;
                default:
                    std::cerr << "[ERROR] ReadAll: Unknown error: " << static_cast<int>(resp.first) << std::endl;
                    break;
            }
            return DatabaseError::DatabaseError;
        }
        
        auto httpResp = resp.second;
        std::cout << "[DEBUG] ReadAll - HTTP status: " << httpResp->getStatusCode() << std::endl;
        std::cout << "[DEBUG] ReadAll - Response body: " << httpResp->getBody() << std::endl;
        
        if (httpResp->getStatusCode() == drogon::k200OK)
        {
            auto json = httpResp->getJsonObject();
            if (json && json->isArray())
            {
                std::cout << "[DEBUG] ReadAll: JSON is array, size=" << json->size() << std::endl;
                return JsonArrayToModelVector(*json);
            }
            else
            {
                std::cerr << "[ERROR] ReadAll: Response is not a JSON array" << std::endl;
            }
        }
        else
        {
            std::cerr << "[ERROR] ReadAll: Unexpected status code: " << httpResp->getStatusCode() << std::endl;
        }
        
        return DatabaseError::DatabaseError;
    }
    catch (const std::exception& e)
    {
        std::cerr << "[ERROR] ReadAll exception: " << e.what() << std::endl;
        return DatabaseError::DatabaseError;
    }
}

std::variant<bool, DatabaseError> PostRepository::Exists(int64_t id)
{
    std::cout << "[DEBUG] Exists: id=" << id << std::endl;
    
    auto result = GetByID(id);
    
    if (std::holds_alternative<drogon_model::distcomp::TblPost>(result))
    {
        std::cout << "[DEBUG] Exists: true" << std::endl;
        return true;
    }
    
    if (std::holds_alternative<DatabaseError>(result))
    {
        DatabaseError error = std::get<DatabaseError>(result);
        if (error == DatabaseError::NotFound)
        {
            std::cout << "[DEBUG] Exists: false (NotFound)" << std::endl;
            return false;
        }
        std::cout << "[DEBUG] Exists: DatabaseError" << std::endl;
        return DatabaseError::DatabaseError;
    }
    
    std::cout << "[DEBUG] Exists: false (default)" << std::endl;
    return false;
}

std::variant<std::vector<drogon_model::distcomp::TblPost>, DatabaseError> PostRepository::FindByIssueId(int64_t issueId)
{
    std::cout << "[DEBUG] FindByIssueId: START, issueId=" << issueId << std::endl;
    
    try
    {
        auto req = drogon::HttpRequest::newHttpRequest();
        req->setMethod(drogon::Get);
        req->setPath("/api/v1.0/posts/by-issue/" + std::to_string(issueId));
        
        std::cout << "[DEBUG] FindByIssueId: Sending to " << req->getPath() << std::endl;
        
        auto resp = m_client->sendRequest(req, 5.0);
        
        std::cout << "[DEBUG] FindByIssueId: ReqResult=" << static_cast<int>(resp.first) << std::endl;
        
        if (resp.first != drogon::ReqResult::Ok)
        {
            return DatabaseError::DatabaseError;
        }
        
        auto httpResp = resp.second;
        std::cout << "[DEBUG] FindByIssueId: HTTP status=" << httpResp->getStatusCode() << std::endl;
        
        if (httpResp->getStatusCode() == drogon::k200OK)
        {
            auto json = httpResp->getJsonObject();
            if (json && json->isArray())
            {
                std::cout << "[DEBUG] FindByIssueId: JSON array, size=" << json->size() << std::endl;
                return JsonArrayToModelVector(*json);
            }
        }
        
        return DatabaseError::DatabaseError;
    }
    catch (const std::exception& e)
    {
        std::cerr << "[ERROR] FindByIssueId exception: " << e.what() << std::endl;
        return DatabaseError::DatabaseError;
    }
}

}