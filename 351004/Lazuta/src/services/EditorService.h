#pragma once

#include <memory>
#include <vector>
#include <optional>

#include <models/TblEditor.h>
#include <mapping/DtoMapper.h>
#include <dto/requests/EditorRequestTo.h>
#include <dto/responses/EditorResponseTo.h>

#include <exceptions/DatabaseException.h>
#include <exceptions/NotFoundException.h>

#include <storage/database/EditorRepository.h>

namespace myapp 
{

using namespace drogon_model::myapp_dev;
using namespace myapp::dto;

class EditorService 
{
private:
    std::shared_ptr<EditorRepository> m_dao;
    
public:
    EditorService(std::shared_ptr<EditorRepository> storage): m_dao(storage) 
    {

    }
        
    EditorResponseTo Create(const EditorRequestTo& request) 
    {
        TblEditor entity = DtoMapper::ToEntity(request);
        auto id = m_dao->Create(entity);
        auto newEntity = m_dao->GetByID(id);

        if (!newEntity)
        {
            throw DatabaseException("Failed to retrieve created editor");
        }

        return DtoMapper::ToResponse(newEntity.value());
    }

    EditorResponseTo Read(int64_t id) 
    {
        auto entity = m_dao->GetByID(id);

        if (!entity)
        {
            throw NotFoundException("Editor not found");
        }

        return DtoMapper::ToResponse(entity.value());
    }

    EditorResponseTo Update(const EditorRequestTo& request, int64_t id) 
    {
        TblEditor entity = DtoMapper::ToEntityForUpdate(request, id);    

        if (!m_dao->Update(id, entity))
        {
            throw NotFoundException("Editor not found for update");
        }

        auto newEntity = m_dao->GetByID(id);

        if (!newEntity)
        {
            throw DatabaseException("Failed to retrieve updated editor");
        }

        return DtoMapper::ToResponse(newEntity.value());
    }

    bool Delete(int64_t id)
    {
        bool result = m_dao->Delete(id);
        if (!result)
        {
            throw NotFoundException("Editor not found for deletion");
        }
        return result;
    }

    std::vector<EditorResponseTo> GetAll()
    {
        auto entities = m_dao->ReadAll();
        return DtoMapper::ToResponseList(entities);
    }

    // Дополнительный метод для авторизации
    std::optional<EditorResponseTo> FindByLogin(const std::string& login)
    {
        auto entity = m_dao->FindByLogin(login);
        if (!entity)
        {
            return std::nullopt;
        }
        return DtoMapper::ToResponse(entity.value());
    }
};

};