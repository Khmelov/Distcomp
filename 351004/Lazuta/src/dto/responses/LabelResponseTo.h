#pragma once

#include <string>
#include <jsoncpp/json/json.h>

class LabelResponseTo 
{
public:
    unsigned long id;
    std::string name;

    Json::Value toJson() const 
    {
        Json::Value json;
        json["id"] = id;
        json["name"] = name;
        return json;
    }
};