#include "CassandraController.hpp"

CassandraController::CassandraController() noexcept {
    m_cluster = cass_cluster_new();
    m_session = cass_session_new();
}

CassandraController::~CassandraController() {
    if (m_session) cass_session_free(m_session);
    if (m_cluster) cass_cluster_free(m_cluster);
}

bool CassandraController::initialize() {
    m_cluster = cass_cluster_new();
    m_session = cass_session_new();

    cass_cluster_set_contact_points(m_cluster, HOST);
    cass_cluster_set_port(m_cluster, PORT);

    CassFuture* connect_future = cass_session_connect(m_session, m_cluster);
    if (cass_future_error_code(connect_future) != CASS_OK) {
        std::cerr << "Failed to connect to Cassandra" << std::endl;
        cass_future_free(connect_future);
        return false;
    }
    cass_future_free(connect_future);

    std::string create_keyspace_query =
        "CREATE KEYSPACE IF NOT EXISTS " + std::string(SCHEMA) +
        " WITH REPLICATION = { 'class' : 'SimpleStrategy', 'replication_factor' : 1 };";

    if (!execute(create_keyspace_query)) {
        std::cerr << "Failed to create keyspace: " << SCHEMA << std::endl;
        return false;
    }

    std::string use_query = "USE " + std::string(SCHEMA) + ";";
    if (!execute(use_query)) {
        std::cerr << "Failed to use keyspace: " << SCHEMA << std::endl;
        return false;
    }

    return true;
}

bool CassandraController::execute(const std::string& query) noexcept {
    CassStatement* statement = cass_statement_new(query.c_str(), 0);
    CassFuture* future = cass_session_execute(m_session, statement);

    bool success = cass_future_error_code(future) == CASS_OK;

    if (!success) {
        const char* message;
        size_t message_length;
        cass_future_error_message(future, &message, &message_length);

        std::cerr << "Cassandra query failed: " << query << std::endl;
        std::cerr << "Error message: " << std::string(message, message_length) << std::endl;
    }

    cass_future_free(future);
    cass_statement_free(statement);
    return success;
}

CassStatement* CassandraController::execute_query(const std::string& query) {
    CassStatement* statement = cass_statement_new(query.c_str(), 0);
    CassFuture* future = cass_session_execute(m_session, statement);
    
    if (cass_future_error_code(future) != CASS_OK) {
        std::cerr << "Cassandra query failed: " << query << std::endl;
        const char* message;
        size_t message_length;
        cass_future_error_message(future, &message, &message_length);

        std::cerr << "Cassandra query failed: " << query << std::endl;
        std::cerr << "Error message: " << std::string(message, message_length) << std::endl;

        cass_future_free(future);
        cass_statement_free(statement);
        return nullptr;
    }

    cass_future_free(future);
    return statement;
}
