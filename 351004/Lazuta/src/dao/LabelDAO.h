#pragma once

#include "DAO.h"
#include <entities/Label.h>

class LabelDAO : public DAO<Label> {
public:
    virtual ~LabelDAO() = default;

    virtual std::optional<Label> FindByName(const std::string& name) = 0;
    virtual std::vector<Label> FindByIssueId(uint64_t issueId) = 0;
    virtual bool ExistsByName(const std::string& name) = 0;
};