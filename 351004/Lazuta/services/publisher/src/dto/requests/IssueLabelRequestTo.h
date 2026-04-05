#pragma once

#include <string>
#include <optional>
#include <jsoncpp/json/json.h>
#include <stdexcept>
#include <exceptions/ValidationException.h>

<<<<<<< HEAD:351004/Lazuta/services/publisher/src/dto/requests/IssueLabelRequestTo.h
namespace publisher::dto
=======
namespace myapp::dto
>>>>>>> f26c601fbbe43710c18d4d0b9d78ec1d65a1357c:351004/Lazuta/src/dto/requests/IssueLabelRequestTo.h
{

class IssueLabelRequestTo 
{
public:
    std::optional<unsigned long> id;
    unsigned long issueId = 0;
    unsigned long labelId = 0;

    void validate() const 
    {
        if (issueId == 0) 
        {
            throw ValidationException("Issue ID is required");
        }
        if (labelId == 0) 
        {
            throw ValidationException("Label ID is required");
        }
    }

    static IssueLabelRequestTo fromJson(const Json::Value& json) 
    {
        IssueLabelRequestTo dto;
        if (json.isMember("id")) dto.id = json["id"].asUInt64();
        if (json.isMember("issueId")) dto.issueId = json["issueId"].asUInt64();
        if (json.isMember("labelId")) dto.labelId = json["labelId"].asUInt64();
        dto.validate();
        return dto;
    }
};

};