#pragma once

#include "EntityHandler.hpp"

#include <memory>
#include <tuple>
#include <iostream>

template<typename ...Args>
class Server final {
public:
    Server();
    ~Server() = default;

    void start_server();

private:
    template<typename Handler>
    void start_handler();

private:
    constexpr static int PORT = 24110;
    constexpr static auto addr = "0.0.0.0";
    httplib::Server m_server;
    std::shared_ptr<PostgresController> m_controller;
};

template <typename... Args>
inline Server<Args...>::Server()
{
    m_controller = std::make_shared<PostgresController>();
    m_controller->initialize();
}

template <typename... Args>
inline void Server<Args...>::start_server()
{
    (start_handler<Args>(), ...);

    m_server.listen(addr, PORT);
}

template <typename... Args>
template <typename Handler>
inline void Server<Args...>::start_handler()
{
    auto handler = std::make_shared<EntityHandler<typename Handler::entity_type>>(m_controller);
    handler->init();

    m_server.Post("/api/v1.0/" + Handler::entity_type::name(), [handler, this](const httplib::Request& req, httplib::Response& res) {
        handler->handle_post(req, res);
    });
    
    m_server.Get("/api/v1.0/" + Handler::entity_type::name(), [handler, this](const httplib::Request& req, httplib::Response& res) {
        handler->handle_get_all(req, res);
    });
    
    m_server.Get("/api/v1.0/" + Handler::entity_type::name() + "/(\\d+)", [handler, this](const httplib::Request& req, httplib::Response& res) {
        uint64_t id = std::stoull(req.matches[1]);
        handler->handle_get_one(req, res, id);
    });
    
    m_server.Delete("/api/v1.0/" + Handler::entity_type::name() + "/(\\d+)", [handler, this](const httplib::Request& req, httplib::Response& res) {
        uint64_t id = std::stoull(req.matches[1]);
        handler->handle_delete(req, res, id);
    });
    
    m_server.Put("/api/v1.0/" + Handler::entity_type::name(), [handler, this](const httplib::Request& req, httplib::Response& res) {
        handler->handle_put(req, res);
    });
}
