#pragma once

#include <string>
#include <optional>
#include <jsoncpp/json/json.h>
#include <stdexcept>
#include <exceptions/ValidationException.h>

class PostRequestTo
{
public:
    std::optional<unsigned long> id;
    unsigned long issueId = 0;
    std::string content;
    std::string created;
    std::string modified;

    void validate() const 
    {
        if (issueId == 0) {
            //throw ValidationException("Editor ID is required");
        }
        if (content.length() < 4 || content.length() > 2048) {
            throw ValidationException("Content must be between 4 and 2048 characters");
        }
    }

    static PostRequestTo fromJson(const Json::Value& json) 
    {
        PostRequestTo dto;
        if (json.isMember("id")) dto.id = json["id"].asUInt64();
        if (json.isMember("issueId")) dto.issueId = json["issueId"].asUInt64();
        if (json.isMember("content")) dto.content = json["content"].asString();
        if (json.isMember("created")) dto.created = json["created"].asString();
        if (json.isMember("modified")) dto.modified = json["modified"].asString();
        dto.validate();
        return dto;
    }
};