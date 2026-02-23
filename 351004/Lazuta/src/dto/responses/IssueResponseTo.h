#pragma once

#include <string>
#include <jsoncpp/json/json.h>

class IssueResponseTo 
{
public:
    unsigned long id;
    unsigned long editorId;
    std::string title;
    std::string content;
    std::string created;
    std::string modified;

    Json::Value toJson() const 
    {
        Json::Value json;
        json["id"] = id;
        json["editorId"] = editorId;
        json["title"] = title;
        json["content"] = content;
        json["created"] = created;
        json["modified"] = modified;
        return json;
    }
};