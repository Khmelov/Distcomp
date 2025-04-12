#pragma once

#include "Utils.hpp"

#include <string>
#include <fmt/core.h>

struct Tag {
    static std::string name() {
        return "tags";
    }
    static std::string table_name() {
        return "tbl_tag";
    }

    Tag() = default;
    explicit Tag(const json& data)
        : id(data.value("id", 0))
        , name_(data["name"])
    {}
    explicit Tag(const pqxx::row& row)
        : id(row["id"].as<uint64_t>())
        , name_(row["name"].as<std::string>())
    {}

    json to_json() const {
        return {
            {"id", id},
            {"name", name_}
        };
    }

    std::string get_insert_query() const {
        return fmt::format(
            "INSERT INTO {} (name) "
            "VALUES ('{}');",
            table_name(), name_
        );
    }

    std::string get_update_query() const {
        return fmt::format(
            "UPDATE {} SET name = '{}' "
            "WHERE id = {};",
            table_name(), name_, id
        );
    }

    static std::string get_create_table_query() {
        return fmt::format(
            "CREATE TABLE IF NOT EXISTS {} ("
            "id BIGINT PRIMARY KEY DEFAULT 0, "
            "name VARCHAR(32) NOT NULL CHECK (LENGTH(name) >= 2) "
            ");",
            table_name()
        );
    }

    uint64_t id{};
    std::string name_{};
};
