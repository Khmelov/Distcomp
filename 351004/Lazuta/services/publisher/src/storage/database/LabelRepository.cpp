#include "storage/database/LabelRepository.h"
#include <drogon/orm/Criteria.h>

namespace publisher
{

using namespace drogon::orm;

std::variant<int64_t, DatabaseError> LabelRepository::Create(const TblLabel& entity)
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

std::variant<TblLabel, DatabaseError> LabelRepository::GetByID(int64_t id)
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

std::variant<bool, DatabaseError> LabelRepository::Update(int64_t id, const TblLabel& entity)
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

std::variant<bool, DatabaseError> LabelRepository::Delete(int64_t id)
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

std::variant<std::vector<TblLabel>, DatabaseError> LabelRepository::ReadAll()
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

std::variant<bool, DatabaseError> LabelRepository::Exists(int64_t id)
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

std::variant<TblLabel, DatabaseError> LabelRepository::FindByName(const std::string& name)
{
    try
    {
        auto criteria = Criteria(TblLabel::Cols::_name, CompareOperator::EQ, name);
        auto result = Mapper().findOne(criteria);
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

std::variant<std::vector<TblLabel>, DatabaseError> LabelRepository::FindByNameContaining(const std::string& substring)
{
    try
    {
        auto criteria = Criteria(TblLabel::Cols::_name, CompareOperator::Like, "%" + substring + "%");
        return Mapper().findBy(criteria);
    }
    catch(const std::exception& e)
    {
        return DatabaseError::DatabaseError;
    }
}

}