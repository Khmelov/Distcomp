#pragma once

#include <optional>
#include <vector>
#include <cstdint>
#include <drogon/orm/DbClient.h>
#include <drogon/orm/Mapper.h>
#include <drogon/HttpAppFramework.h>

#include "IDatabaseRepository.h"
#include <models/TblLabel.h>

namespace myapp 
{

using namespace drogon_model::distcomp;

class LabelRepository : public IDatabaseRepository<TblLabel>
{  
public:
    LabelRepository() = default;
    ~LabelRepository() = default;
    
    int64_t Create(const TblLabel& entity) override;
    std::optional<TblLabel> GetByID(int64_t id) override;
    bool Update(int64_t id, const TblLabel& entity) override;
    bool Delete(int64_t id) override;
    std::vector<TblLabel> ReadAll() override;
    bool Exists(int64_t id) override;
    
    // Дополнительные методы
    std::optional<TblLabel> FindByName(const std::string& name);
    std::vector<TblLabel> FindByNameContaining(const std::string& substring);
};

};