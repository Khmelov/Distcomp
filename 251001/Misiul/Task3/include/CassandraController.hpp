#pragma once

#include "Utils.hpp"

#include <cassandra.h>
#include <fmt/core.h>

#include <cstdint>
#include <memory>
#include <optional>
#include <vector>
#include <iostream>

class CassandraController final {
public:
    CassandraController() noexcept;
    ~CassandraController();

    CassandraController(const CassandraController&) = delete;
    CassandraController(CassandraController&&) = delete;
    CassandraController& operator=(const CassandraController&) = delete;
    CassandraController& operator=(CassandraController&&) = delete;

    bool initialize();

    template<CassandraEntity T>
    [[nodiscard]] bool create_table();
                    
    template<CassandraEntity T>
    [[nodiscard]] bool insert(const T& entity);

    template<CassandraEntity T>
    std::vector<T> get_all();

    template<CassandraEntity T>
    std::optional<T> get_by_id(const std::string& id);

    template<CassandraEntity T>
    bool update_by_id(const T& entity);

    template<CassandraEntity T>
    [[nodiscard]] bool delete_by_id(const std::string& id);
    
private:
    [[nodiscard]] bool execute(const std::string& query) noexcept;
    [[nodiscard]] CassStatement* execute_query(const std::string& query);

private:
    constexpr static auto HOST = "127.0.0.1";
    constexpr static uint16_t PORT = 9042;
    constexpr static auto SCHEMA = "distcomp";
    
    CassCluster* m_cluster = nullptr;
    CassSession* m_session = nullptr;
};


template<CassandraEntity T>
bool CassandraController::create_table() {
    std::string drop_query = fmt::format(
        "DROP TABLE IF EXISTS {};", 
        T::table_name()
    );
    auto drop_result = execute(drop_query);

    std::string query = T::get_create_table_query();
    return execute(query);
}

template<CassandraEntity T>
bool CassandraController::insert(const T& entity) {
    std::string insert_query = entity.get_insert_query();
    return execute(insert_query);
}

template<CassandraEntity T>
std::vector<T> CassandraController::get_all() {
    std::string query = fmt::format("SELECT * FROM {};", T::table_name());
    CassStatement* statement = execute_query(query);
    if (!statement) return {};

    std::vector<T> entities;
    CassFuture* future = cass_session_execute(m_session, statement);
    const CassResult* result = cass_future_get_result(future);
    CassIterator* it = cass_iterator_from_result(result);

    while (cass_iterator_next(it)) {
        const CassRow* row = cass_iterator_get_row(it);
        entities.emplace_back(row);
    }

    cass_result_free(result);
    cass_iterator_free(it);
    cass_statement_free(statement);
    return entities;
}

template<CassandraEntity T>
std::optional<T> CassandraController::get_by_id(const std::string& id) {
    std::string query = fmt::format("SELECT * FROM {} WHERE id = {} ALLOW FILTERING;", 
        T::table_name(), std::stoull(id));

    CassStatement* statement = execute_query(query);
    if (!statement) return {};

    CassFuture* future = cass_session_execute(m_session, statement);
    const CassResult* result = cass_future_get_result(future);
    const CassRow* row = cass_result_first_row(result);

    std::optional<T> entity;
    if (row) {
        entity = T{row};
    }

    cass_result_free(result);
    cass_statement_free(statement);
    return entity;
}

template<CassandraEntity T>
bool CassandraController::update_by_id(const T& entity) {
    std::string update_query = entity.get_update_query();
    return execute(update_query);
}

template<CassandraEntity T>
bool CassandraController::delete_by_id(const std::string& id) {
    std::string query = fmt::format("DELETE FROM {} WHERE country = 'belarus' "
        "AND id = {};", 
        T::table_name(), std::stoull(id));
    return execute(query);
}

