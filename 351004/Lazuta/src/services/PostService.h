#pragma once

#include <memory>
#include <dao/DAO.h>
#include <entities/Post.h>
#include <dto/responses/PostResponseTo.h>
#include <dto/requests/PostRequestTo.h>
#include <mapping/Mapper.h>
#include <exceptions/NotFoundException.h>
#include <exceptions/DatabaseException.h>

class PostService 
{
private:
    std::unique_ptr<DAO<::Post>> m_dao;
    
public:
    PostService(std::unique_ptr<DAO<::Post>> storage): m_dao(std::move(storage)) 
    {

    }
        
    PostResponseTo Create(const PostRequestTo& request) 
    {
        ::Post entity = Mapper::ToEntity(request);
        auto id = m_dao->Create(entity);
        std::optional<::Post> newEntity = m_dao->GetByID(id);

        if (!newEntity)
        {
            throw DatabaseException("Failed to retrieve created post");
        }

        return Mapper::ToResponse(newEntity.value());
    }

    PostResponseTo Read(uint64_t id) 
    {
        std::optional<::Post> entity = m_dao->GetByID(id);

        if (!entity)
        {
            throw NotFoundException("Post not found");
        }

        return Mapper::ToResponse(entity.value());
    }

    PostResponseTo Update(const PostRequestTo& request, uint64_t id) 
    {
        ::Post entity = Mapper::ToEntity(request);    

        if (!m_dao->Update(id, entity))
        {
            throw NotFoundException("Post not found for update");
        }

        std::optional<::Post> newEntity = m_dao->GetByID(id);

        if (!newEntity)
        {
            throw DatabaseException("Failed to retrieve updated post");
        }

        return Mapper::ToResponse(newEntity.value());
    }

    bool Delete(uint64_t id)
    {
        if (!m_dao->Delete(id))
        {
            throw NotFoundException("Post not found for deletion");
        }
        return true;
    }

    std::vector<PostResponseTo> GetAll()
    {
        return Mapper::ToResponseList(m_dao->ReadAll());
    }
};
