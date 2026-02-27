#include "storage/database/IssueLabelRepository.h"
#include <drogon/orm/Criteria.h>

namespace myapp
{

using namespace drogon::orm;

int64_t IssueLabelRepository::Create(const TblIssueLabel& entity)
{
    try
    {
        return mapper.insertFuture(entity).get().getValueOfId();
    }
    catch(const std::exception& e)
    {
        return 0;
    }
}

std::optional<TblIssueLabel> IssueLabelRepository::GetByID(int64_t id)
{
    try
    {
        auto result = mapper.findByPrimaryKey(id);
        return result;
    }
    catch(const std::exception& e)
    {
        return std::nullopt;
    }
}

bool IssueLabelRepository::Update(int64_t id, const TblIssueLabel& entity)
{
    try
    {
        auto numUpdated = mapper.update(entity);
        return numUpdated ? true : false;
    }
    catch(const std::exception& e)
    {
        return false;
    }
}

bool IssueLabelRepository::Delete(int64_t id)
{
    try
    {
        return mapper.deleteByPrimaryKey(id) ? true : false;
    }
    catch(const std::exception& e)
    {
        return false;
    }
}

std::vector<TblIssueLabel> IssueLabelRepository::ReadAll()
{
    try
    {
        return mapper.findAll();
    }
    catch(const std::exception& e)
    {
        return {};
    }
}

bool IssueLabelRepository::Exists(int64_t id)
{
    try
    {
        mapper.findByPrimaryKey(id);
        return true;
    }
    catch(const std::exception& e)
    {
        return false;
    }
}

std::vector<TblIssueLabel> IssueLabelRepository::FindByIssueId(int64_t issueId)
{
    try
    {
        auto criteria = Criteria(TblIssueLabel::Cols::_issue_id, CompareOperator::EQ, issueId);
        return mapper.findBy(criteria);
    }
    catch(const std::exception& e)
    {
        return {};
    }
}

std::vector<TblIssueLabel> IssueLabelRepository::FindByLabelId(int64_t labelId)
{
    try
    {
        auto criteria = Criteria(TblIssueLabel::Cols::_label_id, CompareOperator::EQ, labelId);
        return mapper.findBy(criteria);
    }
    catch(const std::exception& e)
    {
        return {};
    }
}

std::optional<TblIssueLabel> IssueLabelRepository::FindByIssueAndLabel(int64_t issueId, int64_t labelId)
{
    try
    {
        auto criteria = Criteria(TblIssueLabel::Cols::_issue_id, CompareOperator::EQ, issueId) &&
                       Criteria(TblIssueLabel::Cols::_label_id, CompareOperator::EQ, labelId);
        return mapper.findOne(criteria);
    }
    catch(const std::exception& e)
    {
        return std::nullopt;
    }
}

std::vector<int64_t> IssueLabelRepository::FindLabelIdsByIssueId(int64_t issueId)
{
    try
    {
        auto criteria = Criteria(TblIssueLabel::Cols::_issue_id, CompareOperator::EQ, issueId);
        auto results = mapper.findBy(criteria);
        
        std::vector<int64_t> labelIds;
        labelIds.reserve(results.size());
        for (const auto& item : results)
        {
            labelIds.push_back(item.getValueOfLabelId());
        }
        return labelIds;
    }
    catch(const std::exception& e)
    {
        return {};
    }
}

std::vector<int64_t> IssueLabelRepository::FindIssueIdsByLabelId(int64_t labelId)
{
    try
    {
        auto criteria = Criteria(TblIssueLabel::Cols::_label_id, CompareOperator::EQ, labelId);
        auto results = mapper.findBy(criteria);
        
        std::vector<int64_t> issueIds;
        issueIds.reserve(results.size());
        for (const auto& item : results)
        {
            issueIds.push_back(item.getValueOfIssueId());
        }
        return issueIds;
    }
    catch(const std::exception& e)
    {
        return {};
    }
}

bool IssueLabelRepository::DeleteByIssueAndLabel(int64_t issueId, int64_t labelId)
{
    try
    {
        auto criteria = Criteria(TblIssueLabel::Cols::_issue_id, CompareOperator::EQ, issueId) &&
                        Criteria(TblIssueLabel::Cols::_label_id, CompareOperator::EQ, labelId);
        auto numDeleted = mapper.deleteBy(criteria);
        return numDeleted > 0;
    }
    catch(const std::exception& e)
    {
        return false;
    }
}

bool IssueLabelRepository::DeleteByIssueId(int64_t issueId)
{
    try
    {
        auto criteria = Criteria(TblIssueLabel::Cols::_issue_id, CompareOperator::EQ, issueId);
        auto numDeleted = mapper.deleteBy(criteria);
        return numDeleted > 0;
    }
    catch(const std::exception& e)
    {
        return false;
    }
}

bool IssueLabelRepository::DeleteByLabelId(int64_t labelId)
{
    try
    {
        auto criteria = Criteria(TblIssueLabel::Cols::_label_id, CompareOperator::EQ, labelId);
        auto numDeleted = mapper.deleteBy(criteria);
        return numDeleted > 0;
    }
    catch(const std::exception& e)
    {
        return false;
    }
}

bool IssueLabelRepository::Exists(int64_t issueId, int64_t labelId)
{
    try
    {
        auto criteria = Criteria(TblIssueLabel::Cols::_issue_id, CompareOperator::EQ, issueId) &&
                        Criteria(TblIssueLabel::Cols::_label_id, CompareOperator::EQ, labelId);
        mapper.findOne(criteria);
        return true;
    }
    catch(const std::exception& e)
    {
        return false;
    }
}

}