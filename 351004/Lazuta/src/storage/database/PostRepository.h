#pragma once

#include "IDatabaseRepository.h"
#include <entities/Post.h>

class PostRepository : public IDatabaseRepository<Post>
{
    uint64_t Create(const Post& entity) override;
    std::optional<Post> GetByID(uint64_t id) override;
    bool Update(uint64_t id, const Post& entity) override;
    bool Delete(uint64_t id) override;
    std::vector<Post> ReadAll() override;
    bool Exists(uint64_t id) override;
};