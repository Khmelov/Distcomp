#pragma once

#include "Entity.h"
#include <string>

class Post : public Entity
{
private:
    unsigned long m_editorId;
    std::string m_title;
    std::string m_content;
    std::string m_created;
    std::string m_modified;

public:
    Post();
    Post(unsigned long editorId, const std::string& title, 
         const std::string& content, const std::string& created, 
         const std::string& modified);
    
    ~Post();
    
    unsigned long getEditorId() const;
    std::string getTitle() const;
    std::string getContent() const;
    std::string getCreated() const;
    std::string getModified() const;
    
    void setEditorId(unsigned long editorId);
    void setTitle(const std::string& title);
    void setContent(const std::string& content);
    void setCreated(const std::string& created);
    void setModified(const std::string& modified);
};