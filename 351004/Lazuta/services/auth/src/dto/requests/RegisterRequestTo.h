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
        req.firstName = json["firstName"].asString();
        req.lastName = json["lastName"].asString();
        req.role = json["role"].asString();
        return req;
    }

    Json::Value toJson() const {
        Json::Value json;
        json["login"] = login;
        json["password"] = password;
        json["firstName"] = firstName;
        json["lastName"] = lastName;
        json["role"] = role;
        return json;
    }
};

}
