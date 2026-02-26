#pragma once

#include <drogon/HttpController.h>
#include <services/IssueService.h>
#include <dto/requests/IssueRequestTo.h>
#include <dto/responses/IssueResponseTo.h>
#include <exceptions/DatabaseException.h>
#include <exceptions/NotFoundException.h>
#include <exceptions/ValidationException.h>

using namespace drogon;

class IssueController : public drogon::HttpController<IssueController, false>
{
private:
    std::unique_ptr<IssueService> m_service = nullptr;
    
public:
    explicit IssueController(std::unique_ptr<IssueService> service);
    
    METHOD_LIST_BEGIN
        ADD_METHOD_TO(IssueController::CreateIssue, "/api/v1.0/issues", drogon::Post);
        ADD_METHOD_TO(IssueController::ReadIssue, "/api/v1.0/issues/{id}", drogon::Get);
        ADD_METHOD_TO(IssueController::UpdateIssueIdFromRoute, "/api/v1.0/issues/{id}", drogon::Put);
        ADD_METHOD_TO(IssueController::UpdateIssueIdFromBody, "/api/v1.0/issues", drogon::Put);
        ADD_METHOD_TO(IssueController::DeleteIssue, "/api/v1.0/issues/{id}", drogon::Delete);
        ADD_METHOD_TO(IssueController::GetAllIssues, "/api/v1.0/issues", drogon::Get);
        ADD_METHOD_TO(IssueController::GetEditorByIssueId, "/api/v1.0/issues/{id}/editor", drogon::Get);
        ADD_METHOD_TO(IssueController::GetLabelsByIssueId, "/api/v1.0/issues/{id}/labels", drogon::Get);
        ADD_METHOD_TO(IssueController::GetPostsByIssueId, "/api/v1.0/issues/{id}/posts", drogon::Get);
    METHOD_LIST_END

private:
    void CreateIssue(const HttpRequestPtr& req, std::function<void(const HttpResponsePtr&)>&& callback);
    void ReadIssue(const HttpRequestPtr& req, std::function<void(const HttpResponsePtr&)>&& callback, uint64_t id);
    void UpdateIssueIdFromRoute(const HttpRequestPtr& req, std::function<void(const HttpResponsePtr&)>&& callback, uint64_t id);
    void UpdateIssueIdFromBody(const HttpRequestPtr& req, std::function<void(const HttpResponsePtr&)>&& callback);
    void DeleteIssue(const HttpRequestPtr& req, std::function<void(const HttpResponsePtr&)>&& callback, uint64_t id);
    void GetAllIssues(const HttpRequestPtr& req, std::function<void(const HttpResponsePtr&)>&& callback);
    void GetEditorByIssueId(const HttpRequestPtr& req, std::function<void(const HttpResponsePtr&)>&& callback, uint64_t id);
    void GetLabelsByIssueId(const HttpRequestPtr& req, std::function<void(const HttpResponsePtr&)>&& callback, uint64_t id);
    void GetPostsByIssueId(const HttpRequestPtr& req, std::function<void(const HttpResponsePtr&)>&& callback, uint64_t id);
};