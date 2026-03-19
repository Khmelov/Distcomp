#include "storage/database/PostRepository.h"
#include <drogon/orm/Criteria.h>

namespace publisher
{

using namespace drogon::orm;

std::variant<int64_t, DatabaseError> PostRepository::Create(const TblPost& entity)
{
    try
    {
        return Mapper().insertFuture(entity).get().getValueOfId();
    }
    catch(const std::exception& e)
    {
        return DatabaseError::DatabaseError;
    }
}

std::variant<TblPost, DatabaseError> PostRepository::GetByID(int64_t id)
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

std::variant<bool, DatabaseError> PostRepository::Update(int64_t id, const TblPost& entity)
{
    try
    {
        auto numUpdated = Mapper().update(entity);
        return numUpdated ? true : false;
    }
    catch(const std::exception& e)
    {
        return DatabaseError::DatabaseError;
    }
}

std::variant<bool, DatabaseError> PostRepository::Delete(int64_t id)
{
    try
    {
        return Mapper().deleteByPrimaryKey(id) ? true : false;
    }
    catch(const std::exception& e)
    {
        return DatabaseError::DatabaseError;
    }
}

std::variant<std::vector<TblPost>, DatabaseError> PostRepository::ReadAll()
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

std::variant<bool, DatabaseError> PostRepository::Exists(int64_t id)
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

std::variant<std::vector<TblPost>, DatabaseError> PostRepository::FindByIssueId(int64_t issueId)
{
    try
    {
        auto criteria = Criteria(TblPost::Cols::_issue_id, CompareOperator::EQ, issueId);
        return Mapper().findBy(criteria);
    }
    catch(const std::exception& e)
    {
        return DatabaseError::DatabaseError;
    }
}

std::variant<std::vector<TblPost>, DatabaseError> PostRepository::FindRecentByIssue(int64_t issueId, int limit)
{
    try
    {
        auto criteria = Criteria(TblPost::Cols::_issue_id, CompareOperator::EQ, issueId);
        return Mapper().orderBy(TblPost::Cols::_id, SortOrder::DESC).limit(limit).findBy(criteria);
    }
    catch(const std::exception& e)
    {
        return DatabaseError::DatabaseError;
    }
}

std::variant<std::vector<TblPost>, DatabaseError> PostRepository::FindByContentContaining(const std::string& searchText)
{
    try
    {
        auto criteria = Criteria(TblPost::Cols::_content, CompareOperator::Like, "%" + searchText + "%");
        return Mapper().findBy(criteria);
    }
    catch(const std::exception& e)
    {
        return DatabaseError::DatabaseError;
    }
}

}