#pragma once

#include "Utils.hpp"

#include <string>
#include <fmt/core.h>

struct Topic {
    static std::string name() {
        return "topics";
    }
    static std::string table_name() {
        return "tbl_topic";
    }

    Topic() = default;
    explicit Topic(const json& data)
        : id(data.value("id", 0))
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
    explicit Topic(const pqxx::row& row)
        : id(row["id"].as<uint64_t>())
        , title(row["title"].as<std::string>())
        , content(row["content"].as<std::string>())
        , creatorId(row["creator_id"].as<uint64_t>())
        , created(iso8601_to_time(row["created"].as<std::string>()))
        , modified(iso8601_to_time(row["modified"].as<std::string>()))
    {
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

    std::string get_insert_query() const {
        return fmt::format(
            "INSERT INTO {} (title, content, creator_id, created, modified) "
            "VALUES ('{}', '{}', {}, '{}', '{}') RETURNING id",
            table_name(), title, content, creatorId, time_to_iso8601(created), time_to_iso8601(modified)
        );
    }

    std::string get_update_query() const {
        return fmt::format(
            "UPDATE {} SET "
            "title = '{}', content = '{}', creator_id = {}, created = '{}', modified = '{}' "
            "WHERE id = {}",
            table_name(), title, content, creatorId, time_to_iso8601(created), time_to_iso8601(modified), id
        );
    }

    static std::string get_create_table_query() {
        return fmt::format(
            "CREATE TABLE IF NOT EXISTS {} ("
            "id BIGINT PRIMARY KEY DEFAULT 0, "
            "title VARCHAR(64) NOT NULL CHECK (LENGTH(title) >= 2), "
            "content TEXT NOT NULL CHECK (LENGTH(content) >= 2), "
            "creator_id BIGINT NOT NULL REFERENCES tbl_creator(id), "
            "created TIMESTAMP NOT NULL, "
            "modified TIMESTAMP NOT NULL"
            ")",
            table_name()
        );
    }

    uint64_t id{};
    std::string title{};
    std::string content{};
    uint64_t creatorId{};
    std::tm created{};
    std::tm modified{};
};
