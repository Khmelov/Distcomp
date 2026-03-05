#include "storage/database/EditorRepository.h"
#include "EditorRepository.h"

namespace myapp
{

using namespace drogon::orm;

std::variant<int64_t, DatabaseError> EditorRepository::Create(const TblEditor& entity)
{
    try
    {
        auto editorsWithSameLogin = Mapper().findBy(Criteria(TblEditor::Cols::_login, CompareOperator::Like, entity.getValueOfLogin()));
        if (editorsWithSameLogin.size())
        {
            return DatabaseError::AlreadyExists;
        }
        
        return Mapper().insertFuture(entity).get().getValueOfId();
    }
    catch(const std::exception& e)
    {
        return DatabaseError::DatabaseError;
    }
}

std::variant<TblEditor, DatabaseError> EditorRepository::GetByID(int64_t id)
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

std::variant<bool, DatabaseError> EditorRepository::Update(int64_t id, const TblEditor& entity)
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

std::variant<bool, DatabaseError> EditorRepository::Delete(int64_t id)
{
    try
    {
        if (Mapper().deleteByPrimaryKey(id))
            return true;
        else
            return DatabaseError::NotFound;
    }
    catch(const std::exception& e)
    {
        return DatabaseError::DatabaseError;
    }
}

std::variant<std::vector<TblEditor>, DatabaseError> EditorRepository::ReadAll()
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

std::variant<bool, DatabaseError> EditorRepository::Exists(int64_t id)
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

}