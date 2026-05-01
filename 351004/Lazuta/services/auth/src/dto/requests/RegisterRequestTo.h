#pragma once

#include <string>
#include <json/json.h>

namespace auth::dto
{

class RegisterRequestTo {
public:
    std::string login;
    std::string password;
    std::string firstName;
    std::string lastName;
    std::string role = "";

    static RegisterRequestTo fromJson(const Json::Value& json) {
        RegisterRequestTo req;
        req.login = json["login"].asString();
        req.password = json["password"].asString();
        req.firstName = json["firstname"].asString();
        req.lastName = json["lastname"].asString();
        req.role = json["role"].asString();
        return req;
    }

    Json::Value toJson() const {
        Json::Value json;
        json["login"] = login;
        json["password"] = password;
        json["firstname"] = firstName;
        json["lastname"] = lastName;
        json["role"] = role;
        return json;
    }
};

}
