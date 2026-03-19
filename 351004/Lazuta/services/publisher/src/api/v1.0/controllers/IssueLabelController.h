#pragma once

#include <drogon/HttpController.h>
#include <services/IssueLabelService.h>
#include <dto/requests/IssueLabelRequestTo.h>
#include <dto/responses/IssueLabelResponseTo.h>
#include <exceptions/DatabaseException.h>
#include <exceptions/NotFoundException.h>
#include <exceptions/ValidationException.h>

using namespace drogon;
using namespace publisher;

class IssueLabelController : public drogon::HttpController<IssueLabelController, false>
{
private:
    std::unique_ptr<IssueLabelService> m_service = nullptr;
    
public:
    explicit IssueLabelController(std::unique_ptr<IssueLabelService> service);
    
    METHOD_LIST_BEGIN
        ADD_METHOD_TO(IssueLabelController::CreateIssueLabel, "/api/v1.0/issue-labels", drogon::Post);
        ADD_METHOD_TO(IssueLabelController::ReadIssueLabel, "/api/v1.0/issue-labels/{id}", drogon::Get);
        ADD_METHOD_TO(IssueLabelController::UpdateIssueLabelIdFromRoute, "/api/v1.0/issue-labels/{id}", drogon::Put);
        ADD_METHOD_TO(IssueLabelController::UpdateIssueLabelIdFromBody, "/api/v1.0/issue-labels", drogon::Put);
        ADD_METHOD_TO(IssueLabelController::DeleteIssueLabel, "/api/v1.0/issue-labels/{id}", drogon::Delete);
        ADD_METHOD_TO(IssueLabelController::GetAllIssueLabels, "/api/v1.0/issue-labels", drogon::Get);
        ADD_METHOD_TO(IssueLabelController::GetByIssueId, "/api/v1.0/issue-labels/issue/{issueId}", drogon::Get);
        ADD_METHOD_TO(IssueLabelController::GetByLabelId, "/api/v1.0/issue-labels/label/{labelId}", drogon::Get);
        ADD_METHOD_TO(IssueLabelController::DeleteByIssueAndLabel, "/api/v1.0/issue-labels/issue/{issueId}/label/{labelId}", drogon::Delete);
    METHOD_LIST_END

private:
    void CreateIssueLabel(const HttpRequestPtr& req, std::function<void(const HttpResponsePtr&)>&& callback);
    void ReadIssueLabel(const HttpRequestPtr& req, std::function<void(const HttpResponsePtr&)>&& callback, uint64_t id);
    void UpdateIssueLabelIdFromRoute(const HttpRequestPtr& req, std::function<void(const HttpResponsePtr&)>&& callback, uint64_t id);
    void UpdateIssueLabelIdFromBody(const HttpRequestPtr& req, std::function<void(const HttpResponsePtr&)>&& callback);
    void DeleteIssueLabel(const HttpRequestPtr& req, std::function<void(const HttpResponsePtr&)>&& callback, uint64_t id);
    void GetAllIssueLabels(const HttpRequestPtr& req, std::function<void(const HttpResponsePtr&)>&& callback);
    void GetByIssueId(const HttpRequestPtr& req, std::function<void(const HttpResponsePtr&)>&& callback, uint64_t issueId);
    void GetByLabelId(const HttpRequestPtr& req, std::function<void(const HttpResponsePtr&)>&& callback, uint64_t labelId);
    void DeleteByIssueAndLabel(const HttpRequestPtr& req, std::function<void(const HttpResponsePtr&)>&& callback, uint64_t issueId, uint64_t labelId);
};