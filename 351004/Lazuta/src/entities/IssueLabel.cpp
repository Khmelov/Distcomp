#include "IssueLabel.h"

namespace myapp::entities
{

IssueLabel::IssueLabel()
{
}

IssueLabel::IssueLabel(unsigned long issueId, unsigned long labelId)
{
    m_issueId = issueId;
    m_labelId = labelId;
}

IssueLabel::~IssueLabel()
{
}

unsigned long IssueLabel::getIssueId() const
{
    return m_issueId;
}

unsigned long IssueLabel::getLabelId() const
{
    return m_labelId;
}

void IssueLabel::setIssueId(unsigned long issueId)
{
    m_issueId = issueId;
}

void IssueLabel::setLabelId(unsigned long labelId)
{
    m_labelId = labelId;
}

};