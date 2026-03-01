#pragma once

#include <string>
#include <jsoncpp/json/json.h>

class EditorResponseTo 
{
public:
    unsigned long id;
    std::string login;
    std::string firstName;
    std::string lastName;

    Json::Value toJson() const 
    {
        Json::Value json;
        json["id"] = id;
        json["login"] = login;
        json["firstname"] = firstName;
        json["lastname"] = lastName;
        return json;
    }
};