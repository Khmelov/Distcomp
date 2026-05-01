#include "JwtUtils.h"
#include <jwt-cpp/jwt.h>

bool JwtUtils::validateToken(const std::string& token, std::string& login, std::string& role) {
    try {
        auto decoded = jwt::decode(token);
        auto verifier = jwt::verify()
            .allow_algorithm(jwt::algorithm::hs256{"distcomp-secret-key-2024"});
        
        verifier.verify(decoded);
        
        login = decoded.get_subject().c_str();
        if (decoded.has_payload_claim("role")) {
            role = decoded.get_payload_claim("role").as_string();
        }
        return true;
    } catch (...) {
        return false;
    }
}

std::string JwtUtils::extractTokenFromHeader(const drogon::HttpRequestPtr& req) {
    auto authHeader = req->getHeader("Authorization");
    if (authHeader.empty()) {
        return "";
    }
    
    std::string prefix = "Bearer ";
    if (authHeader.rfind(prefix, 0) == 0) {
        return authHeader.substr(prefix.length());
    }
    return authHeader;
}
