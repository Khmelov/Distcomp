#pragma once

#include "DAO.h"
#include <entities/Issue.h>
#include <vector>
#include <optional>

class IssueDAO : public DAO<Issue> 
{
public:
    virtual ~IssueDAO() = default;

    virtual std::vector<Issue> FindByEditorId(uint64_t editorId) = 0;

    virtual std::vector<Issue> FindByLabelId(uint64_t labelId) = 0;
    virtual void AddLabelToIssue(uint64_t issueId, uint64_t labelId) = 0;
    virtual void RemoveLabelFromIssue(uint64_t issueId, uint64_t labelId) = 0;
    virtual std::vector<uint64_t> GetLabelIdsForIssue(uint64_t issueId) = 0;
    
    virtual std::vector<Issue> FindByPostId(uint64_t postId) = 0;

    virtual std::vector<Issue> FindByCriteria
    (
        std::optional<std::string> title,
        std::optional<std::string> content,
        std::optional<std::string> editorLogin,
        std::optional<std::vector<uint64_t>> labelIds,
        std::optional<std::vector<std::string>> labelNames
    ) = 0;
};