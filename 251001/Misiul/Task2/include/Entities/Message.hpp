#pragma once

#include "Utils.hpp"

#include <string>
#include <fmt/core.h>

struct Message {
    static std::string name() {
        return "messages";
    }
    static std::string table_name() {
        return "tbl_message";
    }

    Message() = default;
    explicit Message(const json& data)
        : id(data.value("id", 0))
        , topicId(data["topicId"])
        , content(data["content"])
    {}
    explicit Message(const pqxx::row& row)
        : id(row["id"].as<uint64_t>())
        , topicId(row["topic_id"].as<uint64_t>())
        , content(row["content"].as<std::string>())
    {}

    json to_json() const {
        return {
            {"id", id},
            {"topicId", topicId},
            {"content", content}
        };
    }

    std::string get_insert_query() const {
        return fmt::format(
            "INSERT INTO {} (topic_id, content) "
            "VALUES ({}, '{}')",
            table_name(), topicId, content
        );
    }

    std::string get_update_query() const {
        return fmt::format(
            "UPDATE {} SET "
            "topic_id = {}, "
            "content = '{}' "
            "WHERE id = {}",
            table_name(), topicId, content, id
        );
    }

    static std::string get_create_table_query() {
        return fmt::format(
            "CREATE TABLE IF NOT EXISTS {} ("
            "id BIGINT PRIMARY KEY DEFAULT 0, "
            "topic_id BIGINT NOT NULL REFERENCES tbl_topic(id), "
            "content TEXT NOT NULL CHECK (LENGTH(content) >= 2) "
            "); ",
            table_name()
        );
    }

    uint64_t id{};
    uint64_t topicId{};
    std::string content{};
};
