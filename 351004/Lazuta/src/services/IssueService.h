#pragma once

#include <memory>
#include <vector>
#include <optional>

#include <models/TblIssue.h>
#include <models/TblEditor.h>
#include <mapping/DtoMapper.h>

#include <dto/responses/IssueResponseTo.h>
#include <dto/requests/IssueRequestTo.h>

#include <exceptions/DatabaseException.h>
#include <exceptions/NotFoundException.h>

#include <storage/database/IssueRepository.h>
#include <storage/database/EditorRepository.h>

namespace myapp 
{

using namespace drogon_model::myapp_dev;
using namespace myapp::dto;

class IssueService 
{
private:
    std::shared_ptr<IssueRepository> m_dao;
    std::shared_ptr<EditorRepository> m_editorRepository;
    
public:
    IssueService(std::shared_ptr<IssueRepository> storage, std::shared_ptr<EditorRepository> editorRepository): 
        m_dao(storage), m_editorRepository(editorRepository)
    {

    }
        
    IssueResponseTo Create(const IssueRequestTo& request) 
    {
        TblIssue entity = DtoMapper::ToEntity(request);
        auto id = m_dao->Create(entity);
        auto newEntity = m_dao->GetByID(id);

        if (!newEntity)
        {
            throw DatabaseException("Failed to retrieve created issue");
        }

        return DtoMapper::ToResponse(newEntity.value());
    }

    IssueResponseTo Read(int64_t id) 
    {
        auto entity = m_dao->GetByID(id);

        if (!entity)
        {
            throw NotFoundException("Issue not found");
        }

        return DtoMapper::ToResponse(entity.value());
    }

    IssueResponseTo Update(const IssueRequestTo& request, int64_t id) 
    {
        TblIssue entity = DtoMapper::ToEntityForUpdate(request, id);    

        if (!m_dao->Update(id, entity))
        {
            throw NotFoundException("Issue not found for update");
        }

        auto newEntity = m_dao->GetByID(id);

        if (!newEntity)
        {
            throw DatabaseException("Failed to retrieve updated issue");
        }

        return DtoMapper::ToResponse(newEntity.value());
    }

    bool Delete(int64_t id)
    {
        if (!m_dao->Delete(id))
        {
            throw NotFoundException("Issue not found for deletion");
        }
        return true;
    }

    std::vector<IssueResponseTo> GetAll()
    {
        auto entities = m_dao->ReadAll();
        return DtoMapper::ToResponseList(entities);
    }

    std::vector<IssueResponseTo> GetByEditorId(int64_t editorId)
    {
        auto entities = m_dao->FindByEditorId(editorId);
        return DtoMapper::ToResponseList(entities);
    }

    std::vector<IssueResponseTo> GetRecent(int limit = 10)
    {
        auto entities = m_dao->FindRecent(limit);
        return DtoMapper::ToResponseList(entities);
    }
};

};