#pragma once

#include <string>
#include <optional>
#include <jsoncpp/json/json.h>

class LabelRequestTo 
{
public:
    std::optional<unsigned long> id;
    std::string name;

    static LabelRequestTo fromJson(const Json::Value& json) 
    {
        LabelRequestTo dto;
        if (json.isMember("id")) dto.id = json["id"].asUInt64();
        if (json.isMember("name")) dto.name = json["name"].asString();
        return dto;
    }
};