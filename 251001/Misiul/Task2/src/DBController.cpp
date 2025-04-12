#include "DBController.hpp"

#include <fmt/core.h>

#include <iostream>

PostgresController::~PostgresController() {
    if (m_connection && m_connection->is_open()) {
        m_connection->close();
    }
}

bool PostgresController::initialize() {
    std::string connection_query = fmt::format(
        "dbname={} user={} password={} host={} port={}", 
        SCEME, USER, PASSWORD, HOST, PORT
    );

    try {
        m_connection = std::make_unique<pqxx::connection>(connection_query);
        if (!m_connection->is_open()) {
            std::cerr << "Connection failed!" << std::endl;
            return false;
        }
    } catch (const std::exception& e) {
        std::cerr << "Connection error: " << e.what() << std::endl;
        return false;
    }
    return true;
}

pqxx::result PostgresController::execute(const std::string& query) noexcept {
    pqxx::result result{};
    try {
        pqxx::work txn(*m_connection);
        result = txn.exec(query);
        txn.commit();
    } catch (const pqxx::sql_error& e) {
        std::cerr << "SQL error: " << e.what() << std::endl;
        std::cerr << "Query was: " << e.query() << std::endl;
    } catch (const std::exception& e) {
        std::cerr << "Error: " << e.what() << std::endl;
    }
    return result;
}
