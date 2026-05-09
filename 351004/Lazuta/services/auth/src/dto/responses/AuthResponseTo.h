#pragma once

#include <string>
#include <json/json.h>

namespace auth::dto
{

class AuthResponseTo {
public:
    std::string accessToken;
    std::string tokenType = "Bearer";

    Json::Value toJson() const {
        Json::Value json;
        json["access_token"] = accessToken;
        json["token_type"] = tokenType;
        return json;
    }
};

}
