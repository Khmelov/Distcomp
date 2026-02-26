#pragma once

#include "IDatabaseRepository.h"
#include <entities/IssueLabel.h>

class  IssuelabelRepository : public IDatabaseRepository<IssueLabel>
{
    uint64_t Create(const IssueLabel& entity) override;
    std::optional<IssueLabel> GetByID(uint64_t id) override;
    bool Update(uint64_t id, const IssueLabel& entity) override;
    bool Delete(uint64_t id) override;
    std::vector<IssueLabel> ReadAll() override;
    bool Exists(uint64_t id) override;
};