#pragma once

#include "IDatabaseRepository.h"
#include <entities/Editor.h>

class EditorRepository : public IDatabaseRepository<Editor>
{
    uint64_t Create(const Editor& entity) override;
    std::optional<Editor> GetByID(uint64_t id) override;
    bool Update(uint64_t id, const Editor& entity) override;
    bool Delete(uint64_t id) override;
    std::vector<Editor> ReadAll() override;
    bool Exists(uint64_t id) override;
};