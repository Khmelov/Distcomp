#ifndef JWTUTILS_H
#define JWTUTILS_H

#include <string>
#include <drogon/HttpRequest.h>
#include <json/json.h>

class JwtUtils {
public:
    static bool validateToken(const std::string& token, std::string& login, std::string& role);
    static std::string extractTokenFromHeader(const drogon::HttpRequestPtr& req);
    static bool isAdmin(const std::string& role) { return role == "ADMIN"; }
    static bool isCustomer(const std::string& role) { return role == "CUSTOMER"; }
};

#endif
