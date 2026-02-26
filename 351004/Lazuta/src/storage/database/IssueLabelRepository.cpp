#include "IssueLabelRepository.h"

uint64_t IssuelabelRepository::Create(const IssueLabel& entity)
{
    return 0;
}

std::optional<IssueLabel> IssuelabelRepository::GetByID(uint64_t id)
{
    return std::optional<IssueLabel>();
}

bool IssuelabelRepository::Update(uint64_t id, const IssueLabel& entity)
{
    return false;
}

bool IssuelabelRepository::Delete(uint64_t id)
{
    return false;
}

std::vector<IssueLabel> IssuelabelRepository::ReadAll()
{
    return std::vector<IssueLabel>();
}

bool IssuelabelRepository::Exists(uint64_t id)
{
    return false;
}
