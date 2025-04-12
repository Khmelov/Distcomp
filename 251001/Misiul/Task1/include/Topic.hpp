#pragma once

#include <nlohmann/json.hpp>

#include <string>

#include "Utils.hpp"

using json = nlohmann::json;

struct Topic {
    static std::string name() {
        return "topics";
    }

    Topic() = default;
    Topic(const json& data)
        : id(data["id"])
        , title(data["title"])
        , content(data["content"])
        , creatorId(data["creatorId"])
    {
        std::tm now = {};
        std::time_t t = std::time(nullptr);
        gmtime_r(&t, &now);
        
        created = data.contains("created") 
            ? iso8601_to_time(data["created"]) 
            : now;
        modified = data.contains("modified") 
            ? iso8601_to_time(data["modified"]) 
            : now;
    }

    json to_json() const {
        return {
            {"id", id},
            {"title", title},
            {"content", content},
            {"creatorId", creatorId},
            {"created", time_to_iso8601(created)},
            {"modified", time_to_iso8601(modified)}
        };
    }

    uint64_t id{};
    std::string title{};
    std::string content{};
    uint64_t creatorId{};
    std::tm created{};
    std::tm modified{};
};
