#pragma once

#include <drogon/HttpController.h>
#include <services/EditorService.h>
#include <dto/requests/EditorRequestTo.h>
#include <dto/responses/EditorResponseTo.h>
#include <exceptions/DatabaseException.h>
#include <exceptions/NotFoundException.h>
#include <exceptions/ValidationException.h>

using namespace drogon;
<<<<<<< HEAD:351004/Lazuta/services/publisher/src/api/v1.0/controllers/EditorController.h
using namespace publisher;
=======
using namespace myapp;
>>>>>>> f26c601fbbe43710c18d4d0b9d78ec1d65a1357c:351004/Lazuta/src/api/v1.0/controllers/EditorController.h

class EditorController : public drogon::HttpController<EditorController, false>
{
private:
    std::unique_ptr<EditorService> m_service = nullptr;
public:
    explicit EditorController(std::unique_ptr<EditorService> service);
    
    METHOD_LIST_BEGIN
        ADD_METHOD_TO(EditorController::CreateEditor, "/api/v1.0/editors", drogon::Post);
        ADD_METHOD_TO(EditorController::ReadEditor, "/api/v1.0/editors/{id}", drogon::Get);
        ADD_METHOD_TO(EditorController::UpdateEditorIdFromRoute, "/api/v1.0/editors/{id}", drogon::Put);
        ADD_METHOD_TO(EditorController::UpdateEditorIdFromBody, "/api/v1.0/editors", drogon::Put);
        ADD_METHOD_TO(EditorController::DeleteEditor, "/api/v1.0/editors/{id}", drogon::Delete);
        ADD_METHOD_TO(EditorController::GetAllEditors, "/api/v1.0/editors", drogon::Get);
    METHOD_LIST_END

private:
    void CreateEditor(const HttpRequestPtr& req, std::function<void (const HttpResponsePtr &)> &&callback);
    void ReadEditor(const HttpRequestPtr& req, std::function<void (const HttpResponsePtr &)> &&callback, uint64_t id);
    void UpdateEditorIdFromRoute(const HttpRequestPtr& req, std::function<void (const HttpResponsePtr &)> &&callback, uint64_t id);
    void UpdateEditorIdFromBody(const HttpRequestPtr& req, std::function<void (const HttpResponsePtr &)> &&callback);
    void DeleteEditor(const HttpRequestPtr& req, std::function<void (const HttpResponsePtr &)> &&callback, uint64_t id);
    void GetAllEditors(const HttpRequestPtr& req, std::function<void (const HttpResponsePtr &)> &&callback);
};