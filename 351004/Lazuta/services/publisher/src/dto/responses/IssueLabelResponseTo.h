#pragma once

#include <string>
#include <jsoncpp/json/json.h>

namespace publisher::dto
{

class IssueLabelResponseTo 
{
public:
    unsigned long id;
    unsigned long issueId;
    unsigned long labelId;

    Json::Value toJson() const 
    {
        Json::Value json;
        json["id"] = id;
        json["issueId"] = issueId;
        json["labelId"] = labelId;
        return json;
    }
};

};