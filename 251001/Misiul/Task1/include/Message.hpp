#pragma once

#include <nlohmann/json.hpp>

#include <string>

using json = nlohmann::json;

struct Message {
    static std::string name() {
        return "messages";
    }

    Message() = default;
    Message(const json& data)
        : id(data["id"])
        , topicId(data["topicId"])
        , content(data["content"])
    {}

    json to_json() const {
        return {
            {"id", id},
            {"topicId", topicId},
            {"content", content}
        };
    }

    uint64_t id{};
    uint64_t topicId{};
    std::string content{};
};
