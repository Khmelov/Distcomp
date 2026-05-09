#pragma once

#include <drogon/HttpController.h>
#include <services/PostService.h>
#include <dto/requests/PostRequestTo.h>
#include <dto/responses/PostResponseTo.h>
#include <utils/JwtUtils.h>

using namespace drogon;
using namespace publisher;

class PostControllerV2 : public drogon::HttpController<PostControllerV2, false>
{
private:
    std::unique_ptr<PostService> m_service = nullptr;
    
public:
    explicit PostControllerV2(std::unique_ptr<PostService> service);
    
    METHOD_LIST_BEGIN
        ADD_METHOD_TO(PostControllerV2::CreatePost, "/api/v2.0/posts", drogon::Post);
        ADD_METHOD_TO(PostControllerV2::ReadPost, "/api/v2.0/posts/{id}", drogon::Get);
        ADD_METHOD_TO(PostControllerV2::UpdatePost, "/api/v2.0/posts/{id}", drogon::Put);
        ADD_METHOD_TO(PostControllerV2::DeletePost, "/api/v2.0/posts/{id}", drogon::Delete);
        ADD_METHOD_TO(PostControllerV2::GetAllPosts, "/api/v2.0/posts", drogon::Get);
    METHOD_LIST_END

private:
    void CreatePost(const HttpRequestPtr& req, std::function<void (const HttpResponsePtr &)> &&callback);
    void ReadPost(const HttpRequestPtr& req, std::function<void (const HttpResponsePtr &)> &&callback, uint64_t id);
    void UpdatePost(const HttpRequestPtr& req, std::function<void (const HttpResponsePtr &)> &&callback, uint64_t id);
    void DeletePost(const HttpRequestPtr& req, std::function<void (const HttpResponsePtr &)> &&callback, uint64_t id);
    void GetAllPosts(const HttpRequestPtr& req, std::function<void (const HttpResponsePtr &)> &&callback);
};
