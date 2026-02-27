#include "storage/database/EditorRepository.h"
#include <drogon/orm/Mapper.h>
#include <drogon/orm/Criteria.h>
#include <exceptions/DatabaseException.h>

namespace myapp
{

using namespace drogon::orm;

int64_t EditorRepository::Create(const TblEditor& entity)
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

std::optional<TblEditor> EditorRepository::GetByID(int64_t id)
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

bool EditorRepository::Update(int64_t id, const TblEditor& entity)
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

bool EditorRepository::Delete(int64_t id)
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

std::vector<TblEditor> EditorRepository::ReadAll()
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

bool EditorRepository::Exists(int64_t id)
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

}