#include "PostService.h"
#include <storage/database/PostRepository.h>
#include <storage/database/IssueRepository.h>

#include <mapping/DtoMapper.h>
#include <exceptions/DatabaseException.h>
#include <exceptions/NotFoundException.h>
#include <exceptions/ValidationException.h>
#include <json/json.h>

namespace publisher
{

using namespace drogon_model::distcomp;
using namespace publisher::dto;

PostService::PostService(
    std::shared_ptr<PostRepository> storage,
    std::shared_ptr<IssueRepository> issueRepository,
    std::unique_ptr<KafkaProducer> kafkaProducer)
    : m_dao(storage)
    , m_issueRepository(issueRepository)
    , m_kafkaProducer(std::move(kafkaProducer))
{
}

PostResponseTo PostService::Create(const PostRequestTo& request)
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
    
    TblPost entity = DtoMapper::ToEntity(request);
    auto result = m_dao->Create(entity);
    
    if (std::holds_alternative<DatabaseError>(result))
    {
        throw DatabaseException("Failed to create post");
    }
    
    int64_t id = std::get<int64_t>(result);
    auto getResult = m_dao->GetByID(id);
    
    if (std::holds_alternative<DatabaseError>(getResult))
    {
        throw DatabaseException("Failed to retrieve created post");
    }

    if (m_kafkaProducer)
    {
        Json::Value kafkaMsg;
        kafkaMsg["id"] = id;
        kafkaMsg["issueId"] = request.issueId;
        kafkaMsg["content"] = request.content;
        
        std::string key = std::to_string(request.issueId);
        m_kafkaProducer->Send(key, Json::FastWriter().write(kafkaMsg));
        std::cout << "[KAFKA] Sent post " << id << " to InTopic for moderation" << std::endl;
    }
    
    return DtoMapper::ToResponse(std::get<TblPost>(getResult));
}

PostResponseTo PostService::Read(int64_t id)
{
    auto result = m_dao->GetByID(id);
    
    if (std::holds_alternative<DatabaseError>(result))
    {
        DatabaseError error = std::get<DatabaseError>(result);
        if (error == DatabaseError::NotFound)
        {
            throw NotFoundException("Post not found");
        }
        throw DatabaseException("Failed to retrieve post");
    }
    
    return DtoMapper::ToResponse(std::get<TblPost>(result));
}

PostResponseTo PostService::Update(const PostRequestTo& request, int64_t id)
{
    request.validate();
    
    TblPost entity = DtoMapper::ToEntityForUpdate(request, id);
    auto updateResult = m_dao->Update(id, entity);
    
    if (std::holds_alternative<DatabaseError>(updateResult))
    {
        DatabaseError error = std::get<DatabaseError>(updateResult);
        if (error == DatabaseError::NotFound)
        {
            throw NotFoundException("Post not found for update");
        }
        throw DatabaseException("Failed to update post");
    }
    
    auto getResult = m_dao->GetByID(id);
    
    if (std::holds_alternative<DatabaseError>(getResult))
    {
        throw DatabaseException("Failed to retrieve updated post");
    }
    
    return DtoMapper::ToResponse(std::get<TblPost>(getResult));
}

bool PostService::Delete(int64_t id)
{
    auto result = m_dao->Delete(id);
    
    if (std::holds_alternative<DatabaseError>(result))
    {
        DatabaseError error = std::get<DatabaseError>(result);
        if (error == DatabaseError::NotFound)
        {
            throw NotFoundException("Post not found for deletion");
        }
        throw DatabaseException("Failed to delete post");
    }
    
    return std::get<bool>(result);
}

std::vector<PostResponseTo> PostService::GetAll()
{
    auto result = m_dao->ReadAll();
    
    if (std::holds_alternative<DatabaseError>(result))
    {
        throw DatabaseException("Failed to retrieve all posts");
    }
    
    return DtoMapper::ToResponseList(std::get<std::vector<TblPost>>(result));
}

}