#pragma once

#include <memory>
#include <vector>

#include <dto/responses/PostResponseTo.h>
#include <dto/requests/PostRequestTo.h>

namespace myapp
{

class PostRepository;
class IssueRepository;

class PostService 
{
private:
    std::shared_ptr<PostRepository> m_dao;
    std::shared_ptr<IssueRepository> m_issueRepository;
    
public:
    PostService(
        std::shared_ptr<PostRepository> storage,
        std::shared_ptr<IssueRepository> issueRepository);
    
    dto::PostResponseTo Create(const dto::PostRequestTo& request);
    dto::PostResponseTo Read(int64_t id);
    dto::PostResponseTo Update(const dto::PostRequestTo& request, int64_t id);
    bool Delete(int64_t id);
    std::vector<dto::PostResponseTo> GetAll();
};

}