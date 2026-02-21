#pragma once

#include <memory>
#include <dao/DAO.h>
#include <entities/Issue.h>
#include <dto/responses/IssueResponseTo.h>
#include <dto/requests/IssueRequestTo.h>
#include <mapping/Mapper.h>
#include <exceptions/DatabaseException.h>
#include <exceptions/NotFoundException.h>

class IssueService 
{
private:
    std::unique_ptr<DAO<Issue>> m_dao;
    
public:
    IssueService(std::unique_ptr<DAO<Issue>> storage): m_dao(std::move(storage)) 
    {

    }
        
    IssueResponseTo Create(const IssueRequestTo& request) 
    {
        Issue entity = Mapper::ToEntity(request);
        auto id = m_dao->Create(entity);
        std::optional<Issue> newEntity = m_dao->GetByID(id);

        if (!newEntity)
        {
            throw DatabaseException("Failed to retrieve created issue");
        }

        return Mapper::ToResponse(newEntity.value());
    }

    IssueResponseTo Read(uint64_t id) 
    {
        std::optional<Issue> entity = m_dao->GetByID(id);

        if (!entity)
        {
            throw NotFoundException("Issue not found");
        }

        return Mapper::ToResponse(entity.value());
    }

    IssueResponseTo Update(const IssueRequestTo& request, uint64_t id) 
    {
        Issue entity = Mapper::ToEntity(request);    

        if (!m_dao->Update(id, entity))
        {
            throw NotFoundException("Issue not found for update");
        }

        std::optional<Issue> newEntity = m_dao->GetByID(id);

        if (!newEntity)
        {
            throw DatabaseException("Failed to retrieve updated issue");
        }

        return Mapper::ToResponse(newEntity.value());
    }

    bool Delete(uint64_t id)
    {
        if (!m_dao->Delete(id))
        {
            throw NotFoundException("Issue not found for deletion");
        }
        return true;
    }

    std::vector<IssueResponseTo> GetAll()
    {
        return Mapper::ToResponseList(m_dao->ReadAll());
    }
};