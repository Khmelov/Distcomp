#pragma once

#include <vector>
#include <cstdint>
#include <variant>
#include <drogon/orm/Mapper.h>
#include <drogon/orm/DbClient.h>
#include <drogon/HttpAppFramework.h>

#include "IDatabaseRepository.h"
#include <models/TblIssueLabel.h>
#include <exceptions/DatabaseError.h>

namespace publisher
{

using namespace drogon_model::distcomp;

class IssueLabelRepository : public IDatabaseRepository<TblIssueLabel>
{
public:
    IssueLabelRepository() = default;
    ~IssueLabelRepository() = default;
    
    std::variant<int64_t, DatabaseError> Create(const TblIssueLabel& entity) override;
    std::variant<TblIssueLabel, DatabaseError> GetByID(int64_t id) override;
    std::variant<bool, DatabaseError> Update(int64_t id, const TblIssueLabel& entity) override;
    std::variant<bool, DatabaseError> Delete(int64_t id) override;
    std::variant<std::vector<TblIssueLabel>, DatabaseError> ReadAll() override;
    std::variant<bool, DatabaseError> Exists(int64_t id) override;
    
    std::variant<std::vector<TblIssueLabel>, DatabaseError> FindByIssueId(int64_t issueId);
    std::variant<std::vector<TblIssueLabel>, DatabaseError> FindByLabelId(int64_t labelId);
    std::variant<TblIssueLabel, DatabaseError> FindByIssueAndLabel(int64_t issueId, int64_t labelId);
    std::variant<std::vector<int64_t>, DatabaseError> FindLabelIdsByIssueId(int64_t issueId);
    std::variant<std::vector<int64_t>, DatabaseError> FindIssueIdsByLabelId(int64_t labelId);
    std::variant<bool, DatabaseError> DeleteByIssueAndLabel(int64_t issueId, int64_t labelId);
    std::variant<bool, DatabaseError> DeleteByIssueId(int64_t issueId);
    std::variant<bool, DatabaseError> DeleteByLabelId(int64_t labelId);
    std::variant<bool, DatabaseError> Exists(int64_t issueId, int64_t labelId);
};

};