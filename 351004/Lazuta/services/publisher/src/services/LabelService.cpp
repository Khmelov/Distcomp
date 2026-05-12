#include "LabelService.h"
#include <storage/database/LabelRepository.h>
#include <storage/cache/LabelCache.h>
#include <mapping/DtoMapper.h>
#include <exceptions/DatabaseException.h>
#include <exceptions/NotFoundException.h>

namespace publisher
{

using namespace drogon_model::distcomp;
using namespace publisher::dto;

LabelService::LabelService(std::shared_ptr<LabelRepository> storage, std::shared_ptr<LabelCache> cache)
    : m_dao(storage)
    , m_cache(cache)
{
}

LabelResponseTo LabelService::Create(const LabelRequestTo& request)
{
    request.validate();
    
    TblLabel entity = DtoMapper::ToEntity(request);
    auto result = m_dao->Create(entity);
    
    if (std::holds_alternative<DatabaseError>(result))
    {
        throw DatabaseException("Failed to create label");
    }
    
    int64_t id = std::get<int64_t>(result);
    auto getResult = m_dao->GetByID(id);
    
    if (std::holds_alternative<DatabaseError>(getResult))
    {
        throw DatabaseException("Failed to retrieve created label");
    }
    
    auto entityResult = std::get<TblLabel>(getResult);
    m_cache->Create(entityResult);
    
    return DtoMapper::ToResponse(entityResult);
}

LabelResponseTo LabelService::Read(int64_t id)
{
    auto cacheResult = m_cache->GetByID(id);
    
    if (std::holds_alternative<TblLabel>(cacheResult))
    {
        return DtoMapper::ToResponse(std::get<TblLabel>(cacheResult));
    }
    
    auto result = m_dao->GetByID(id);
    
    if (std::holds_alternative<DatabaseError>(result))
    {
        DatabaseError error = std::get<DatabaseError>(result);
        if (error == DatabaseError::NotFound)
        {
            throw NotFoundException("Label not found");
        }
        throw DatabaseException("Failed to retrieve label");
    }
    
    auto entity = std::get<TblLabel>(result);
    m_cache->Create(entity);
    
    return DtoMapper::ToResponse(entity);
}

LabelResponseTo LabelService::Update(const LabelRequestTo& request, int64_t id)
{
    request.validate();
    
    TblLabel entity = DtoMapper::ToEntityForUpdate(request, id);
    auto updateResult = m_dao->Update(id, entity);
    
    if (std::holds_alternative<DatabaseError>(updateResult))
    {
        DatabaseError error = std::get<DatabaseError>(updateResult);
        if (error == DatabaseError::NotFound)
        {
            throw NotFoundException("Label not found for update");
        }
        throw DatabaseException("Failed to update label");
    }
    
    auto getResult = m_dao->GetByID(id);
    
    if (std::holds_alternative<DatabaseError>(getResult))
    {
        throw DatabaseException("Failed to retrieve updated label");
    }

    m_cache->Update(id, entity);
    
    return DtoMapper::ToResponse(std::get<TblLabel>(getResult));
}

bool LabelService::Delete(int64_t id)
{
    auto result = m_dao->Delete(id);
    
    if (std::holds_alternative<DatabaseError>(result))
    {
        DatabaseError error = std::get<DatabaseError>(result);
        if (error == DatabaseError::NotFound)
        {
            throw NotFoundException("Label not found for deletion");
        }
        throw DatabaseException("Failed to delete label");
    }
    
    m_cache->Delete(id);
    
    return std::get<bool>(result);
}

std::vector<LabelResponseTo> LabelService::GetAll()
{
    auto cacheResult = m_cache->ReadAll();
    
    if (std::holds_alternative<std::vector<TblLabel>>(cacheResult))
    {
        auto& labels = std::get<std::vector<TblLabel>>(cacheResult);
        if (!labels.empty())
        {
            return DtoMapper::ToResponseList(labels);
        }
    }
    
    auto result = m_dao->ReadAll();
    
    if (std::holds_alternative<DatabaseError>(result))
    {
        throw DatabaseException("Failed to retrieve all labels");
    }
    
    auto& labels = std::get<std::vector<TblLabel>>(result);
    for (const auto& label : labels)
    {
        m_cache->Create(label);
    }
    
    return DtoMapper::ToResponseList(labels);
}

}
