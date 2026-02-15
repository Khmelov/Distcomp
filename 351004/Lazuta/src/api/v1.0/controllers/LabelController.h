#pragma once

#include <drogon/HttpController.h>
#include <services/LabelService.h>
#include <dto/requests/LabelRequestTo.h>
#include <dto/responses/LabelResponseTo.h>
#include <exceptions/DatabaseException.h>
#include <exceptions/NotFoundException.h>
#include <exceptions/ValidationException.h>

using namespace drogon;

class LabelController : public drogon::HttpController<LabelController, false>
{
private:
    std::unique_ptr<LabelService> m_service = nullptr;
    
public:
    explicit LabelController(std::unique_ptr<LabelService> service);
    
    METHOD_LIST_BEGIN
        ADD_METHOD_TO(LabelController::CreateLabel, "/api/v1.0/labels", drogon::Post);
        ADD_METHOD_TO(LabelController::ReadLabel, "/api/v1.0/labels/{id}", drogon::Get);
        ADD_METHOD_TO(LabelController::UpdateLabelIdFromRoute, "/api/v1.0/labels/{id}", drogon::Put);
        ADD_METHOD_TO(LabelController::UpdateLabelIdFromBody, "/api/v1.0/labels", drogon::Put);
        ADD_METHOD_TO(LabelController::DeleteLabel, "/api/v1.0/labels/{id}", drogon::Delete);
        ADD_METHOD_TO(LabelController::GetAllLabels, "/api/v1.0/labels", drogon::Get);
    METHOD_LIST_END

private:
    void CreateLabel(const HttpRequestPtr& req, std::function<void(const HttpResponsePtr&)>&& callback);
    void ReadLabel(const HttpRequestPtr& req, std::function<void(const HttpResponsePtr&)>&& callback, uint64_t id);
    void UpdateLabelIdFromRoute(const HttpRequestPtr& req, std::function<void(const HttpResponsePtr&)>&& callback, uint64_t id);
    void UpdateLabelIdFromBody(const HttpRequestPtr& req, std::function<void(const HttpResponsePtr&)>&& callback);
    void DeleteLabel(const HttpRequestPtr& req, std::function<void(const HttpResponsePtr&)>&& callback, uint64_t id);
    void GetAllLabels(const HttpRequestPtr& req, std::function<void(const HttpResponsePtr&)>&& callback);
};