#pragma once

#include <string>
#include <json/json.h>

namespace auth::dto
{

class EditorResponseTo 
{
public:
    int64_t id;
    std::string login;
    std::string firstName;
    std::string lastName;
    std::string role;

    Json::Value toJson() const 
    {
        Json::Value json;
        json["id"] = static_cast<Json::Int64>(id);
        json["login"] = login;
        json["firstname"] = firstName;
        json["lastname"] = lastName;
        json["role"] = role;
        return json;
    }
};

}
