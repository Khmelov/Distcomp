#include "EditorService.h"
#include <storage/database/EditorRepository.h>
#include <mapping/DtoMapper.h>
#include <exceptions/DatabaseException.h>
#include <exceptions/NotFoundException.h>
#include <exceptions/ValidationException.h>

namespace myapp
{

using namespace drogon_model::distcomp;
using namespace myapp::dto;

EditorService::EditorService(std::shared_ptr<EditorRepository> storage)
    : m_dao(storage)
{
}

EditorResponseTo EditorService::Create(const EditorRequestTo& request)
{
    request.validate();
    
    TblEditor entity = DtoMapper::ToEntity(request);
    auto result = m_dao->Create(entity);
    
    if (std::holds_alternative<DatabaseError>(result))
    {
        DatabaseError error = std::get<DatabaseError>(result);
        switch (error)
        {
            case DatabaseError::AlreadyExists:
                throw ValidationException("Editor with this login already exists");
            default:
                throw DatabaseException("Failed to create editor");
        }
    }
    
    int64_t id = std::get<int64_t>(result);
    auto getResult = m_dao->GetByID(id);
    
    if (std::holds_alternative<DatabaseError>(getResult))
    {
        throw DatabaseException("Failed to retrieve created editor");
    }
    
    return DtoMapper::ToResponse(std::get<TblEditor>(getResult));
}

EditorResponseTo EditorService::Read(int64_t id)
{
    auto result = m_dao->GetByID(id);
    
    if (std::holds_alternative<DatabaseError>(result))
    {
        DatabaseError error = std::get<DatabaseError>(result);
        if (error == DatabaseError::NotFound)
        {
            throw NotFoundException("Editor not found");
        }
        throw DatabaseException("Failed to retrieve editor");
    }
    
    return DtoMapper::ToResponse(std::get<TblEditor>(result));
}

EditorResponseTo EditorService::Update(const EditorRequestTo& request, int64_t id)
{
    request.validate();
    
    TblEditor entity = DtoMapper::ToEntityForUpdate(request, id);
    auto updateResult = m_dao->Update(id, entity);
    
    if (std::holds_alternative<DatabaseError>(updateResult))
    {
        DatabaseError error = std::get<DatabaseError>(updateResult);
        if (error == DatabaseError::NotFound)
        {
            throw NotFoundException("Editor not found for update");
        }
        throw DatabaseException("Failed to update editor");
    }
    
    auto getResult = m_dao->GetByID(id);
    
    if (std::holds_alternative<DatabaseError>(getResult))
    {
        throw DatabaseException("Failed to retrieve updated editor");
    }
    
    return DtoMapper::ToResponse(std::get<TblEditor>(getResult));
}

bool EditorService::Delete(int64_t id)
{
    auto result = m_dao->Delete(id);
    
    if (std::holds_alternative<DatabaseError>(result))
    {
        DatabaseError error = std::get<DatabaseError>(result);
        if (error == DatabaseError::NotFound)
        {
            throw NotFoundException("Editor not found for deletion");
        }
        throw DatabaseException("Failed to delete editor");
    }
    
    return std::get<bool>(result);
}

std::vector<EditorResponseTo> EditorService::GetAll()
{
    auto result = m_dao->ReadAll();
    
    if (std::holds_alternative<DatabaseError>(result))
    {
        throw DatabaseException("Failed to retrieve all editors");
    }
    
    return DtoMapper::ToResponseList(std::get<std::vector<TblEditor>>(result));
}

}