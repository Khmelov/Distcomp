#pragma once

#include <vector>
#include <cstdint>
#include <variant>
#include <drogon/orm/DbClient.h>
#include <drogon/orm/Mapper.h>
#include <drogon/HttpAppFramework.h>

#include "IDatabaseRepository.h"
#include <models/TblPost.h>
#include <exceptions/DatabaseError.h>

namespace myapp
{

using namespace drogon_model::distcomp;

class PostRepository : public IDatabaseRepository<TblPost>
{
public:
    PostRepository() = default;
    ~PostRepository() = default;
    
    std::variant<int64_t, DatabaseError> Create(const TblPost& entity) override;
    std::variant<TblPost, DatabaseError> GetByID(int64_t id) override;
    std::variant<bool, DatabaseError> Update(int64_t id, const TblPost& entity) override;
    std::variant<bool, DatabaseError> Delete(int64_t id) override;
    std::variant<std::vector<TblPost>, DatabaseError> ReadAll() override;
    std::variant<bool, DatabaseError> Exists(int64_t id) override;
    
    std::variant<std::vector<TblPost>, DatabaseError> FindByIssueId(int64_t issueId);
    std::variant<std::vector<TblPost>, DatabaseError> FindRecentByIssue(int64_t issueId, int limit = 10);
    std::variant<std::vector<TblPost>, DatabaseError> FindByContentContaining(const std::string& searchText);
};

};