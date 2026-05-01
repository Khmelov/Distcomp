#pragma once

#include <string>
#include <json/json.h>

namespace auth::dto
{

class LoginRequestTo {
public:
    std::string login;
    std::string password;

    static LoginRequestTo fromJson(const Json::Value& json) {
        LoginRequestTo req;
        req.login = json["login"].asString();
        req.password = json["password"].asString();
        return req;
    }

    Json::Value toJson() const {
        Json::Value json;
        json["login"] = login;
        json["password"] = password;
        return json;
    }
};

}
