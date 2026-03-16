#pragma once

#include <memory>
#include <vector>
#include <optional>

#include <dto/responses/IssueLabelResponseTo.h>
#include <dto/requests/IssueLabelRequestTo.h>

namespace myapp
{

class IssueLabelRepository;
class IssueRepository;
class LabelRepository;

class IssueLabelService 
{
private:
    std::shared_ptr<IssueLabelRepository> m_dao;
    std::shared_ptr<IssueRepository> m_issueRepository;
    std::shared_ptr<LabelRepository> m_labelRepository;
    
public:
    IssueLabelService(
        std::shared_ptr<IssueLabelRepository> storage,
        std::shared_ptr<IssueRepository> issueRepository,
        std::shared_ptr<LabelRepository> labelRepository);
    
    dto::IssueLabelResponseTo Create(const dto::IssueLabelRequestTo& request);
    dto::IssueLabelResponseTo Read(int64_t id);
    dto::IssueLabelResponseTo Update(const dto::IssueLabelRequestTo& request, int64_t id);
    bool Delete(int64_t id);
    std::vector<dto::IssueLabelResponseTo> GetAll();
    
    std::vector<dto::IssueLabelResponseTo> GetByIssueId(int64_t issueId);
    std::vector<dto::IssueLabelResponseTo> GetByLabelId(int64_t labelId);
    std::optional<dto::IssueLabelResponseTo> GetByIssueAndLabel(int64_t issueId, int64_t labelId);
    std::vector<int64_t> GetLabelIdsByIssueId(int64_t issueId);
    std::vector<int64_t> GetIssueIdsByLabelId(int64_t labelId);
    bool DeleteByIssueAndLabel(int64_t issueId, int64_t labelId);
    bool DeleteByIssueId(int64_t issueId);
    bool DeleteByLabelId(int64_t labelId);
    bool Exists(int64_t issueId, int64_t labelId);
};

}