#include "storage/database/IssueLabelRepository.h"
#include <drogon/orm/Criteria.h>

namespace publisher
{

using namespace drogon::orm;

std::variant<int64_t, DatabaseError> IssueLabelRepository::Create(const TblIssueLabel& entity)
{
    try
    {
        auto sameLabels = FindByIssueAndLabel(*entity.getIssueId(), *entity.getLabelId());

        if (std::get<DatabaseError>(sameLabels) == DatabaseError::None)
            return Mapper().insertFuture(entity).get().getValueOfId();
        
        return DatabaseError::DatabaseError;
    }
    // sameLabels has a value of found entity
    catch (std::bad_variant_access& e)
    {
        return DatabaseError::AlreadyExists;
    }
    catch(const std::exception& e)
    {
        return DatabaseError::DatabaseError;
    }
}

std::variant<TblIssueLabel, DatabaseError> IssueLabelRepository::GetByID(int64_t id)
{
    try
    {
        auto result = Mapper().findByPrimaryKey(id);
        return result;
    }
    catch (const UnexpectedRows& e)
    {
        return DatabaseError::NotFound;
    }
    catch(const std::exception& e)
    {
        return DatabaseError::DatabaseError;
    }
}

std::variant<bool, DatabaseError> IssueLabelRepository::Update(int64_t id, const TblIssueLabel& entity)
{
    try
    {
        auto numUpdated = Mapper().update(entity);
        if (numUpdated)
            return true;

        return DatabaseError::NotFound;
    }
    catch(const std::exception& e)
    {
        return DatabaseError::DatabaseError;
    }
}

std::variant<bool, DatabaseError> IssueLabelRepository::Delete(int64_t id)
{
    try
    {
        if (Mapper().deleteByPrimaryKey(id))
            return true;

        return DatabaseError::NotFound;
    }
    catch(const std::exception& e)
    {
        return DatabaseError::DatabaseError;
    }
}

std::variant<std::vector<TblIssueLabel>, DatabaseError> IssueLabelRepository::ReadAll()
{
    try
    {
        return Mapper().findAll();
    }
    catch(const std::exception& e)
    {
        return DatabaseError::DatabaseError;
    }
}

std::variant<bool, DatabaseError> IssueLabelRepository::Exists(int64_t id)
{
    try
    {
        Mapper().findByPrimaryKey(id);
        return true;
    }
    catch (const UnexpectedRows& e)
    {
        return false;
    }
    catch(const std::exception& e)
    {
        return DatabaseError::DatabaseError;
    }
}

std::variant<std::vector<TblIssueLabel>, DatabaseError> IssueLabelRepository::FindByIssueId(int64_t issueId)
{
    try
    {
        auto criteria = Criteria(TblIssueLabel::Cols::_issue_id, CompareOperator::EQ, issueId);
        return Mapper().findBy(criteria);
    }
    catch(const std::exception& e)
    {
        return DatabaseError::DatabaseError;
    }
}

std::variant<std::vector<TblIssueLabel>, DatabaseError> IssueLabelRepository::FindByLabelId(int64_t labelId)
{
    try
    {
        auto criteria = Criteria(TblIssueLabel::Cols::_label_id, CompareOperator::EQ, labelId);
        return Mapper().findBy(criteria);
    }
    catch(const std::exception& e)
    {
        return DatabaseError::DatabaseError;
    }
}

std::variant<TblIssueLabel, DatabaseError> IssueLabelRepository::FindByIssueAndLabel(int64_t issueId, int64_t labelId)
{
    try
    {
        auto criteria = Criteria(TblIssueLabel::Cols::_issue_id, CompareOperator::EQ, issueId) &&
                       Criteria(TblIssueLabel::Cols::_label_id, CompareOperator::EQ, labelId);
        return Mapper().findOne(criteria);
    }
    catch (const UnexpectedRows& e)
    {
        return DatabaseError::None;
    }
    catch(const std::exception& e)
    {
        return DatabaseError::DatabaseError;
    }
}

std::variant<std::vector<int64_t>, DatabaseError> IssueLabelRepository::FindLabelIdsByIssueId(int64_t issueId)
{
    try
    {
        auto criteria = Criteria(TblIssueLabel::Cols::_issue_id, CompareOperator::EQ, issueId);
        auto results = Mapper().findBy(criteria);
        
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
        return DatabaseError::DatabaseError;
    }
}

std::variant<std::vector<int64_t>, DatabaseError> IssueLabelRepository::FindIssueIdsByLabelId(int64_t labelId)
{
    try
    {
        auto criteria = Criteria(TblIssueLabel::Cols::_label_id, CompareOperator::EQ, labelId);
        auto results = Mapper().findBy(criteria);
        
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
        return DatabaseError::DatabaseError;
    }
}

std::variant<bool, DatabaseError> IssueLabelRepository::DeleteByIssueAndLabel(int64_t issueId, int64_t labelId)
{
    try
    {
        auto criteria = Criteria(TblIssueLabel::Cols::_issue_id, CompareOperator::EQ, issueId) &&
                        Criteria(TblIssueLabel::Cols::_label_id, CompareOperator::EQ, labelId);
        auto numDeleted = Mapper().deleteBy(criteria);
        if (numDeleted)
            return true;
        
        return DatabaseError::NotFound;
    }
    catch(const std::exception& e)
    {
        return DatabaseError::DatabaseError;
    }
}

std::variant<bool, DatabaseError> IssueLabelRepository::DeleteByIssueId(int64_t issueId)
{
    try
    {
        auto criteria = Criteria(TblIssueLabel::Cols::_issue_id, CompareOperator::EQ, issueId);
        auto numDeleted = Mapper().deleteBy(criteria);

        if (numDeleted)
            return true;
        
        return DatabaseError::NotFound;
    }
    catch(const std::exception& e)
    {
        return DatabaseError::DatabaseError;
    }
}

std::variant<bool, DatabaseError> IssueLabelRepository::DeleteByLabelId(int64_t labelId)
{
    try
    {
        auto criteria = Criteria(TblIssueLabel::Cols::_label_id, CompareOperator::EQ, labelId);
        auto numDeleted = Mapper().deleteBy(criteria);
        
        if (numDeleted)
            return true;
        
        return DatabaseError::NotFound;
    }
    catch(const std::exception& e)
    {
        return DatabaseError::DatabaseError;
    }
}

std::variant<bool, DatabaseError> IssueLabelRepository::Exists(int64_t issueId, int64_t labelId)
{
    try
    {
        auto criteria = Criteria(TblIssueLabel::Cols::_issue_id, CompareOperator::EQ, issueId) &&
                        Criteria(TblIssueLabel::Cols::_label_id, CompareOperator::EQ, labelId);
        Mapper().findOne(criteria);
        return true;
    }
    catch (const UnexpectedRows& e)
    {
        return false;
    }
    catch(const std::exception& e)
    {
        return DatabaseError::DatabaseError;
    }
}

}