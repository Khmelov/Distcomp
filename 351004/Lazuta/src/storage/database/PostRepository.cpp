#include "storage/database/PostRepository.h"
#include <drogon/orm/Criteria.h>

namespace myapp
{

using namespace drogon::orm;

int64_t PostRepository::Create(const TblPost& entity)
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

std::optional<TblPost> PostRepository::GetByID(int64_t id)
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

bool PostRepository::Update(int64_t id, const TblPost& entity)
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

bool PostRepository::Delete(int64_t id)
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

std::vector<TblPost> PostRepository::ReadAll()
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

bool PostRepository::Exists(int64_t id)
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

std::vector<TblPost> PostRepository::FindByIssueId(int64_t issueId)
{
    try
    {
        auto criteria = Criteria(TblPost::Cols::_issue_id, CompareOperator::EQ, issueId);
        return mapper.findBy(criteria);
    }
    catch(const std::exception& e)
    {
        return {};
    }
}

std::vector<TblPost> PostRepository::FindRecentByIssue(int64_t issueId, int limit)
{
    try
    {
        auto criteria = Criteria(TblPost::Cols::_issue_id, CompareOperator::EQ, issueId);
        return mapper.orderBy(TblPost::Cols::_id, SortOrder::DESC).limit(limit).findBy(criteria);
    }
    catch(const std::exception& e)
    {
        return {};
    }
}

std::vector<TblPost> PostRepository::FindByContentContaining(const std::string& searchText)
{
    try
    {
        auto criteria = Criteria(TblPost::Cols::_content, CompareOperator::Like, "%" + searchText + "%");
        return mapper.findBy(criteria);
    }
    catch(const std::exception& e)
    {
        return {};
    }
}

}