#pragma once

#include <vector>
#include <cstdint>
#include <variant>
#include <drogon/orm/DbClient.h>
#include <drogon/orm/Mapper.h>
#include <drogon/HttpAppFramework.h>

#include "IDatabaseRepository.h"
#include <models/TblLabel.h>
#include <exceptions/DatabaseError.h>

namespace myapp
{

using namespace drogon_model::distcomp;

class LabelRepository : public IDatabaseRepository<TblLabel>
{
public:
    LabelRepository() = default;
    ~LabelRepository() = default;
    
    std::variant<int64_t, DatabaseError> Create(const TblLabel& entity) override;
    std::variant<TblLabel, DatabaseError> GetByID(int64_t id) override;
    std::variant<bool, DatabaseError> Update(int64_t id, const TblLabel& entity) override;
    std::variant<bool, DatabaseError> Delete(int64_t id) override;
    std::variant<std::vector<TblLabel>, DatabaseError> ReadAll() override;
    std::variant<bool, DatabaseError> Exists(int64_t id) override;
    
    std::variant<TblLabel, DatabaseError> FindByName(const std::string& name);
    std::variant<std::vector<TblLabel>, DatabaseError> FindByNameContaining(const std::string& substring);
};

};