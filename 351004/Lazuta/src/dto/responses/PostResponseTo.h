#pragma once

#include <string>
#include <jsoncpp/json/json.h>

class PostResponseTo 
{
public:
    unsigned long id;
    unsigned long issueId;
    std::string content;
    std::string created;
    std::string modified;

    Json::Value toJson() const 
    {
        Json::Value json;
        json["id"] = id;
        json["issueId"] = issueId;
        json["content"] = content;
        json["created"] = created;
        json["modified"] = modified;
        return json;
    }
};