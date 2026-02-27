#pragma once

#include <memory>
#include <vector>
#include <optional>

#include <models/TblPost.h>
#include <models/TblIssue.h>

#include <dto/responses/PostResponseTo.h>
#include <dto/requests/PostRequestTo.h>
#include <mapping/DtoMapper.h>

#include <exceptions/NotFoundException.h>
#include <exceptions/DatabaseException.h>

#include <storage/database/PostRepository.h>
#include <storage/database/IssueRepository.h>

namespace myapp 
{

using namespace drogon_model::myapp_dev;
using namespace myapp::dto;

class PostService 
{
private:
    std::shared_ptr<PostRepository> m_dao;
    std::shared_ptr<IssueRepository> m_issueRepository;
    
public:
    PostService(std::shared_ptr<PostRepository> storage, std::shared_ptr<IssueRepository> issueRepository): 
        m_dao(storage), m_issueRepository(issueRepository)
    {

    }
        
    PostResponseTo Create(const PostRequestTo& request) 
    {
        TblPost entity = DtoMapper::ToEntity(request);
        auto id = m_dao->Create(entity);
        auto newEntity = m_dao->GetByID(id);

        if (!newEntity)
        {
            throw DatabaseException("Failed to retrieve created post");
        }

        return DtoMapper::ToResponse(newEntity.value());
    }

    PostResponseTo Read(int64_t id) 
    {
        auto entity = m_dao->GetByID(id);

        if (!entity)
        {
            throw NotFoundException("Post not found");
        }

        return DtoMapper::ToResponse(entity.value());
    }

    PostResponseTo Update(const PostRequestTo& request, int64_t id) 
    {
        TblPost entity = DtoMapper::ToEntityForUpdate(request, id);    

        if (!m_dao->Update(id, entity))
        {
            throw NotFoundException("Post not found for update");
        }

        auto newEntity = m_dao->GetByID(id);

        if (!newEntity)
        {
            throw DatabaseException("Failed to retrieve updated post");
        }

        return DtoMapper::ToResponse(newEntity.value());
    }

    bool Delete(int64_t id)
    {
        if (!m_dao->Delete(id))
        {
            throw NotFoundException("Post not found for deletion");
        }
        return true;
    }

    std::vector<PostResponseTo> GetAll()
    {
        auto entities = m_dao->ReadAll();
        return DtoMapper::ToResponseList(entities);
    }
};

};