#pragma once

#include <drogon/HttpClient.h>
#include <drogon/HttpController.h>
#include <services/EditorService.h>
#include <dto/requests/EditorRequestTo.h>
#include <dto/responses/EditorResponseTo.h>
#include <exceptions/DatabaseException.h>
#include <exceptions/NotFoundException.h>
#include <exceptions/ValidationException.h>
#include <utils/JwtUtils.h>

using namespace drogon;
using namespace publisher;

class EditorControllerV2 : public drogon::HttpController<EditorControllerV2, false>
{
private:
    std::unique_ptr<EditorService> m_service = nullptr;
    HttpClientPtr m_client;
    
    bool validateJwt(const HttpRequestPtr& req, std::function<void(const HttpResponsePtr&)>& callback, std::string& login, std::string& role);
    
public:
    explicit EditorControllerV2(std::unique_ptr<EditorService> service);
    
    METHOD_LIST_BEGIN
        ADD_METHOD_TO(EditorControllerV2::CreateEditor, "/api/v2.0/editors", drogon::Post);
        ADD_METHOD_TO(EditorControllerV2::ReadEditor, "/api/v2.0/editors/{id}", drogon::Get);
        ADD_METHOD_TO(EditorControllerV2::UpdateEditorIdFromRoute, "/api/v2.0/editors/{id}", drogon::Put);
        ADD_METHOD_TO(EditorControllerV2::UpdateEditorIdFromBody, "/api/v2.0/editors", drogon::Put);
        ADD_METHOD_TO(EditorControllerV2::DeleteEditor, "/api/v2.0/editors/{id}", drogon::Delete);
        ADD_METHOD_TO(EditorControllerV2::GetAllEditors, "/api/v2.0/editors", drogon::Get);
        ADD_METHOD_TO(EditorControllerV2::Login, "/api/v2.0/login", drogon::Post);
        ADD_METHOD_TO(EditorControllerV2::GetCurrentUser, "/api/v2.0/editors/me", drogon::Get);
    METHOD_LIST_END

private:
    void CreateEditor(const HttpRequestPtr& req, std::function<void (const HttpResponsePtr &)> &&callback);
    void ReadEditor(const HttpRequestPtr& req, std::function<void (const HttpResponsePtr &)> &&callback, uint64_t id);
    void UpdateEditorIdFromRoute(const HttpRequestPtr& req, std::function<void (const HttpResponsePtr &)> &&callback, uint64_t id);
    void UpdateEditorIdFromBody(const HttpRequestPtr& req, std::function<void (const HttpResponsePtr &)> &&callback);
    void DeleteEditor(const HttpRequestPtr& req, std::function<void (const HttpResponsePtr &)> &&callback, uint64_t id);
    void GetAllEditors(const HttpRequestPtr& req, std::function<void (const HttpResponsePtr &)> &&callback);

    void Login(const HttpRequestPtr& req, std::function<void (const HttpResponsePtr &)> &&callback);
    void GetCurrentUser(const HttpRequestPtr& req, std::function<void (const HttpResponsePtr &)> &&callback);
};
