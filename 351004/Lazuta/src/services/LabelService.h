#pragma once

#include <memory>
#include <dao/DAO.h>
#include <entities/Label.h>
#include <dto/responses/LabelResponseTo.h>
#include <dto/requests/LabelRequestTo.h>
#include <mapping/Mapper.h>
#include <exceptions/NotFoundException.h>
#include <exceptions/DatabaseException.h>

class LabelService 
{
private:
    std::unique_ptr<DAO<Label>> m_dao;
    
public:
    LabelService(std::unique_ptr<DAO<Label>> storage): m_dao(std::move(storage)) 
    {

    }
        
    LabelResponseTo Create(const LabelRequestTo& request) 
    {
        Label entity = Mapper::ToEntity(request);
        auto id = m_dao->Create(entity);
        std::optional<Label> newEntity = m_dao->GetByID(id);

        if (!newEntity)
        {
            throw DatabaseException("Failed to retrieve created label");
        }

        return Mapper::ToResponse(newEntity.value());
    }

    LabelResponseTo Read(uint64_t id) 
    {
        std::optional<Label> entity = m_dao->GetByID(id);

        if (!entity)
        {
            throw NotFoundException("Label not found");
        }

        return Mapper::ToResponse(entity.value());
    }

    LabelResponseTo Update(const LabelRequestTo& request, uint64_t id) 
    {
        Label entity = Mapper::ToEntity(request);    

        if (!m_dao->Update(id, entity))
        {
            throw NotFoundException("Label not found for update");
        }

        std::optional<Label> newEntity = m_dao->GetByID(id);

        if (!newEntity)
        {
            throw DatabaseException("Failed to retrieve updated label");
        }

        return Mapper::ToResponse(newEntity.value());
    }

    bool Delete(uint64_t id)
    {
        if (!m_dao->Delete(id))
        {
            throw NotFoundException("Label not found for deletion");
        }
        return true;
    }

    std::vector<LabelResponseTo> GetAll()
    {
        return Mapper::ToResponseList(m_dao->ReadAll());
    }
};