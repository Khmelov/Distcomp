#pragma once

#include <nlohmann/json.hpp>
#include <pqxx/pqxx>

#include <concepts>

using json = nlohmann::json;

template<typename T>
concept Entity = requires(T t) {
    { t.to_json() } -> std::same_as<json>;
    std::constructible_from<T, const json &>;
    std::constructible_from<T, const pqxx::row &>;
    { t.id } -> std::convertible_to<uint64_t>;
    { T::name() } -> std::convertible_to<std::string>;
    { T::table_name() } -> std::convertible_to<std::string>;
    { t.get_insert_query() } -> std::convertible_to<std::string>;
    { t.get_update_query() } -> std::convertible_to<std::string>;
    { T::get_create_table_query() } -> std::convertible_to<std::string>;
};

std::string time_to_iso8601(const std::tm& tm);
std::tm iso8601_to_time(const std::string& iso8601);
