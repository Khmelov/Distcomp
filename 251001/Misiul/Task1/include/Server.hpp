#pragma once

#include "EntityHandler.hpp"

#include <memory>
#include <tuple>
#include <iostream>

template<typename ...Args>
class Server final {
public:
    Server() = default;
    ~Server() = default;

    void start_server();

private:
    using Handlers = std::tuple<Args...>;

private:
    template<typename Handler>
    void start_handler(Handler& handler);

private:
    constexpr static int PORT = 24110;
    constexpr static auto addr = "0.0.0.0";
    httplib::Server m_server;
    Handlers m_handlers;
};

template <typename... Args>
inline void Server<Args...>::start_server()
{
    std::apply([this](auto&... handler) {
        (start_handler(handler), ...);
    }, m_handlers);

    m_server.listen(addr, PORT);
}

template <typename... Args>
template <typename Handler>
inline void Server<Args...>::start_handler(Handler &handler)
{
    m_server.Post("/api/v1.0/" + Handler::entity_type::name(), [&, this](const httplib::Request& req, httplib::Response& res) {
        handler.handle_post(req, res);
    });
    
    m_server.Get("/api/v1.0/" + Handler::entity_type::name(), [&, this](const httplib::Request& req, httplib::Response& res) {
        handler.handle_get_all(req, res);
    });
    
    m_server.Get("/api/v1.0/" + Handler::entity_type::name() + "/(\\d+)", [&, this](const httplib::Request& req, httplib::Response& res) {
        uint64_t id = std::stoull(req.matches[1]);
        handler.handle_get_one(req, res, id);
    });
    
    m_server.Delete("/api/v1.0/" + Handler::entity_type::name() + "/(\\d+)", [&, this](const httplib::Request& req, httplib::Response& res) {
        uint64_t id = std::stoull(req.matches[1]);
        handler.handle_delete(req, res, id);
    });
    
    m_server.Put("/api/v1.0/" + Handler::entity_type::name(), [&, this](const httplib::Request& req, httplib::Response& res) {
        handler.handle_put(req, res);
    });
}
