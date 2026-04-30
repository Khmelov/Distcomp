//
// Created by dmitry on 30.04.2026.
//

#ifndef DISTCOMP_POSTCACHE_H
#define DISTCOMP_POSTCACHE_H

#include <vector>
#include <cstdint>
#include <variant>
#include <string>
#include <models/TblPost.h>
#include <exceptions/DatabaseError.h>

namespace publisher
{

class PostCache
{
public:
    PostCache() = default;
    ~PostCache() = default;

    std::variant<int64_t, DatabaseError> Create(const drogon_model::distcomp::TblPost& entity);
    std::variant<drogon_model::distcomp::TblPost, DatabaseError> GetByID(int64_t id);
    std::variant<bool, DatabaseError> Update(int64_t id, const drogon_model::distcomp::TblPost& entity);
    std::variant<bool, DatabaseError> Delete(int64_t id);
    std::variant<std::vector<drogon_model::distcomp::TblPost>, DatabaseError> ReadAll();
    std::variant<bool, DatabaseError> Exists(int64_t id);
    std::variant<std::vector<drogon_model::distcomp::TblPost>, DatabaseError> FindByIssueId(int64_t issueId);
};

}

#endif //DISTCOMP_POSTCACHE_H
