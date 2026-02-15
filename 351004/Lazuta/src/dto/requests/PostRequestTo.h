#pragma once

#include <string>
#include <optional>
#include <jsoncpp/json/json.h>

class PostRequestTo 
{
public:
    std::optional<unsigned long> id;
    unsigned long editorId;
    std::string title;
    std::string content;
    std::string created;
    std::string modified;

    static PostRequestTo fromJson(const Json::Value& json) 
    {
        PostRequestTo dto;
        if (json.isMember("id")) dto.id = json["id"].asUInt64();
        if (json.isMember("editorid")) dto.editorId = json["editorid"].asUInt64();
        if (json.isMember("title")) dto.title = json["title"].asString();
        if (json.isMember("content")) dto.content = json["content"].asString();
        if (json.isMember("created")) dto.created = json["created"].asString();
        if (json.isMember("modified")) dto.modified = json["modified"].asString();
        return dto;
    }
};