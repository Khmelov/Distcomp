#include "Utils.hpp"

#include <sstream>

std::string time_to_iso8601(const std::tm& tm) {
    char buffer[30];
    std::strftime(buffer, sizeof(buffer), "%Y-%m-%dT%H:%M:%SZ", &tm);
    return std::string(buffer);
}

std::tm iso8601_to_time(const std::string& iso8601) {
    std::tm tm = {};
    std::istringstream ss(iso8601);
    ss >> std::get_time(&tm, "%Y-%m-%dT%H:%M:%SZ");
    return tm;
}