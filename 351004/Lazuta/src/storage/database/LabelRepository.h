#pragma once

#include "IDatabaseRepository.h"
#include <entities/Label.h>

class LabelRepository : public IDatabaseRepository<Label>
{
    uint64_t Create(const Label& entity) override;
    std::optional<Label> GetByID(uint64_t id) override;
    bool Update(uint64_t id, const Label& entity) override;
    bool Delete(uint64_t id) override;
    std::vector<Label> ReadAll() override;
    bool Exists(uint64_t id) override;
};