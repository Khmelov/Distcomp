#include "EditorRepository.h"

uint64_t EditorRepository::Create(const Editor& entity)
{
    return 0;
}

std::optional<Editor> EditorRepository::GetByID(uint64_t id)
{
    return std::optional<Editor>();
}

bool EditorRepository::Update(uint64_t id, const Editor& entity)
{
    return false;
}

bool EditorRepository::Delete(uint64_t id)
{
    return false;
}

std::vector<Editor> EditorRepository::ReadAll()
{
    return std::vector<Editor>();
}

bool EditorRepository::Exists(uint64_t id)
{
    return false;
}
