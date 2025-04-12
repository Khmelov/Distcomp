#pragma once

#include <nlohmann/json.hpp>

#include <concepts>

using json = nlohmann::json;

template<typename T>
concept Entity = requires(T t) {
    { t.to_json() } -> std::same_as<json>;
    std::constructible_from<T, const json &>;
    { t.id } -> std::convertible_to<uint64_t>;
    { T::name() } -> std::convertible_to<std::string>;
};

std::string time_to_iso8601(const std::tm& tm);
std::tm iso8601_to_time(const std::string& iso8601);
