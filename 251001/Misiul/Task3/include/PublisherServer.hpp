#pragma once

#include <memory>

#include "PublisherHandler.hpp"


template<PostgresEntity ... Ts>
class PublisherServer final {
public:
    PublisherServer() noexcept;
    ~PublisherServer() = default;

    void redirect(const std::string& query, uint16_t port);
    void start_server();

private:
    template<PostgresEntity T>
    void register_entity();

private:
    constexpr static auto SERVER_ADDRESS = "0.0.0.0";
    constexpr static uint16_t SERVER_PORT = 24110;

    httplib::Server m_server;
    std::shared_ptr<PostgresController> m_controller;
};

template<PostgresEntity ... Ts>
PublisherServer<Ts...>::PublisherServer() noexcept {
    m_controller = std::make_shared<PostgresController>();
    m_controller->initialize();
    (register_entity<Ts>(), ...);
}

template<PostgresEntity ... Ts>
void PublisherServer<Ts...>::redirect(const std::string& query, uint16_t port) {
    m_server.Get("/api/v1.0/" + query, [port](const httplib::Request& req, httplib::Response& res) {
        httplib::Client client(SERVER_ADDRESS, port);
        auto result = client.Get(req.path.c_str());

        if (result && result->status == 200) {
            res.status = result->status;
            res.set_content(result->body, result->get_header_value("Content-Type"));
        } else {
            res.status = 502;
            res.set_content("Failed to redirect request", "text/plain");
        }
    });
    
    m_server.Get("/api/v1.0/" + query + "/(\\d+)", [port](const httplib::Request& req, httplib::Response& res) {
        httplib::Client client(SERVER_ADDRESS, port);
        auto result = client.Get(req.path.c_str());

        if (result && result->status == 200) {
            res.status = result->status;
            res.set_content(result->body, result->get_header_value("Content-Type"));
        } else {
            res.status = 502;
            res.set_content("Failed to redirect request", "text/plain");
        }
    });

    m_server.Post("/api/v1.0/" + query, [port](const httplib::Request& req, httplib::Response& res) {
        httplib::Client client(SERVER_ADDRESS, port);
        auto result = client.Post(req.path.c_str(), req.body, req.get_header_value("Content-Type"));

        if (result) {
            res.status = result->status;
            res.set_content(result->body, result->get_header_value("Content-Type"));
        } else {
            res.status = 502;
            res.set_content("Failed to redirect request", "text/plain");
        }
    });

    m_server.Put("/api/v1.0/" + query, [port](const httplib::Request& req, httplib::Response& res) {
        httplib::Client client(SERVER_ADDRESS, port);
        auto result = client.Put(req.path.c_str(), req.body, req.get_header_value("Content-Type"));

        if (result) {
            res.status = result->status;
            res.set_content(result->body, result->get_header_value("Content-Type"));
        } else {
            res.status = 502;
            res.set_content("Failed to redirect request", "text/plain");
        }
    });

    m_server.Delete("/api/v1.0/" + query + "/(\\d+)", [port](const httplib::Request& req, httplib::Response& res) {
        httplib::Client client(SERVER_ADDRESS, port);
        auto result = client.Delete(req.path.c_str());
        if (result) {
            res.status = result->status;
            res.set_content(result->body, result->get_header_value("Content-Type"));
        } else {
            res.status = 502;
            res.set_content("Failed to redirect request", "text/plain");
        }
    });
}

template<PostgresEntity ... Ts>
template<PostgresEntity T>
void PublisherServer<Ts...>::register_entity() {
    auto handler = std::make_shared<PublisherHandler<T>>(m_controller);
    handler->initialize();
    std::string entity_name = T::name();

    m_server.Post("/api/v1.0/" + entity_name, [handler](const httplib::Request& req, httplib::Response& res) {
        handler->handle_post(req, res);
    });

    m_server.Get("/api/v1.0/" + entity_name, [handler](const httplib::Request& req, httplib::Response& res) {
        handler->handle_get_all(req, res);
    });

    m_server.Get("/api/v1.0/" + entity_name + "/(\\d+)", [handler](const httplib::Request& req, httplib::Response& res) {
        uint64_t id = std::stoull(req.matches[1]);
        handler->handle_get_one(req, res, id);
    });

    m_server.Delete("/api/v1.0/" + entity_name + "/(\\d+)", [handler](const httplib::Request& req, httplib::Response& res) {
        uint64_t id = std::stoull(req.matches[1]);
        handler->handle_delete(req, res, id);
    });

    m_server.Put("/api/v1.0/" + entity_name, [handler](const httplib::Request& req, httplib::Response& res) {
        handler->handle_put(req, res);
    });
}

template<PostgresEntity ... Ts>
void PublisherServer<Ts...>::start_server() {
    m_server.listen(SERVER_ADDRESS, SERVER_PORT);
}
