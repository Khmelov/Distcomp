#pragma once

#include <nlohmann/json.hpp>

#include <string>

using json = nlohmann::json;

struct Tag {
    static std::string name() {
        return "tags";
    }

    Tag() = default;
    Tag(const json& data)
        : id(data["id"])
        , name_(data["name"])
    {}

    json to_json() const {
        return {
            {"id", id},
            {"name", name_}
        };
    }

    uint64_t id{};
    std::string name_{};
};
