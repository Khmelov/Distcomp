//
// Created by dmitry on 30.04.2026.
//

#ifndef DISTCOMP_LABELCACHE_H
#define DISTCOMP_LABELCACHE_H

#include <vector>
#include <cstdint>
#include <variant>
#include <string>
#include <models/TblLabel.h>
#include <exceptions/DatabaseError.h>

namespace publisher
{

    using namespace drogon_model::distcomp;

class LabelCache {
public:
    LabelCache() = default;
    ~LabelCache() = default;

    std::variant<int64_t, DatabaseError> Create(const drogon_model::distcomp::TblLabel& entity);
    std::variant<TblLabel, DatabaseError> GetByID(int64_t id);
    std::variant<bool, DatabaseError> Update(int64_t id, const TblLabel& entity);
    std::variant<bool, DatabaseError> Delete(int64_t id);
    std::variant<std::vector<TblLabel>, DatabaseError> ReadAll();
    std::variant<bool, DatabaseError> Exists(int64_t id);

    std::variant<TblLabel, DatabaseError> FindByName(const std::string& name);
    std::variant<std::vector<TblLabel>, DatabaseError> FindByNameContaining(const std::string& substring);
};

}

#endif //DISTCOMP_LABELCACHE_H
