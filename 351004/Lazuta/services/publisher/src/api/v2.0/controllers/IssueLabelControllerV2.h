#pragma once

#include <drogon/HttpController.h>
#include <services/IssueLabelService.h>
#include <dto/requests/IssueLabelRequestTo.h>
#include <dto/responses/IssueLabelResponseTo.h>
#include <utils/JwtUtils.h>

using namespace drogon;
using namespace publisher;

class IssueLabelControllerV2 : public drogon::HttpController<IssueLabelControllerV2, false>
{
private:
    std::unique_ptr<IssueLabelService> m_service = nullptr;
    
public:
    explicit IssueLabelControllerV2(std::unique_ptr<IssueLabelService> service);
    
    METHOD_LIST_BEGIN
        ADD_METHOD_TO(IssueLabelControllerV2::CreateIssueLabel, "/api/v2.0/issue-labels", drogon::Post);
        ADD_METHOD_TO(IssueLabelControllerV2::ReadIssueLabel, "/api/v2.0/issue-labels/{id}", drogon::Get);
        ADD_METHOD_TO(IssueLabelControllerV2::DeleteIssueLabel, "/api/v2.0/issue-labels/{id}", drogon::Delete);
        ADD_METHOD_TO(IssueLabelControllerV2::GetAllIssueLabels, "/api/v2.0/issue-labels", drogon::Get);
    METHOD_LIST_END

private:
    void CreateIssueLabel(const HttpRequestPtr& req, std::function<void (const HttpResponsePtr &)> &&callback);
    void ReadIssueLabel(const HttpRequestPtr& req, std::function<void (const HttpResponsePtr &)> &&callback, uint64_t id);
    void DeleteIssueLabel(const HttpRequestPtr& req, std::function<void (const HttpResponsePtr &)> &&callback, uint64_t id);
    void GetAllIssueLabels(const HttpRequestPtr& req, std::function<void (const HttpResponsePtr &)> &&callback);
};
