#pragma once

#include <optional>
#include <vector>
#include <cstdint>
#include <drogon/orm/DbClient.h>
#include <drogon/orm/Mapper.h>
#include <drogon/HttpAppFramework.h>

#include "IDatabaseRepository.h"
#include <models/TblIssueLabel.h>

namespace myapp 
{

using namespace drogon_model::myapp_dev;

class IssueLabelRepository : public IDatabaseRepository<TblIssueLabel>
{
public:
    IssueLabelRepository() = default;
    ~IssueLabelRepository() = default;
    
    int64_t Create(const TblIssueLabel& entity) override;
    std::optional<TblIssueLabel> GetByID(int64_t id) override;
    bool Update(int64_t id, const TblIssueLabel& entity) override;
    bool Delete(int64_t id) override;
    std::vector<TblIssueLabel> ReadAll() override;
    bool Exists(int64_t id) override;
    
    // Дополнительные методы
    std::vector<TblIssueLabel> FindByIssueId(int64_t issueId);
    std::vector<TblIssueLabel> FindByLabelId(int64_t labelId);
    std::optional<TblIssueLabel> FindByIssueAndLabel(int64_t issueId, int64_t labelId);
    std::vector<int64_t> FindLabelIdsByIssueId(int64_t issueId);
    std::vector<int64_t> FindIssueIdsByLabelId(int64_t labelId);
    bool DeleteByIssueAndLabel(int64_t issueId, int64_t labelId);
    bool DeleteByIssueId(int64_t issueId);
    bool DeleteByLabelId(int64_t labelId);
    bool Exists(int64_t issueId, int64_t labelId);
};

};