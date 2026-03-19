#pragma once

#include <memory>
#include <vector>

#include <dto/responses/IssueResponseTo.h>
#include <dto/requests/IssueRequestTo.h>

namespace publisher
{

class IssueRepository;
class EditorRepository;
class LabelRepository;
class IssueLabelRepository;

class IssueService 
{
private:
    std::shared_ptr<IssueRepository> m_dao;
    std::shared_ptr<EditorRepository> m_editorRepository;
    std::shared_ptr<LabelRepository> m_labelRepository;
    std::shared_ptr<IssueLabelRepository> m_issueLabelRepository;
    
    std::vector<int64_t> ProcessLabels(const std::vector<std::string>& labelNames);
    
public:
    IssueService(
        std::shared_ptr<IssueRepository> storage,
        std::shared_ptr<EditorRepository> editorRepository,
        std::shared_ptr<LabelRepository> labelRepository,
        std::shared_ptr<IssueLabelRepository> issueLabelRepository);
    
    dto::IssueResponseTo Create(const dto::IssueRequestTo& request);
    dto::IssueResponseTo Read(int64_t id);
    dto::IssueResponseTo Update(const dto::IssueRequestTo& request, int64_t id);
    bool Delete(int64_t id);
    std::vector<dto::IssueResponseTo> GetAll();
    
    std::vector<dto::IssueResponseTo> GetByEditorId(int64_t editorId);
    std::vector<dto::IssueResponseTo> GetRecent(int limit = 10);
};

}