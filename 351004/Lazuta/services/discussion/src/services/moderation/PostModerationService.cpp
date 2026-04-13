#include "PostModerationService.h"
#include <algorithm>
#include <iostream>
#include <cctype>

namespace discussion
{

PostModerationService::PostModerationService()
{
    InitDefaultStopWords();
}

void PostModerationService::InitDefaultStopWords()
{
    m_stopWords = 
    {
        "spam",
        "badword",
        "advertisement",
        "viagra",
        "casino",
        "lottery",
        "crypto",
        "bitcoin",
        "xxx",
        "porn",
        "drugs",
        "gambling", 
        "touhou"
    };
}

PostState PostModerationService::Moderate(const std::string& content)
{
    std::string lowerContent = content;
    std::transform(lowerContent.begin(), lowerContent.end(), lowerContent.begin(), ::tolower);
    
    for (const auto& word : m_stopWords)
    {
        if (lowerContent.find(word) != std::string::npos)
        {
            std::cout << "[MODERATION] Content rejected, found stop word: " << word << std::endl;
            return PostState::DECLINE;
        }
    }
    
    std::cout << "[MODERATION] Content approved" << std::endl;
    return PostState::APPROVE;
}

void PostModerationService::AddStopWord(const std::string& word)
{
    m_stopWords.push_back(word);
}

void PostModerationService::RemoveStopWord(const std::string& word)
{
    auto it = std::find(m_stopWords.begin(), m_stopWords.end(), word);
    if (it != m_stopWords.end())
    {
        m_stopWords.erase(it);
    }
}

}