#pragma once
#include <stdexcept>
#include <string>

class InvalidOperationException : public std::runtime_error 
{
public:
    explicit InvalidOperationException(const std::string& message) 
        : std::runtime_error(message) 
    {
    }
};