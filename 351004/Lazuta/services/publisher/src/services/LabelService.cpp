#include "LabelService.h"
#include <storage/database/LabelRepository.h>
#include <mapping/DtoMapper.h>
#include <exceptions/DatabaseException.h>
#include <exceptions/NotFoundException.h>

namespace publisher
{

using namespace drogon_model::distcomp;
using namespace publisher::dto;

LabelService::LabelService(std::shared_ptr<LabelRepository> storage)
    : m_dao(storage)
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
    
    return DtoMapper::ToResponse(std::get<TblLabel>(getResult));
}

LabelResponseTo LabelService::Read(int64_t id)
{
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
    
    return DtoMapper::ToResponse(std::get<TblLabel>(result));
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
    
    return std::get<bool>(result);
}

std::vector<LabelResponseTo> LabelService::GetAll()
{
    auto result = m_dao->ReadAll();
    
    if (std::holds_alternative<DatabaseError>(result))
    {
        throw DatabaseException("Failed to retrieve all labels");
    }
    
    return DtoMapper::ToResponseList(std::get<std::vector<TblLabel>>(result));
}

}