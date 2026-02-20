#pragma once

#include <string>
#include <optional>
#include <jsoncpp/json/json.h>
#include <stdexcept>
#include <exceptions/ValidationException.h>

class IssueRequestTo 
{
public:
    std::optional<unsigned long> id;
    unsigned long editorId = 0;
    std::string title;
    std::string content;
    std::string created;
    std::string modified;

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
    }

    static IssueRequestTo fromJson(const Json::Value& json) 
    {
        IssueRequestTo dto;
        if (json.isMember("id")) dto.id = json["id"].asUInt64();
        if (json.isMember("editorId")) dto.editorId = json["editorId"].asUInt64();
        if (json.isMember("title")) dto.title = json["title"].asString();
        if (json.isMember("content")) dto.content = json["content"].asString();
        if (json.isMember("created")) dto.created = json["created"].asString();
        if (json.isMember("modified")) dto.modified = json["modified"].asString();
        dto.validate();
        return dto;
    }
};