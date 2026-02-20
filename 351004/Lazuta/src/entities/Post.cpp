#include "Post.h"

Post::Post() : Entity()
{
    m_editorId = 0;
    m_title = "";
    m_content = "";
    m_created = "";
    m_modified = "";
}

Post::Post(unsigned long editorId, const std::string& title, 
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

Post::~Post()
{
}

unsigned long Post::getEditorId() const
{
    return m_editorId;
}

std::string Post::getTitle() const
{
    return m_title;
}

std::string Post::getContent() const
{
    return m_content;
}

std::string Post::getCreated() const
{
    return m_created;
}

std::string Post::getModified() const
{
    return m_modified;
}

void Post::setEditorId(unsigned long editorId)
{
    m_editorId = editorId;
}

void Post::setTitle(const std::string& title)
{
    m_title = title;
}

void Post::setContent(const std::string& content)
{
    m_content = content;
}

void Post::setCreated(const std::string& created)
{
    m_created = created;
}

void Post::setModified(const std::string& modified)
{
    m_modified = modified;
}