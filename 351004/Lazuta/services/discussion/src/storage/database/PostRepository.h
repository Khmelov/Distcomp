#pragma once

#include <vector>
#include <cstdint>
#include <variant>
#include <string>
#include <mongocxx/client.hpp>
#include <mongocxx/collection.hpp>
#include <bsoncxx/document/value.hpp>
#include <bsoncxx/document/view.hpp>
#include <bsoncxx/builder/stream/document.hpp>

#include "IMongoDBRepository.h"
#include <models/TblPost.h>
#include <exceptions/DatabaseError.h>

namespace discussion
{

class PostRepository : public IMongoDBRepository<TblPost>
{
public:
    PostRepository();
    ~PostRepository() = default;

    std::variant<int64_t, DatabaseError> Create(const TblPost& entity);
    std::variant<TblPost, DatabaseError> GetByID(int64_t id);
    std::variant<bool, DatabaseError> Update(int64_t id, const TblPost& entity);
    std::variant<bool, DatabaseError> Delete(int64_t id);
    std::variant<std::vector<TblPost>, DatabaseError> ReadAll();
    std::variant<bool, DatabaseError> Exists(int64_t id);
    
    std::variant<std::vector<TblPost>, DatabaseError> FindByIssueId(int64_t issueId);
    std::variant<std::vector<TblPost>, DatabaseError> FindRecentByIssue(int64_t issueId, int limit = 10);
    std::variant<std::vector<TblPost>, DatabaseError> FindByContentContaining(const std::string& searchText);

private:
    int64_t GetNextId();
    bsoncxx::document::value ToBson(const TblPost& entity);
    TblPost FromBson(const bsoncxx::document::view& doc);
};

}