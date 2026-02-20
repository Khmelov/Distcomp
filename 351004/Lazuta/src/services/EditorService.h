#pragma once

#include <memory>
#include <dao/DAO.h>
#include <entities/Editor.h>
#include <dto/responses/EditorResponseTo.h>
#include <dto/requests/EditorRequestTo.h>
#include <mapping/Mapper.h>
#include <exceptions/DatabaseException.h>
#include <exceptions/NotFoundException.h>

class EditorService 
{
private:
    std::unique_ptr<DAO<Editor>> m_dao;
    
public:
    EditorService(std::unique_ptr<DAO<Editor>> storage): m_dao(std::move(storage)) 
    {

    }
        
    EditorResponseTo Create(const EditorRequestTo& request) 
    {
        Editor entity = Mapper::ToEntity(request);
        auto id = m_dao->Create(entity);
        std::optional<Editor> newEntity = m_dao->GetByID(id);

        if (!newEntity)
        {
            throw DatabaseException("Failed to retrieve created editor");
        }

        return Mapper::ToResponse(newEntity.value());
    }

    EditorResponseTo Read(uint64_t id) 
    {
        std::optional<Editor> entity = m_dao->GetByID(id);

        if (!entity)
        {
            throw NotFoundException("Editor not found");
        }

        return Mapper::ToResponse(entity.value());
    }

    EditorResponseTo Update(const EditorRequestTo& request, uint64_t id) 
    {
        Editor entity = Mapper::ToEntity(request);    

        if (!m_dao->Update(id, entity))
        {
            throw NotFoundException("Editor not found for update");
        }

        std::optional<Editor> newEntity = m_dao->GetByID(id);

        if (!newEntity)
        {
            throw DatabaseException("Failed to retrieve updated editor");
        }

        return Mapper::ToResponse(newEntity.value());
    }

    bool Delete(uint64_t id)
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
        return Mapper::ToResponseList(m_dao->ReadAll());
    }
};