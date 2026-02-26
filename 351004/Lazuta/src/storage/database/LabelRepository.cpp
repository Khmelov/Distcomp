#include "LabelRepository.h"

uint64_t LabelRepository::Create(const Label& entity)
{
    return 0;
}

std::optional<Label> LabelRepository::GetByID(uint64_t id)
{
    return std::optional<Label>();
}

bool LabelRepository::Update(uint64_t id, const Label& entity)
{
    return false;
}

bool LabelRepository::Delete(uint64_t id)
{
    return false;
}

std::vector<Label> LabelRepository::ReadAll()
{
    return std::vector<Label>();
}

bool LabelRepository::Exists(uint64_t id)
{
    return false;
}
