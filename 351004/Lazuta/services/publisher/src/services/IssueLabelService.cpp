#include "IssueLabelService.h"
#include <storage/database/IssueLabelRepository.h>
#include <storage/database/IssueRepository.h>
#include <storage/database/LabelRepository.h>
#include <mapping/DtoMapper.h>
#include <exceptions/DatabaseException.h>
#include <exceptions/NotFoundException.h>
#include <exceptions/ValidationException.h>

namespace myapp
{

using namespace drogon_model::distcomp;
using namespace myapp::dto;

IssueLabelService::IssueLabelService(
    std::shared_ptr<IssueLabelRepository> storage,
    std::shared_ptr<IssueRepository> issueRepository,
    std::shared_ptr<LabelRepository> labelRepository)
    : m_dao(storage)
    , m_issueRepository(issueRepository)
    , m_labelRepository(labelRepository)
{
}

IssueLabelResponseTo IssueLabelService::Create(const IssueLabelRequestTo& request)
{
    request.validate();
    
    // Check if issue exists
    auto issueResult = m_issueRepository->GetByID(request.issueId);
    if (std::holds_alternative<DatabaseError>(issueResult))
    {
        DatabaseError error = std::get<DatabaseError>(issueResult);
        if (error == DatabaseError::NotFound)
        {
            throw ValidationException("Issue not found");
        }
        throw DatabaseException("Failed to validate issue");
    }
    
    // Check if label exists
    auto labelResult = m_labelRepository->GetByID(request.labelId);
    if (std::holds_alternative<DatabaseError>(labelResult))
    {
        DatabaseError error = std::get<DatabaseError>(labelResult);
        if (error == DatabaseError::NotFound)
        {
            throw ValidationException("Label not found");
        }
        throw DatabaseException("Failed to validate label");
    }
    
    TblIssueLabel entity = DtoMapper::ToEntity(request);
    auto result = m_dao->Create(entity);
    
    if (std::holds_alternative<DatabaseError>(result))
    {
        DatabaseError error = std::get<DatabaseError>(result);
        switch (error)
        {
            case DatabaseError::AlreadyExists:
                throw ValidationException("Issue label combination already exists");
            default:
                throw DatabaseException("Failed to create issue label");
        }
    }
    
    int64_t id = std::get<int64_t>(result);
    auto getResult = m_dao->GetByID(id);
    
    if (std::holds_alternative<DatabaseError>(getResult))
    {
        throw DatabaseException("Failed to retrieve created issue label");
    }
    
    return DtoMapper::ToResponse(std::get<TblIssueLabel>(getResult));
}

IssueLabelResponseTo IssueLabelService::Read(int64_t id)
{
    auto result = m_dao->GetByID(id);
    
    if (std::holds_alternative<DatabaseError>(result))
    {
        DatabaseError error = std::get<DatabaseError>(result);
        if (error == DatabaseError::NotFound)
        {
            throw NotFoundException("Issue label not found");
        }
        throw DatabaseException("Failed to retrieve issue label");
    }
    
    return DtoMapper::ToResponse(std::get<TblIssueLabel>(result));
}

IssueLabelResponseTo IssueLabelService::Update(const IssueLabelRequestTo& request, int64_t id)
{
    request.validate();
    
    TblIssueLabel entity = DtoMapper::ToEntityForUpdate(request, id);
    auto updateResult = m_dao->Update(id, entity);
    
    if (std::holds_alternative<DatabaseError>(updateResult))
    {
        DatabaseError error = std::get<DatabaseError>(updateResult);
        if (error == DatabaseError::NotFound)
        {
            throw NotFoundException("Issue label not found for update");
        }
        throw DatabaseException("Failed to update issue label");
    }
    
    auto getResult = m_dao->GetByID(id);
    
    if (std::holds_alternative<DatabaseError>(getResult))
    {
        throw DatabaseException("Failed to retrieve updated issue label");
    }
    
    return DtoMapper::ToResponse(std::get<TblIssueLabel>(getResult));
}

bool IssueLabelService::Delete(int64_t id)
{
    auto result = m_dao->Delete(id);
    
    if (std::holds_alternative<DatabaseError>(result))
    {
        DatabaseError error = std::get<DatabaseError>(result);
        if (error == DatabaseError::NotFound)
        {
            throw NotFoundException("Issue label not found for deletion");
        }
        throw DatabaseException("Failed to delete issue label");
    }
    
    return std::get<bool>(result);
}

std::vector<IssueLabelResponseTo> IssueLabelService::GetAll()
{
    auto result = m_dao->ReadAll();
    
    if (std::holds_alternative<DatabaseError>(result))
    {
        throw DatabaseException("Failed to retrieve all issue labels");
    }
    
    return DtoMapper::ToResponseList(std::get<std::vector<TblIssueLabel>>(result));
}

std::vector<IssueLabelResponseTo> IssueLabelService::GetByIssueId(int64_t issueId)
{
    // Check if issue exists (but don't throw, just return empty if not)
    auto issueResult = m_issueRepository->GetByID(issueId);
    if (std::holds_alternative<DatabaseError>(issueResult))
    {
        return {};
    }
    
    auto result = m_dao->FindByIssueId(issueId);
    
    if (std::holds_alternative<DatabaseError>(result))
    {
        throw DatabaseException("Failed to retrieve issue labels by issue ID");
    }
    
    return DtoMapper::ToResponseList(std::get<std::vector<TblIssueLabel>>(result));
}

std::vector<IssueLabelResponseTo> IssueLabelService::GetByLabelId(int64_t labelId)
{
    // Check if label exists (but don't throw, just return empty if not)
    auto labelResult = m_labelRepository->GetByID(labelId);
    if (std::holds_alternative<DatabaseError>(labelResult))
    {
        return {};
    }
    
    auto result = m_dao->FindByLabelId(labelId);
    
    if (std::holds_alternative<DatabaseError>(result))
    {
        throw DatabaseException("Failed to retrieve issue labels by label ID");
    }
    
    return DtoMapper::ToResponseList(std::get<std::vector<TblIssueLabel>>(result));
}

std::optional<IssueLabelResponseTo> IssueLabelService::GetByIssueAndLabel(int64_t issueId, int64_t labelId)
{
    auto result = m_dao->FindByIssueAndLabel(issueId, labelId);
    
    if (std::holds_alternative<DatabaseError>(result))
    {
        DatabaseError error = std::get<DatabaseError>(result);
        if (error != DatabaseError::None)
        {
            throw DatabaseException("Failed to retrieve issue label by issue and label");
        }
        return std::nullopt;
    }
    
    return DtoMapper::ToResponse(std::get<TblIssueLabel>(result));
}

std::vector<int64_t> IssueLabelService::GetLabelIdsByIssueId(int64_t issueId)
{
    // Check if issue exists (but don't throw, just return empty if not)
    auto issueResult = m_issueRepository->GetByID(issueId);
    if (std::holds_alternative<DatabaseError>(issueResult))
    {
        return {};
    }
    
    auto result = m_dao->FindLabelIdsByIssueId(issueId);
    
    if (std::holds_alternative<DatabaseError>(result))
    {
        throw DatabaseException("Failed to retrieve label IDs by issue ID");
    }
    
    return std::get<std::vector<int64_t>>(result);
}

std::vector<int64_t> IssueLabelService::GetIssueIdsByLabelId(int64_t labelId)
{
    // Check if label exists (but don't throw, just return empty if not)
    auto labelResult = m_labelRepository->GetByID(labelId);
    if (std::holds_alternative<DatabaseError>(labelResult))
    {
        return {};
    }
    
    auto result = m_dao->FindIssueIdsByLabelId(labelId);
    
    if (std::holds_alternative<DatabaseError>(result))
    {
        throw DatabaseException("Failed to retrieve issue IDs by label ID");
    }
    
    return std::get<std::vector<int64_t>>(result);
}

bool IssueLabelService::DeleteByIssueAndLabel(int64_t issueId, int64_t labelId)
{
    auto findResult = m_dao->FindByIssueAndLabel(issueId, labelId);
    
    if (std::holds_alternative<DatabaseError>(findResult))
    {
        DatabaseError error = std::get<DatabaseError>(findResult);
        if (error == DatabaseError::None)
        {
            throw NotFoundException("Issue label combination not found");
        }
        throw DatabaseException("Failed to find issue label combination");
    }
    
    TblIssueLabel entity = std::get<TblIssueLabel>(findResult);
    return Delete(entity.getValueOfId());
}

bool IssueLabelService::DeleteByIssueId(int64_t issueId)
{
    // Check if issue exists
    auto issueResult = m_issueRepository->GetByID(issueId);
    if (std::holds_alternative<DatabaseError>(issueResult))
    {
        DatabaseError error = std::get<DatabaseError>(issueResult);
        if (error == DatabaseError::NotFound)
        {
            return false;
        }
        throw DatabaseException("Failed to validate issue");
    }
    
    auto result = m_dao->DeleteByIssueId(issueId);
    
    if (std::holds_alternative<DatabaseError>(result))
    {
        DatabaseError error = std::get<DatabaseError>(result);
        if (error == DatabaseError::NotFound)
        {
            return false;
        }
        throw DatabaseException("Failed to delete issue labels by issue ID");
    }
    
    return std::get<bool>(result);
}

bool IssueLabelService::DeleteByLabelId(int64_t labelId)
{
    // Check if label exists
    auto labelResult = m_labelRepository->GetByID(labelId);
    if (std::holds_alternative<DatabaseError>(labelResult))
    {
        DatabaseError error = std::get<DatabaseError>(labelResult);
        if (error == DatabaseError::NotFound)
        {
            return false;
        }
        throw DatabaseException("Failed to validate label");
    }
    
    auto result = m_dao->DeleteByLabelId(labelId);
    
    if (std::holds_alternative<DatabaseError>(result))
    {
        DatabaseError error = std::get<DatabaseError>(result);
        if (error == DatabaseError::NotFound)
        {
            return false;
        }
        throw DatabaseException("Failed to delete issue labels by label ID");
    }
    
    return std::get<bool>(result);
}

bool IssueLabelService::Exists(int64_t issueId, int64_t labelId)
{
    auto result = m_dao->Exists(issueId, labelId);
    
    if (std::holds_alternative<DatabaseError>(result))
    {
        throw DatabaseException("Failed to check existence of issue label combination");
    }
    
    return std::get<bool>(result);
}

}