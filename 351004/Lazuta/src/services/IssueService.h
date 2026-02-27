#pragma once

#include <memory>
#include <vector>

#include <dto/responses/IssueResponseTo.h>
#include <dto/requests/IssueRequestTo.h>

namespace myapp
{

class IssueRepository;
class EditorRepository;

class IssueService 
{
private:
    std::shared_ptr<IssueRepository> m_dao;
    std::shared_ptr<EditorRepository> m_editorRepository;
    
public:
    IssueService(
        std::shared_ptr<IssueRepository> storage,
        std::shared_ptr<EditorRepository> editorRepository);
    
    dto::IssueResponseTo Create(const dto::IssueRequestTo& request);
    dto::IssueResponseTo Read(int64_t id);
    dto::IssueResponseTo Update(const dto::IssueRequestTo& request, int64_t id);
    bool Delete(int64_t id);
    std::vector<dto::IssueResponseTo> GetAll();
    
    std::vector<dto::IssueResponseTo> GetByEditorId(int64_t editorId);
    std::vector<dto::IssueResponseTo> GetRecent(int limit = 10);
};

}