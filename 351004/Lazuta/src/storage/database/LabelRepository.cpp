#include "storage/database/LabelRepository.h"
#include <drogon/orm/Criteria.h>

namespace myapp
{

using namespace drogon::orm;

int64_t LabelRepository::Create(const TblLabel& entity)
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

std::optional<TblLabel> LabelRepository::GetByID(int64_t id)
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

bool LabelRepository::Update(int64_t id, const TblLabel& entity)
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

bool LabelRepository::Delete(int64_t id)
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

std::vector<TblLabel> LabelRepository::ReadAll()
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

bool LabelRepository::Exists(int64_t id)
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

std::optional<TblLabel> LabelRepository::FindByName(const std::string& name)
{
    try
    {
        auto criteria = Criteria(TblLabel::Cols::_name, CompareOperator::EQ, name);
        auto result = mapper.findOne(criteria);
        return result;
    }
    catch(const std::exception& e)
    {
        return std::nullopt;
    }
}

std::vector<TblLabel> LabelRepository::FindByNameContaining(const std::string& substring)
{
    try
    {
        auto criteria = Criteria(TblLabel::Cols::_name, CompareOperator::Like, "%" + substring + "%");
        return mapper.findBy(criteria);
    }
    catch(const std::exception& e)
    {
        return {};
    }
}

}