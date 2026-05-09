#pragma once

#include <drogon/HttpController.h>
#include <services/LabelService.h>
#include <dto/requests/LabelRequestTo.h>
#include <dto/responses/LabelResponseTo.h>
#include <utils/JwtUtils.h>

using namespace drogon;
using namespace publisher;

class LabelControllerV2 : public drogon::HttpController<LabelControllerV2, false>
{
private:
    std::unique_ptr<LabelService> m_service = nullptr;
    
public:
    explicit LabelControllerV2(std::unique_ptr<LabelService> service);
    
    METHOD_LIST_BEGIN
        ADD_METHOD_TO(LabelControllerV2::CreateLabel, "/api/v2.0/labels", drogon::Post);
        ADD_METHOD_TO(LabelControllerV2::ReadLabel, "/api/v2.0/labels/{id}", drogon::Get);
        ADD_METHOD_TO(LabelControllerV2::UpdateLabel, "/api/v2.0/labels/{id}", drogon::Put);
        ADD_METHOD_TO(LabelControllerV2::DeleteLabel, "/api/v2.0/labels/{id}", drogon::Delete);
        ADD_METHOD_TO(LabelControllerV2::GetAllLabels, "/api/v2.0/labels", drogon::Get);
    METHOD_LIST_END

private:
    void CreateLabel(const HttpRequestPtr& req, std::function<void (const HttpResponsePtr &)> &&callback);
    void ReadLabel(const HttpRequestPtr& req, std::function<void (const HttpResponsePtr &)> &&callback, uint64_t id);
    void UpdateLabel(const HttpRequestPtr& req, std::function<void (const HttpResponsePtr &)> &&callback, uint64_t id);
    void DeleteLabel(const HttpRequestPtr& req, std::function<void (const HttpResponsePtr &)> &&callback, uint64_t id);
    void GetAllLabels(const HttpRequestPtr& req, std::function<void (const HttpResponsePtr &)> &&callback);
};
