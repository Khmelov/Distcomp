#pragma once
#include "IDatabaseRepository.h"
#include "models/TblLabel.h"


namespace publisher
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