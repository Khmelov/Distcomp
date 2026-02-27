#include "storage/database/IssueRepository.h"
#include <drogon/orm/Criteria.h>

namespace myapp
{

using namespace drogon::orm;

int64_t IssueRepository::Create(const TblIssue& entity)
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

std::optional<TblIssue> IssueRepository::GetByID(int64_t id)
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

bool IssueRepository::Update(int64_t id, const TblIssue& entity)
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

bool IssueRepository::Delete(int64_t id)
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

std::vector<TblIssue> IssueRepository::ReadAll()
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

bool IssueRepository::Exists(int64_t id)
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

std::vector<TblIssue> IssueRepository::FindByEditorId(int64_t editorId)
{
    try
    {
        auto criteria = Criteria(TblIssue::Cols::_editor_id, CompareOperator::EQ, editorId);
        return mapper.findBy(criteria);
    }
    catch(const std::exception& e)
    {
        return {};
    }
}

std::vector<TblIssue> IssueRepository::FindRecent(int limit)
{
    try
    {
        auto criteria = Criteria(TblIssue::Cols::_created, CompareOperator::LT, trantor::Date::date());
        
        return mapper.orderBy(TblIssue::Cols::_created, SortOrder::DESC).limit(limit).findBy(criteria);
    }
    catch(const std::exception& e)
    {
        return {};
    }
}

std::vector<TblIssue> IssueRepository::FindByDateRange(const trantor::Date& from, const trantor::Date& to)
{
    try
    {
        auto criteria = Criteria(TblIssue::Cols::_created, CompareOperator::GE, from) &&
                        Criteria(TblIssue::Cols::_created, CompareOperator::LE, to);
        return mapper.orderBy(TblIssue::Cols::_created, SortOrder::ASC).findBy(criteria);
    }
    catch(const std::exception& e)
    {
        return {};
    }
}

}