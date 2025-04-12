#pragma once

#include <Utils.hpp>

#include <string>
#include <fmt/core.h>

struct Creator {
    static std::string name() {
        return "creators";
    }
    static std::string table_name() {
        return "tbl_creator";
    }

    Creator() = default;
    explicit Creator(const json& data)
        : id(data.value("id", 0))
        , login(data["login"])
        , password(data["password"])
        , firstname(data["firstname"])
        , lastname(data["lastname"])
    {}
    explicit Creator(const pqxx::row& row)
        : id(row["id"].as<uint64_t>())
        , login(row["login"].as<std::string>())
        , password(row["password"].as<std::string>())
        , firstname(row["firstname"].as<std::string>())
        , lastname(row["lastname"].as<std::string>())
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
    
    std::string get_insert_query() const {
        return fmt::format(
            "INSERT INTO {} (login, password, firstname, lastname) "
            "VALUES ('{}', '{}', '{}', '{}')",
            table_name(), login, password, firstname, lastname
        );
    }

    std::string get_update_query() const {
        return fmt::format(
            "UPDATE {} SET "
            "login = '{}', password = '{}', firstname = '{}', lastname = '{}' "
            "WHERE id = {}",
            table_name(), login, password, firstname, lastname, id
        );
    }

    static std::string get_create_table_query() {
        return fmt::format(
            "CREATE TABLE IF NOT EXISTS {} ("
            "id BIGINT PRIMARY KEY DEFAULT 0, "
            "login VARCHAR(64) NOT NULL UNIQUE CHECK (LENGTH(login) >= 2), "
            "password VARCHAR(128) NOT NULL CHECK (LENGTH(password) >= 8), "
            "firstname VARCHAR(64) NOT NULL CHECK (LENGTH(firstname) >= 2), "
            "lastname VARCHAR(64) NOT NULL CHECK (LENGTH(lastname) >= 2)"
            ")",
            table_name()
        );
    }

    uint64_t id{};
    std::string login{};
    std::string password{};
    std::string firstname{};
    std::string lastname{};
};
