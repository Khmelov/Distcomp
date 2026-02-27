#include "IssueService.h"
#include <storage/database/IssueRepository.h>
#include <storage/database/EditorRepository.h>
#include <mapping/DtoMapper.h>
#include <exceptions/DatabaseException.h>
#include <exceptions/NotFoundException.h>
#include <exceptions/ValidationException.h>

namespace myapp
{

using namespace drogon_model::distcomp;
using namespace myapp::dto;

IssueService::IssueService(
    std::shared_ptr<IssueRepository> storage,
    std::shared_ptr<EditorRepository> editorRepository)
    : m_dao(storage)
    , m_editorRepository(editorRepository)
{
}

IssueResponseTo IssueService::Create(const IssueRequestTo& request)
{
    request.validate();
    
    // Check if editor exists
    auto editorResult = m_editorRepository->GetByID(request.editorId);
    if (std::holds_alternative<DatabaseError>(editorResult))
    {
        DatabaseError error = std::get<DatabaseError>(editorResult);
        if (error == DatabaseError::NotFound)
        {
            throw ValidationException("Editor not found");
        }
        throw DatabaseException("Failed to validate editor");
    }

    auto titleResult = m_dao->FindByTitle(request.title);
    if (std::holds_alternative<std::vector<TblIssue>>(titleResult))
    {
        if (std::get<std::vector<TblIssue>>(titleResult).size())
        {
            throw ValidationException("Issue with this title already exists");
        }        
    }
    
    TblIssue entity = DtoMapper::ToEntity(request);
    auto result = m_dao->Create(entity);
    
    if (std::holds_alternative<DatabaseError>(result))
    {
        throw DatabaseException("Failed to create issue");
    }
    
    int64_t id = std::get<int64_t>(result);
    auto getResult = m_dao->GetByID(id);
    
    if (std::holds_alternative<DatabaseError>(getResult))
    {
        throw DatabaseException("Failed to retrieve created issue");
    }
    
    return DtoMapper::ToResponse(std::get<TblIssue>(getResult));
}

IssueResponseTo IssueService::Read(int64_t id)
{
    auto result = m_dao->GetByID(id);
    
    if (std::holds_alternative<DatabaseError>(result))
    {
        DatabaseError error = std::get<DatabaseError>(result);
        if (error == DatabaseError::NotFound)
        {
            throw NotFoundException("Issue not found");
        }
        throw DatabaseException("Failed to retrieve issue");
    }
    
    return DtoMapper::ToResponse(std::get<TblIssue>(result));
}

IssueResponseTo IssueService::Update(const IssueRequestTo& request, int64_t id)
{
    request.validate();
    
    TblIssue entity = DtoMapper::ToEntityForUpdate(request, id);
    auto updateResult = m_dao->Update(id, entity);
    
    if (std::holds_alternative<DatabaseError>(updateResult))
    {
        DatabaseError error = std::get<DatabaseError>(updateResult);
        if (error == DatabaseError::NotFound)
        {
            throw NotFoundException("Issue not found for update");
        }
        throw DatabaseException("Failed to update issue");
    }
    
    auto getResult = m_dao->GetByID(id);
    
    if (std::holds_alternative<DatabaseError>(getResult))
    {
        throw DatabaseException("Failed to retrieve updated issue");
    }
    
    return DtoMapper::ToResponse(std::get<TblIssue>(getResult));
}

bool IssueService::Delete(int64_t id)
{
    auto result = m_dao->Delete(id);
    
    if (std::holds_alternative<DatabaseError>(result))
    {
        DatabaseError error = std::get<DatabaseError>(result);
        if (error == DatabaseError::NotFound)
        {
            throw NotFoundException("Issue not found for deletion");
        }
        throw DatabaseException("Failed to delete issue");
    }
    
    return std::get<bool>(result);
}

std::vector<IssueResponseTo> IssueService::GetAll()
{
    auto result = m_dao->ReadAll();
    
    if (std::holds_alternative<DatabaseError>(result))
    {
        throw DatabaseException("Failed to retrieve all issues");
    }
    
    return DtoMapper::ToResponseList(std::get<std::vector<TblIssue>>(result));
}

std::vector<IssueResponseTo> IssueService::GetByEditorId(int64_t editorId)
{
    auto result = m_dao->FindByEditorId(editorId);
    
    if (std::holds_alternative<DatabaseError>(result))
    {
        throw DatabaseException("Failed to retrieve issues by editor ID");
    }
    
    return DtoMapper::ToResponseList(std::get<std::vector<TblIssue>>(result));
}

std::vector<IssueResponseTo> IssueService::GetRecent(int limit)
{
    auto result = m_dao->FindRecent(limit);
    
    if (std::holds_alternative<DatabaseError>(result))
    {
        throw DatabaseException("Failed to retrieve recent issues");
    }
    
    return DtoMapper::ToResponseList(std::get<std::vector<TblIssue>>(result));
}

}