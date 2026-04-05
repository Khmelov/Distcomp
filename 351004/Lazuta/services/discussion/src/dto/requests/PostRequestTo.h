#pragma once

#include <string>
#include <vector>
#include <optional>
#include <jsoncpp/json/json.h>
#include <stdexcept>
#include <exceptions/ValidationException.h>

<<<<<<< HEAD:351004/Lazuta/services/discussion/src/dto/requests/PostRequestTo.h
namespace discussion::dto
{

class PostRequestTo
=======
namespace myapp::dto
{

class IssueRequestTo 
>>>>>>> f26c601fbbe43710c18d4d0b9d78ec1d65a1357c:351004/Lazuta/src/dto/requests/IssueRequestTo.h
{
public:
    std::optional<unsigned long> id;
    unsigned long issueId = 0;
    std::string content;
    std::vector<std::string> labels;

    void validate() const 
    {
        if (issueId == 0) {
            //throw ValidationException("Editor ID is required");
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

    static PostRequestTo fromJson(const Json::Value& json) 
    {
<<<<<<< HEAD:351004/Lazuta/services/discussion/src/dto/requests/PostRequestTo.h
        PostRequestTo dto;
        if (json.isMember("id")) dto.id = json["id"].asUInt64();
        if (json.isMember("issueId")) dto.issueId = json["issueId"].asUInt64();
        if (json.isMember("content")) dto.content = json["content"].asString();
        if (json.isMember("created")) dto.created = json["created"].asString();
        if (json.isMember("modified")) dto.modified = json["modified"].asString();
=======
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
        
>>>>>>> f26c601fbbe43710c18d4d0b9d78ec1d65a1357c:351004/Lazuta/src/dto/requests/IssueRequestTo.h
        dto.validate();
        return dto;
    }
};

};