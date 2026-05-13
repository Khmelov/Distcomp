#ifndef AUTHEXCEPTION_H
#define AUTHEXCEPTION_H

#include <string>
#include <exception>

class AuthException : public std::exception {
public:
    AuthException(int errorCode, const std::string& errorMessage)
        : errorCode_(errorCode), errorMessage_(errorMessage) {}

    int getErrorCode() const { return errorCode_; }
    const char* what() const noexcept override { return errorMessage_.c_str(); }

private:
    int errorCode_;
    std::string errorMessage_;
};

class UnauthorizedException : public AuthException {
public:
    UnauthorizedException(const std::string& msg = "Invalid credentials")
        : AuthException(40100, msg) {}
};

class ForbiddenException : public AuthException {
public:
    ForbiddenException(const std::string& msg = "Access denied")
        : AuthException(40300, msg) {}
};

class ValidationException : public AuthException {
public:
    ValidationException(const std::string& msg = "Validation error")
        : AuthException(40000, msg) {}
};

class NotFoundException : public AuthException {
public:
    NotFoundException(const std::string& msg = "Resource not found")
        : AuthException(40400, msg) {}
};

#endif
