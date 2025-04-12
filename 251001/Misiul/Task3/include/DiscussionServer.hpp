#pragma once

#include "DiscussionHandler.hpp"

#include <memory>

template<CassandraEntity ... Ts>
class DiscussionServer final {
public:
    DiscussionServer() noexcept;
    ~DiscussionServer() = default;

    void start_server();

private:
    template<CassandraEntity T>
    void register_entity();

private:
    constexpr static auto SERVER_ADDRESS = "0.0.0.0";
    constexpr static uint16_t SERVER_PORT = 24130;

    httplib::Server m_server;
    std::shared_ptr<CassandraController> m_controller;
};

template<CassandraEntity ... Ts>
DiscussionServer<Ts...>::DiscussionServer() noexcept {
    m_controller = std::make_shared<CassandraController>();
    m_controller->initialize();
    (register_entity<Ts>(), ...);
}

template<CassandraEntity ... Ts>
template<CassandraEntity T>
void DiscussionServer<Ts...>::register_entity() {
    auto handler = std::make_shared<DiscussionHandler<T>>(m_controller);
    handler->initialize();
    std::string entity_name = T::name();

    m_server.Post("/api/v1.0/" + entity_name, [handler](const httplib::Request& req, httplib::Response& res) {
        handler->handle_post(req, res);
    });

    m_server.Get("/api/v1.0/" + entity_name, [handler](const httplib::Request& req, httplib::Response& res) {
        handler->handle_get_all(req, res);
    });

    m_server.Get(R"(/api/v1.0/)" + entity_name + R"(/([\w-]+))", [handler](const httplib::Request& req, httplib::Response& res) {
        std::string id = req.matches[1];
        handler->handle_get_one(req, res, id);
    });

    m_server.Delete(R"(/api/v1.0/)" + entity_name + R"(/([\w-]+))", [handler](const httplib::Request& req, httplib::Response& res) {
        std::string id = req.matches[1];
        handler->handle_delete(req, res, id);
    });

    m_server.Put("/api/v1.0/" + entity_name, [handler](const httplib::Request& req, httplib::Response& res) {
        handler->handle_put(req, res);
    });
}

template<CassandraEntity ... Ts>
void DiscussionServer<Ts...>::start_server() {
    m_server.listen(SERVER_ADDRESS, SERVER_PORT);
}
