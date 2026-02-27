#pragma once

#include <memory>
#include <vector>
#include <optional>

#include <dto/responses/LabelResponseTo.h>
#include <dto/requests/LabelRequestTo.h>
#include <mapping/DtoMapper.h>
#include <models/TblLabel.h>

#include <exceptions/NotFoundException.h>
#include <exceptions/DatabaseException.h>

#include <storage/database/LabelRepository.h>

namespace myapp 
{

using namespace drogon_model::distcomp;
using namespace myapp::dto;

class LabelService 
{
private:
    std::shared_ptr<LabelRepository> m_dao;
    
public:
    LabelService(std::shared_ptr<LabelRepository> storage): m_dao(storage) 
    {

    }
        
    LabelResponseTo Create(const LabelRequestTo& request) 
    {
        TblLabel entity = DtoMapper::ToEntity(request);
        auto id = m_dao->Create(entity);
        auto newEntity = m_dao->GetByID(id);

        if (!newEntity)
        {
            throw DatabaseException("Failed to retrieve created label");
        }

        return DtoMapper::ToResponse(newEntity.value());
    }

    LabelResponseTo Read(int64_t id) 
    {
        auto entity = m_dao->GetByID(id);

        if (!entity)
        {
            throw NotFoundException("Label not found");
        }

        return DtoMapper::ToResponse(entity.value());
    }

    LabelResponseTo Update(const LabelRequestTo& request, int64_t id) 
    {
        TblLabel entity = DtoMapper::ToEntityForUpdate(request, id);    

        if (!m_dao->Update(id, entity))
        {
            throw NotFoundException("Label not found for update");
        }

        auto newEntity = m_dao->GetByID(id);

        if (!newEntity)
        {
            throw DatabaseException("Failed to retrieve updated label");
        }

        return DtoMapper::ToResponse(newEntity.value());
    }

    bool Delete(int64_t id)
    {
        if (!m_dao->Delete(id))
        {
            throw NotFoundException("Label not found for deletion");
        }
        return true;
    }

    std::vector<LabelResponseTo> GetAll()
    {
        auto entities = m_dao->ReadAll();
        return DtoMapper::ToResponseList(entities);
    }
};

};