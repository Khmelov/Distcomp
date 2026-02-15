#pragma once

#include <drogon/HttpController.h>
#include <services/EditorService.h>
#include <dto/requests/EditorRequestTo.h>
#include <dto/responses/EditorResponseTo.h>
#include <exceptions/DatabaseException.h>
#include <exceptions/NotFoundException.h>

using namespace drogon;

class EditorController : public drogon::HttpController<EditorController, false>
{
private:
    std::unique_ptr<EditorService> m_service = nullptr;
public:
    explicit EditorController(std::unique_ptr<EditorService> service);
    
    METHOD_LIST_BEGIN
        METHOD_ADD(EditorController::CreateEditor, "/api/v1.0/editors", drogon::Post);
        METHOD_ADD(EditorController::ReadEditor, "/api/v1.0/editors/{id}", drogon::Get);
        METHOD_ADD(EditorController::UpdateEditor, "/api/v1.0/editors/", drogon::Put);
        METHOD_ADD(EditorController::DeleteEditor, "/api/v1.0/editors/{id}", drogon::Delete);
        METHOD_ADD(EditorController::GetAllEditors, "/api/v1.0/editors", drogon::Get);
    METHOD_LIST_END

private:
    void CreateEditor(const HttpRequestPtr& req, std::function<void (const HttpResponsePtr &)> &&callback);
    void ReadEditor(const HttpRequestPtr& req, std::function<void (const HttpResponsePtr &)> &&callback, uint64_t id);
    void UpdateEditor(const HttpRequestPtr& req, std::function<void (const HttpResponsePtr &)> &&callback);
    void DeleteEditor(const HttpRequestPtr& req, std::function<void (const HttpResponsePtr &)> &&callback, uint64_t id);
    void GetAllEditors(const HttpRequestPtr& req, std::function<void (const HttpResponsePtr &)> &&callback);
};
