#pragma once

#include "Utils.hpp"
#include "DBController.hpp"

#include <httplib.h>
#include <fmt/core.h>

#include <iostream>
#include <memory>

namespace ranges = std::ranges;

template<Entity EntityT>
class EntityHandler {
public:
    using entity_type = EntityT;

public:
    EntityHandler(std::shared_ptr<PostgresController> controller) : m_controller(controller) {}
    virtual ~EntityHandler() = default;

    void init();

    void handle_post(const httplib::Request& req, httplib::Response& res);
    void handle_get_all(const httplib::Request& req, httplib::Response& res);
    void handle_get_one(const httplib::Request& req, httplib::Response& res, uint64_t id);
    void handle_delete(const httplib::Request& req, httplib::Response& res, uint64_t id);
    void handle_put(const httplib::Request& req, httplib::Response& res);

protected:
    std::shared_ptr<PostgresController> m_controller;
};

template<Entity EntityT>
inline void EntityHandler<EntityT>::init()
{
    auto tmp = m_controller->create_table<EntityT>();
}

template <Entity EntityT>
inline void EntityHandler<EntityT>::handle_post(const httplib::Request &req, httplib::Response &res)
{
    EntityT entity{};
    try {
        entity = EntityT(json::parse(req.body));
    }
    catch (const std::exception& e){
        res.status = 415;
        res.set_content(json{{"error", "Invalid JSON"}}.dump(), "application/json");
        return;
    }

    if (m_controller->insert(entity)) {
        res.status = 201;
        res.set_content(entity.to_json().dump(), "application/json");
    } else {
        res.status = 403;
        res.set_content(json{{"error", "Insert failure"}}.dump(), "application/json");
    }
}

template <Entity EntityT>
inline void EntityHandler<EntityT>::handle_get_all(const httplib::Request &req, httplib::Response &res)
{
    std::vector<EntityT> entities = m_controller->get_all<EntityT>();

    json entities_json = json::array();
    std::ranges::transform(entities, std::back_inserter(entities_json), 
        [this](const EntityT& entity) 
    {
        return entity.to_json();
    });

    res.status = 200;
    res.set_content(entities_json.dump(), "application/json");
}

template <Entity EntityT>
inline void EntityHandler<EntityT>::handle_get_one(const httplib::Request &req, httplib::Response &res, uint64_t id)
{
    std::optional<EntityT> entity = m_controller->get_by_id<EntityT>(id);

    if (entity.has_value()) {
        res.status = 200;
        res.set_content(entity.value().to_json().dump(), "application/json");
    }
    else {
        res.status = 404;
        res.set_content(json{{"error", fmt::format("Id not found", id)}}.dump(), "application/json");
    }
}

template <Entity EntityT>
inline void EntityHandler<EntityT>::handle_delete(const httplib::Request &req, httplib::Response &res, uint64_t id)
{
    if (m_controller->delete_by_id<EntityT>(id)){
        res.status = 204;
    }
    else{
        res.status = 404;
    }
}

template <Entity EntityT>
inline void EntityHandler<EntityT>::handle_put(const httplib::Request &req, httplib::Response &res)
{
    EntityT entity{json::parse(req.body)};
    m_controller->update_by_id(entity);
    res.status = 200;
    res.set_content(entity.to_json().dump(), "application/json");
}
