#pragma once

#include "Entity.h"
#include <string>

class IssueLabel : public Entity
{
private:
    unsigned long m_issueId;
    unsigned long m_labelId;
public:
    IssueLabel();
    IssueLabel(unsigned long issueId, unsigned long labelId);
    ~IssueLabel();

    unsigned long getIssueId() const;
    unsigned long getLabelId() const;
    void setIssueId(unsigned long issueId);
    void setLabelId(unsigned long labelId);
};