#include "IssueService.h"
#include <storage/database/IssueRepository.h>
#include <storage/database/EditorRepository.h>
#include <storage/database/LabelRepository.h>
#include <storage/database/IssueLabelRepository.h>
#include <mapping/DtoMapper.h>
#include <exceptions/DatabaseException.h>
#include <exceptions/NotFoundException.h>
#include <exceptions/ValidationException.h>

namespace publisher
{

using namespace drogon_model::distcomp;
using namespace publisher::dto;

IssueService::IssueService(
    std::shared_ptr<IssueRepository> storage,
    std::shared_ptr<EditorRepository> editorRepository,
    std::shared_ptr<LabelRepository> labelRepository,
    std::shared_ptr<IssueLabelRepository> issueLabelRepository)
    : m_dao(storage)
    , m_editorRepository(editorRepository)
    , m_labelRepository(labelRepository)
    , m_issueLabelRepository(issueLabelRepository)
{
}

std::vector<int64_t> IssueService::ProcessLabels(const std::vector<std::string>& labelNames)
{
    std::vector<int64_t> labelIds;
    labelIds.reserve(labelNames.size());
    
    for (const auto& labelName : labelNames)
    {
        auto existingLabel = m_labelRepository->FindByName(labelName);
        
        if (std::holds_alternative<TblLabel>(existingLabel))
        {
            labelIds.push_back(std::get<TblLabel>(existingLabel).getValueOfId());
        }
        else
        {
            TblLabel newLabel;
            newLabel.setName(labelName);
            
            auto createResult = m_labelRepository->Create(newLabel);
            
            if (std::holds_alternative<DatabaseError>(createResult))
            {
                throw DatabaseException("Failed to create label: " + labelName);
            }
            
            labelIds.push_back(std::get<int64_t>(createResult));
        }
    }
    
    return labelIds;
}

IssueResponseTo IssueService::Create(const IssueRequestTo& request)
{
    request.validate();
    
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
    
    int64_t issueId = std::get<int64_t>(result);
    
    if (!request.labels.empty())
    {
        auto labelIds = ProcessLabels(request.labels);
        
        for (int64_t labelId : labelIds)
        {
            TblIssueLabel issueLabel;
            issueLabel.setIssueId(issueId);
            issueLabel.setLabelId(labelId);
            
            auto linkResult = m_issueLabelRepository->Create(issueLabel);
            
            if (std::holds_alternative<DatabaseError>(linkResult))
            {
                throw DatabaseException("Failed to link label to issue");
            }
        }
    }
    
    auto getResult = m_dao->GetByID(issueId);
    
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
    
    if (!request.labels.empty())
    {
        m_issueLabelRepository->DeleteByIssueId(id);
        
        auto labelIds = ProcessLabels(request.labels);
        
        for (int64_t labelId : labelIds)
        {
            TblIssueLabel issueLabel;
            issueLabel.setIssueId(id);
            issueLabel.setLabelId(labelId);
            
            auto linkResult = m_issueLabelRepository->Create(issueLabel);
            
            if (std::holds_alternative<DatabaseError>(linkResult))
            {
                throw DatabaseException("Failed to link label to issue");
            }
        }
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
    auto deleteLabelsResult = m_issueLabelRepository->FindByIssueId(id);
    if (std::holds_alternative<std::vector<TblIssueLabel>>(deleteLabelsResult))
    {
        for (auto& label: std::get<std::vector<TblIssueLabel>>(deleteLabelsResult))
        {
            m_issueLabelRepository->Delete(label.getValueOfId());
            m_labelRepository->Delete(label.getValueOfLabelId());
        }
            
    }
    else
    {
        throw DatabaseException("Failed to delete issue labels");
    }
    
    if (std::holds_alternative<DatabaseError>(deleteLabelsResult))
    {
        DatabaseError error = std::get<DatabaseError>(deleteLabelsResult);
        if (error != DatabaseError::NotFound)
        {
            throw DatabaseException("Failed to delete issue labels");
        }
    }

    m_issueLabelRepository->DeleteByIssueId(id);
    
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