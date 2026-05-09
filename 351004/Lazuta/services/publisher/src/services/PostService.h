#pragma once

#include <memory>
#include <vector>

#include <kafka/producer/KafkaProducer.h>
#include <dto/responses/PostResponseTo.h>
#include <dto/requests/PostRequestTo.h>


namespace publisher
{

class PostRepository;
class IssueRepository;
class PostCache;

class PostService 
{
private:
    std::shared_ptr<PostRepository> m_dao;
    std::shared_ptr<IssueRepository> m_issueRepository;
    std::unique_ptr<KafkaProducer> m_kafkaProducer; 
    std::shared_ptr<PostCache> m_cache;
    
public:
    PostService(
        std::shared_ptr<PostRepository> storage,
        std::shared_ptr<IssueRepository> issueRepository,
        std::unique_ptr<KafkaProducer> kafkaProducer,
        std::shared_ptr<PostCache> cache);
    
    dto::PostResponseTo Create(const dto::PostRequestTo& request);
    dto::PostResponseTo Read(int64_t id);
    dto::PostResponseTo Update(const dto::PostRequestTo& request, int64_t id);
    bool Delete(int64_t id);
    std::vector<dto::PostResponseTo> GetAll();
};

}