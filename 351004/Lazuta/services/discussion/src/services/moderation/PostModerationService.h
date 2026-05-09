#pragma once

#include <string>
#include <vector>
#include <models/TblPost.h>

namespace discussion
{

class PostModerationService
{
public:
    PostModerationService();
    ~PostModerationService() = default;
    
    PostState Moderate(const std::string& content);
    void AddStopWord(const std::string& word);
    void RemoveStopWord(const std::string& word);
    
private:
    std::vector<std::string> m_stopWords;
    
    void InitDefaultStopWords();
};

}