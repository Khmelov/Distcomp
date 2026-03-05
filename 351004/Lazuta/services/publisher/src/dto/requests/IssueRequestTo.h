#pragma once

#include <string>
#include <vector>
#include <optional>
#include <jsoncpp/json/json.h>
#include <stdexcept>
#include <exceptions/ValidationException.h>

namespace myapp::dto
{

class IssueRequestTo 
{
public:
    std::optional<unsigned long> id;
    unsigned long editorId = 0;
    std::string title;
    std::string content;
    std::vector<std::string> labels;

    void validate() const 
    {
        if (editorId == 0) {
            //throw ValidationException("Editor ID is required");
        }
        if (title.length() < 2 || title.length() > 64) {
            throw ValidationException("Title must be between 2 and 64 characters");
        }
        if (content.length() < 4 || content.length() > 2048) {
            throw ValidationException("Content must be between 4 and 2048 characters");
        }
        
        for (const auto& label : labels) 
        {
            if (label.empty()) 
            {
                throw ValidationException("Label cannot be empty");
            }
        }
    }

    static IssueRequestTo fromJson(const Json::Value& json) 
    {
        IssueRequestTo dto;
        
        if (json.isMember("id") && json["id"].isUInt64()) 
            dto.id = json["id"].asUInt64();
        
        if (json.isMember("editorId") && json["editorId"].isUInt64()) 
            dto.editorId = json["editorId"].asUInt64();
        
        if (json.isMember("title") && json["title"].isString()) 
            dto.title = json["title"].asString();
        
        if (json.isMember("content") && json["content"].isString()) 
            dto.content = json["content"].asString();
        
        if (json.isMember("labels") && json["labels"].isArray())
         {
            const Json::Value& labelsArray = json["labels"];
            dto.labels.reserve(labelsArray.size());
            
            for (const auto& label : labelsArray) 
            {
                if (label.isString()) 
                {
                    dto.labels.push_back(label.asString());
                }
            }
        }
        
        dto.validate();
        return dto;
    }
};

};