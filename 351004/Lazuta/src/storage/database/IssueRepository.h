#pragma once

#include "IDatabaseRepository.h"
#include <entities/Issue.h>

class  IssueRepository : public IDatabaseRepository<Issue>
{
    uint64_t Create(const Issue& entity) override;
    std::optional<Issue> GetByID(uint64_t id) override;
    bool Update(uint64_t id, const Issue& entity) override;
    bool Delete(uint64_t id) override;
    std::vector<Issue> ReadAll() override;
    bool Exists(uint64_t id) override;
};