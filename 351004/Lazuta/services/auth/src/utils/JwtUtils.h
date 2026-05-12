#ifndef JWTUTILS_H
#define JWTUTILS_H

#include <string>
#include <drogon/HttpRequest.h>
#include <json/json.h>

class JwtUtils {
public:
    static std::string generateToken(const std::string& login, const std::string& role, int expiryHours = 1);
    static bool validateToken(const std::string& token, std::string& login, std::string& role);
    static std::string extractTokenFromHeader(const drogon::HttpRequestPtr& req);
};

#endif
