#pragma once

#include <string>
#include <optional>
#include <jsoncpp/json/json.h>
#include <stdexcept>
#include <exceptions/ValidationException.h>

class LabelRequestTo 
{
public:
    std::optional<unsigned long> id;
    std::string name;

    void validate() const 
    {
        if (name.length() < 2 || name.length() > 32) {
            throw ValidationException("Name must be between 2 and 32 characters");
        }
    }

    static LabelRequestTo fromJson(const Json::Value& json) 
    {
        LabelRequestTo dto;
        if (json.isMember("id")) dto.id = json["id"].asUInt64();
        if (json.isMember("name")) dto.name = json["name"].asString();
        dto.validate();
        return dto;
    }
};