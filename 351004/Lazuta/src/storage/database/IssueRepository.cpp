#include "IssueRepository.h"

uint64_t IssueRepository::Create(const Issue& entity)
{
    return 0;
}

std::optional<Issue> IssueRepository::GetByID(uint64_t id)
{
    return std::optional<Issue>();
}

bool IssueRepository::Update(uint64_t id, const Issue& entity)
{
    return false;
}

bool IssueRepository::Delete(uint64_t id)
{
    return false;
}

std::vector<Issue> IssueRepository::ReadAll()
{
    return std::vector<Issue>();
}

bool IssueRepository::Exists(uint64_t id)
{
    return false;
}
