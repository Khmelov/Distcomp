#pragma once

#include <memory>
#include <vector>
#include <optional>

#include <dto/responses/IssueLabelResponseTo.h>
#include <dto/requests/IssueLabelRequestTo.h>
#include <models/TblIssueLabel.h>
#include <mapping/DtoMapper.h>

#include <exceptions/NotFoundException.h>
#include <exceptions/DatabaseException.h>

#include <storage/database/IssueLabelRepository.h>
#include <storage/database/IssueRepository.h>
#include <storage/database/LabelRepository.h>

namespace myapp 
{

using namespace drogon_model::myapp_dev;
using namespace myapp::dto;

class IssueLabelService 
{
private:
    std::shared_ptr<IssueLabelRepository> m_dao;
    std::shared_ptr<IssueRepository> m_issueRepository;
    std::shared_ptr<LabelRepository> m_labelRepository;
public:
    IssueLabelService(std::shared_ptr<IssueLabelRepository> storage,
                    std::shared_ptr<IssueRepository> issueRepository,
                    std::shared_ptr<LabelRepository> labelRepository): 
        m_dao(storage), m_issueRepository(issueRepository), m_labelRepository(labelRepository)
    {

    }
        
    IssueLabelResponseTo Create(const IssueLabelRequestTo& request) 
    {
        TblIssueLabel entity = DtoMapper::ToEntity(request);
        auto id = m_dao->Create(entity);
        auto newEntity = m_dao->GetByID(id);

        if (!newEntity)
        {
            throw DatabaseException("Failed to retrieve created issue label");
        }

        return DtoMapper::ToResponse(newEntity.value());
    }

    IssueLabelResponseTo Read(int64_t id) 
    {
        auto entity = m_dao->GetByID(id);

        if (!entity)
        {
            throw NotFoundException("Issue label not found");
        }

        return DtoMapper::ToResponse(entity.value());
    }

    IssueLabelResponseTo Update(const IssueLabelRequestTo& request, int64_t id) 
    {
        TblIssueLabel entity = DtoMapper::ToEntityForUpdate(request, id);    

        if (!m_dao->Update(id, entity))
        {
            throw NotFoundException("Issue label not found for update");
        }

        auto newEntity = m_dao->GetByID(id);

        if (!newEntity)
        {
            throw DatabaseException("Failed to retrieve updated issue label");
        }

        return DtoMapper::ToResponse(newEntity.value());
    }

    bool Delete(int64_t id)
    {
        if (!m_dao->Delete(id))
        {
            throw NotFoundException("Issue label not found for deletion");
        }
        return true;
    }

    std::vector<IssueLabelResponseTo> GetAll()
    {
        auto entities = m_dao->ReadAll();
        return DtoMapper::ToResponseList(entities);
    }

    std::vector<IssueLabelResponseTo> GetByIssueId(int64_t issueId)
    {
        try 
        {
            m_issueRepository->GetByID(issueId);
        } 
        catch (const NotFoundException& e) 
        {
            return {};
        }
        
        auto entities = m_dao->FindByIssueId(issueId);
        return DtoMapper::ToResponseList(entities);
    }

    std::vector<IssueLabelResponseTo> GetByLabelId(int64_t labelId)
    {
        try 
        {
            m_labelRepository->GetByID(labelId);
        } 
        catch (const NotFoundException& e) 
        {
            return {};
        }
        
        auto entities = m_dao->FindByLabelId(labelId);
        return DtoMapper::ToResponseList(entities);
    }

    std::optional<IssueLabelResponseTo> GetByIssueAndLabel(int64_t issueId, int64_t labelId)
    {
        auto entity = m_dao->FindByIssueAndLabel(issueId, labelId);
        
        if (!entity) {
            return std::nullopt;
        }
        
        return DtoMapper::ToResponse(entity.value());
    }

    bool DeleteByIssueAndLabel(int64_t issueId, int64_t labelId)
    {
        auto entity = m_dao->FindByIssueAndLabel(issueId, labelId);
        if (!entity) 
        {
            throw NotFoundException("Issue label combination not found");
        }
        
        return m_dao->Delete(entity.value().getValueOfId());
    }

    bool DeleteByIssueId(int64_t issueId)
    {
        try 
        {
            m_issueRepository->GetByID(issueId);
        } 
        catch (const NotFoundException& e) 
        {
            return false;
        }
        
        return m_dao->DeleteByIssueId(issueId);
    }

    bool DeleteByLabelId(int64_t labelId)
    {
        try 
        {
            m_labelRepository->GetByID(labelId);
        } 
        catch (const NotFoundException& e) 
        {
            return false;
        }
        
        return m_dao->DeleteByLabelId(labelId);
    }

    std::vector<int64_t> GetLabelIdsByIssueId(int64_t issueId)
    {
        try 
        {
            m_issueRepository->GetByID(issueId);
        } 
        catch (const NotFoundException& e) 
        {
            return {};
        }
        
        return m_dao->FindLabelIdsByIssueId(issueId);
    }

    std::vector<int64_t> GetIssueIdsByLabelId(int64_t labelId)
    {
        try 
        {
            m_labelRepository->GetByID(labelId);
        } 
        catch (const NotFoundException& e) 
        {
            return {};
        }
        
        return m_dao->FindIssueIdsByLabelId(labelId);
    }

    bool Exists(int64_t issueId, int64_t labelId)
    {
        return m_dao->Exists(issueId, labelId);
    }
};

};