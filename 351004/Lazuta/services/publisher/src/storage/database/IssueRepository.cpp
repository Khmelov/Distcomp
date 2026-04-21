#include "storage/database/IssueRepository.h"
#include <drogon/orm/Criteria.h>
#include "IssueRepository.h"

namespace publisher
{

using namespace drogon::orm;

std::variant<int64_t, DatabaseError> IssueRepository::Create(const TblIssue& entity)
{
    try
    {
        return Mapper().insertFuture(entity).get().getValueOfId();
    }
    catch(const std::exception& e)
    {
        std::cout << e.what();
        return DatabaseError::DatabaseError;
    }
}

std::variant<TblIssue, DatabaseError> IssueRepository::GetByID(int64_t id)
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

std::variant<bool, DatabaseError> IssueRepository::Update(int64_t id, const TblIssue& entity)
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

std::variant<bool, DatabaseError> IssueRepository::Delete(int64_t id)
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

std::variant<std::vector<TblIssue>, DatabaseError> IssueRepository::ReadAll()
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

std::variant<bool, DatabaseError> IssueRepository::Exists(int64_t id)
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

std::variant<std::vector<TblIssue>, DatabaseError> IssueRepository::FindByEditorId(int64_t editorId)
{
    try
    {
        auto criteria = Criteria(TblIssue::Cols::_editor_id, CompareOperator::EQ, editorId);
        return Mapper().findBy(criteria);
    }
    catch(const std::exception& e)
    {
        return DatabaseError::DatabaseError;
    }
}


std::variant<std::vector<TblIssue>, DatabaseError> IssueRepository::FindByTitle(const std::string& title)
{
    try
    {
        auto criteria = Criteria(TblIssue::Cols::_title, CompareOperator::EQ, title);
        return Mapper().findBy(criteria);
    }
    catch(const std::exception& e)
    {
        return DatabaseError::DatabaseError;
    }
}

std::variant<std::vector<TblIssue>, DatabaseError> IssueRepository::FindRecent(int limit)
{
    try
    {
        auto criteria = Criteria(TblIssue::Cols::_created, CompareOperator::LT, trantor::Date::date());
        
        return Mapper().orderBy(TblIssue::Cols::_created, SortOrder::DESC).limit(limit).findBy(criteria);
    }
    catch(const std::exception& e)
    {
        return DatabaseError::DatabaseError;
    }
}

std::variant<std::vector<TblIssue>, DatabaseError> IssueRepository::FindByDateRange(const trantor::Date& from, const trantor::Date& to)
{
    try
    {
        auto criteria = Criteria(TblIssue::Cols::_created, CompareOperator::GE, from) &&
                        Criteria(TblIssue::Cols::_created, CompareOperator::LE, to);
        return Mapper().orderBy(TblIssue::Cols::_created, SortOrder::ASC).findBy(criteria);
    }
    catch(const std::exception& e)
    {
        return DatabaseError::DatabaseError;
    }
}

}