#pragma once

#include "DAO.h"
#include <entities/Post.h>

class PostDAO : public DAO<Post> 
{
public:
    virtual ~PostDAO() = default;

    virtual std::vector<Post> FindByIssueId(uint64_t issueId) = 0;
    virtual bool DeleteByIssueId(uint64_t issueId) = 0;
};