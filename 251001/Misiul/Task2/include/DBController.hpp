#pragma once

#include "Utils.hpp"

#include <pqxx/pqxx>
#include <fmt/core.h>

#include <iostream>
#include <cstdint>
#include <memory>
#include <optional>

using namespace std::string_literals;

class PostgresController final {
public:
    PostgresController() noexcept = default;
    ~PostgresController();

    PostgresController(const PostgresController&) = delete;
    PostgresController(PostgresController&&) = delete;
    PostgresController& operator=(const PostgresController&) = delete;
    PostgresController& operator=(PostgresController&&) = delete;

    bool initialize();

    template<Entity T>
    [[nodiscard]] bool create_table();
                    
    template<Entity T>
    [[nodiscard]] bool insert(const T& entity);

    template<Entity T>
    std::vector<T> get_all();

    template<Entity T>
    std::optional<T> get_by_id(uint64_t id);

    template<Entity T>
    void update_by_id(const T& entity);

    template<Entity T>
    [[nodiscard]] bool delete_by_id(uint64_t id);
    
private:
    [[nodiscard]] pqxx::result execute(const std::string& query) noexcept;

private:
    constexpr static auto DRIVER_PREFIX = "jdbc:postgresql:";
    constexpr static auto HOST = "localhost";
    constexpr static uint16_t PORT = 5432;
    constexpr static auto USER = "postgres";
    constexpr static auto PASSWORD = "postgres";
    constexpr static auto SCEME = "distcomp";
    
    std::unique_ptr<pqxx::connection> m_connection;
};

template<Entity T>
std::vector<T> PostgresController::get_all() {
    std::string query = fmt::format(
        "SELECT * FROM {};", 
        T::table_name()
    );
    auto rows = execute(query);

    std::vector<T> entities;
    std::ranges::transform(rows, std::back_inserter(entities), [](const auto& row){
        return T{row};
    });
    return entities;
}

template<Entity T>
std::optional<T> PostgresController::get_by_id(uint64_t id) {
    std::string query = fmt::format(
        "SELECT * FROM {} WHERE id = {};", 
        T::table_name(),
        id
    );
    auto result = execute(query);
    if (result.empty()) {
        return {};
    }
    return T{result[0]};
}

template<Entity T>
bool PostgresController::create_table() 
{
    std::string drop_query = fmt::format(
        "DROP TABLE IF EXISTS {} CASCADE;", 
        T::table_name()
    );
    auto drop_result = execute(drop_query);

    std::string query = T::get_create_table_query();
    auto result = execute(query);
    return true;
}

template<Entity T>
bool PostgresController::insert(const T& entity) 
{
    std::string insert_query = entity.get_insert_query();
    auto result = execute(insert_query);
    return result.affected_rows() > 0;
}

template<Entity T>
void PostgresController::update_by_id(const T& entity) 
{
    std::string update_query = entity.get_update_query();
    auto result = execute(update_query);
}

template<Entity T>
bool PostgresController::delete_by_id(uint64_t id) 
{
    std::string query = fmt::format(
        "DELETE FROM {} WHERE id = {};", 
        T::table_name(),
        id
    );
    auto result = execute(query);
    return result.affected_rows() > 0;
}
