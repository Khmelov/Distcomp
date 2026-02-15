#pragma once

#include <string>
#include <optional>
#include <jsoncpp/json/json.h>

class EditorRequestTo 
{
public:
    std::optional<unsigned long> id;
    std::string login;
    std::string password;
    std::string firstName;
    std::string lastName;

    static EditorRequestTo fromJson(const Json::Value& json) 
    {
        EditorRequestTo dto;
        if (json.isMember("id")) dto.id = json["id"].asUInt64();
        if (json.isMember("login")) dto.login = json["login"].asString();
        if (json.isMember("password")) dto.password = json["password"].asString();
        if (json.isMember("firstName")) dto.firstName = json["firstName"].asString();
        if (json.isMember("lastName")) dto.lastName = json["lastName"].asString();
        return dto;
    }
};