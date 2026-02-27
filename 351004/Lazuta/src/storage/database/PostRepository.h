#pragma once

#include <optional>
#include <vector>
#include <cstdint>
#include <drogon/orm/DbClient.h>
#include <drogon/orm/Mapper.h>
#include <drogon/HttpAppFramework.h>

#include "IDatabaseRepository.h"
#include <models/TblPost.h>

namespace myapp 
{

using namespace drogon_model::distcomp;

class PostRepository : public IDatabaseRepository<TblPost>
{  
public:
    PostRepository() = default;
    ~PostRepository() = default;
    
    int64_t Create(const TblPost& entity) override;
    std::optional<TblPost> GetByID(int64_t id) override;
    bool Update(int64_t id, const TblPost& entity) override;
    bool Delete(int64_t id) override;
    std::vector<TblPost> ReadAll() override;
    bool Exists(int64_t id) override;
    
    // Дополнительные методы
    std::vector<TblPost> FindByIssueId(int64_t issueId);
    std::vector<TblPost> FindRecentByIssue(int64_t issueId, int limit = 10);
    std::vector<TblPost> FindByContentContaining(const std::string& searchText);
};

};