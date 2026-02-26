#include "PostRepository.h"

uint64_t PostRepository::Create(const Post& entity)
{
    return 0;
}

std::optional<Post> PostRepository::GetByID(uint64_t id)
{
    return std::optional<Post>();
}

bool PostRepository::Update(uint64_t id, const Post& entity)
{
    return false;
}

bool PostRepository::Delete(uint64_t id)
{
    return false;
}

std::vector<Post> PostRepository::ReadAll()
{
    return std::vector<Post>();
}

bool PostRepository::Exists(uint64_t id)
{
    return false;
}
