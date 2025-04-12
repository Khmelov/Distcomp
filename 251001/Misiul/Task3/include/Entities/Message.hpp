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
        , country(data.value("country", "belarus"))
        , content(data["content"])
    {}
    explicit Message(const CassRow* row) {
        const CassValue* id_value = cass_row_get_column_by_name(row, "id");
        const CassValue* topic_id_value = cass_row_get_column_by_name(row, "topic_id");
        const CassValue* content_value = cass_row_get_column_by_name(row, "content");
        const CassValue* country_value = cass_row_get_column_by_name(row, "country");

        cass_value_get_int64(id_value, &id);
        cass_value_get_int64(topic_id_value, &topicId);

        const char* country_str;
        size_t country_length;
        if (cass_value_get_string(country_value, &country_str, &country_length) == CASS_OK) {
            country.assign(country_str, country_length);
        }

        const char* content_str;
        size_t content_length;
        if (cass_value_get_string(content_value, &content_str, &content_length) == CASS_OK) {
            content.assign(content_str, content_length);
        }    
    }

    json to_json() const {
        return {
            {"id", id},
            {"topicId", topicId},
            {"country", country},
            {"content", content}
        };
    }

    std::string get_insert_query() const {
        return fmt::format(
            "INSERT INTO {} (id, topic_id, country, content) "
            "VALUES ({}, {}, '{}', '{}');",
            table_name(), id, topicId, country, content
        );
    }

    std::string get_update_query() const {
        return fmt::format(
            "UPDATE {} SET content = '{}' "
            "WHERE country = '{}' AND id = {};",
            table_name(), content, country, id
        );
    }

    static std::string get_create_table_query() {
        return fmt::format(
            "CREATE TABLE IF NOT EXISTS {} ("
            "id BIGINT, "
            "topic_id BIGINT, "
            "country TEXT, "
            "content TEXT, "
            "PRIMARY KEY (country, id)"
            ") WITH CLUSTERING ORDER BY (id ASC);",
            table_name()
        );
    }

    int64_t id{};
    int64_t topicId{};
    std::string country;
    std::string content;
};
