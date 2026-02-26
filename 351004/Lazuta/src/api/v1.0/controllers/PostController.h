#pragma once

#include <drogon/HttpController.h>
#include <services/PostService.h>
#include <dto/requests/PostRequestTo.h>
#include <dto/responses/PostResponseTo.h>
#include <exceptions/DatabaseException.h>
#include <exceptions/NotFoundException.h>
#include <exceptions/ValidationException.h>

using namespace drogon;

class PostController : public drogon::HttpController<PostController, false>
{
private:
    std::unique_ptr<PostService> m_service = nullptr;
    
public:
    explicit PostController(std::unique_ptr<PostService> service);
    
    METHOD_LIST_BEGIN
        ADD_METHOD_TO(PostController::CreatePost, "/api/v1.0/posts", drogon::Post);
        ADD_METHOD_TO(PostController::ReadPost, "/api/v1.0/posts/{id}", drogon::Get);
        ADD_METHOD_TO(PostController::UpdatePostIdFromRoute, "/api/v1.0/posts/{id}", drogon::Put);
        ADD_METHOD_TO(PostController::UpdatePostIdFromBody, "/api/v1.0/posts", drogon::Put);
        ADD_METHOD_TO(PostController::DeletePost, "/api/v1.0/posts/{id}", drogon::Delete);
        ADD_METHOD_TO(PostController::GetAllPosts, "/api/v1.0/posts", drogon::Get);
    METHOD_LIST_END

private:
    void CreatePost(const HttpRequestPtr& req, std::function<void(const HttpResponsePtr&)>&& callback);
    void ReadPost(const HttpRequestPtr& req, std::function<void(const HttpResponsePtr&)>&& callback, uint64_t id);
    void UpdatePostIdFromRoute(const HttpRequestPtr& req, std::function<void(const HttpResponsePtr&)>&& callback, uint64_t id);
    void UpdatePostIdFromBody(const HttpRequestPtr& req, std::function<void(const HttpResponsePtr&)>&& callback);
    void DeletePost(const HttpRequestPtr& req, std::function<void(const HttpResponsePtr&)>&& callback, uint64_t id);
    void GetAllPosts(const HttpRequestPtr& req, std::function<void(const HttpResponsePtr&)>&& callback);
};