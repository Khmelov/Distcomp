#pragma once

#include <string>
#include <optional>
#include <jsoncpp/json/json.h>
#include <stdexcept>
#include <exceptions/ValidationException.h>

class EditorRequestTo 
{
public:
    std::optional<unsigned long> id;
    std::string login;
    std::string password;
    std::string firstName;
    std::string lastName;

    void validate() const 
    {
        if (login.length() < 2 || login.length() > 64) {
            throw ValidationException("Login must be between 2 and 64 characters");
        }
        if (password.length() < 8 || password.length() > 128) {
            throw ValidationException("Password must be between 8 and 128 characters");
        }
        if (firstName.length() < 2 || firstName.length() > 64) {
            throw ValidationException("First name must be between 2 and 64 characters");
        }
        if (lastName.length() < 2 || lastName.length() > 64) {
            throw ValidationException("Last name must be between 2 and 64 characters");
        }
    }

    static EditorRequestTo fromJson(const Json::Value& json) 
    {
        EditorRequestTo dto;
        if (json.isMember("id")) dto.id = json["id"].asUInt64();
        if (json.isMember("login")) dto.login = json["login"].asString();
        if (json.isMember("password")) dto.password = json["password"].asString();
        if (json.isMember("firstname")) dto.firstName = json["firstname"].asString();
        if (json.isMember("lastname")) dto.lastName = json["lastname"].asString();
        dto.validate();
        return dto;
    }
};