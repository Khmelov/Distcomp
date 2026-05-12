#pragma once

#include <memory>
#include <vector>

#include <dto/responses/PostResponseTo.h>
#include <dto/requests/PostRequestTo.h>

namespace discussion
{

class PostRepository;

class PostService 
{
private:
    std::shared_ptr<PostRepository> m_dao;
    
public:
    explicit PostService(std::shared_ptr<PostRepository> storage);
    
    dto::PostResponseTo Create(const dto::PostRequestTo& request);
    dto::PostResponseTo Read(int64_t id);
    dto::PostResponseTo Update(const dto::PostRequestTo& request, int64_t id);
    bool Delete(int64_t id);
    std::vector<dto::PostResponseTo> GetAll();
    std::vector<dto::PostResponseTo> GetByIssueId(int64_t issueId);
};

}