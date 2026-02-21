#include "Issue.h"

Issue::Issue() : Entity()
{
    m_editorId = 0;
    m_title = "";
    m_content = "";
    m_created = "";
    m_modified = "";
}

Issue::Issue(unsigned long editorId, const std::string& title, 
             const std::string& content, const std::string& created, 
             const std::string& modified)
    : Entity()
{
    m_editorId = editorId;
    m_title = title;
    m_content = content;
    m_created = created;
    m_modified = modified;
}

Issue::~Issue()
{
}

unsigned long Issue::getEditorId() const
{
    return m_editorId;
}

std::string Issue::getTitle() const
{
    return m_title;
}

std::string Issue::getContent() const
{
    return m_content;
}

std::string Issue::getCreated() const
{
    return m_created;
}

std::string Issue::getModified() const
{
    return m_modified;
}

void Issue::setEditorId(unsigned long editorId)
{
    m_editorId = editorId;
}

void Issue::setTitle(const std::string& title)
{
    m_title = title;
}

void Issue::setContent(const std::string& content)
{
    m_content = content;
}

void Issue::setCreated(const std::string& created)
{
    m_created = created;
}

void Issue::setModified(const std::string& modified)
{
    m_modified = modified;
}