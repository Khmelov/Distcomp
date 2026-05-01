#pragma once

#include <drogon/HttpController.h>
#include <services/IssueService.h>
#include <dto/requests/IssueRequestTo.h>
#include <dto/responses/IssueResponseTo.h>
#include <exceptions/DatabaseException.h>
#include <exceptions/NotFoundException.h>
#include <exceptions/ValidationException.h>
#include <utils/JwtUtils.h>

using namespace drogon;
using namespace publisher;

class IssueControllerV2 : public drogon::HttpController<IssueControllerV2, false>
{
private:
    std::unique_ptr<IssueService> m_service = nullptr;
    
    bool validateJwt(const HttpRequestPtr& req, std::function<void(const HttpResponsePtr&)>& callback, std::string& login, std::string& role);
    
public:
    explicit IssueControllerV2(std::unique_ptr<IssueService> service);
    
    METHOD_LIST_BEGIN
        ADD_METHOD_TO(IssueControllerV2::CreateIssue, "/api/v2.0/issues", drogon::Post);
        ADD_METHOD_TO(IssueControllerV2::ReadIssue, "/api/v2.0/issues/{id}", drogon::Get);
        ADD_METHOD_TO(IssueControllerV2::UpdateIssueIdFromRoute, "/api/v2.0/issues/{id}", drogon::Put);
        ADD_METHOD_TO(IssueControllerV2::UpdateIssueIdFromBody, "/api/v2.0/issues", drogon::Put);
        ADD_METHOD_TO(IssueControllerV2::DeleteIssue, "/api/v2.0/issues/{id}", drogon::Delete);
        ADD_METHOD_TO(IssueControllerV2::GetAllIssues, "/api/v2.0/issues", drogon::Get);
    METHOD_LIST_END

private:
    void CreateIssue(const HttpRequestPtr& req, std::function<void (const HttpResponsePtr &)> &&callback);
    void ReadIssue(const HttpRequestPtr& req, std::function<void (const HttpResponsePtr &)> &&callback, uint64_t id);
    void UpdateIssueIdFromRoute(const HttpRequestPtr& req, std::function<void (const HttpResponsePtr &)> &&callback, uint64_t id);
    void UpdateIssueIdFromBody(const HttpRequestPtr& req, std::function<void (const HttpResponsePtr &)> &&callback);
    void DeleteIssue(const HttpRequestPtr& req, std::function<void (const HttpResponsePtr &)> &&callback, uint64_t id);
    void GetAllIssues(const HttpRequestPtr& req, std::function<void (const HttpResponsePtr &)> &&callback);
};
