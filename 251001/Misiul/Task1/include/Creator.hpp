#pragma once

#include <nlohmann/json.hpp>

#include <string>

using json = nlohmann::json;

struct Creator {
    static std::string name() {
        return "creators";
    }

    Creator() = default;
    Creator(const json& data)
        : id(data["id"])
        , login(data["login"])
        , password(data["password"])
        , firstname(data["firstname"])
        , lastname(data["lastname"])
    {}

    json to_json() const {
        return {
            {"id", id},
            {"login", login},
            {"password", password},
            {"firstname", firstname},
            {"lastname", lastname}
        };
    }

    uint64_t id{};
    std::string login{};
    std::string password{};
    std::string firstname{};
    std::string lastname{};
};
