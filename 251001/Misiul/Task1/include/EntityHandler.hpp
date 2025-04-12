#pragma once

#include "Creator.hpp"
#include "Topic.hpp"
#include "Utils.hpp"

#include <httplib.h>
#include <iostream>

namespace ranges = std::ranges;

template<Entity EntityT>
class EntityHandler {
public:
    using entity_type = EntityT;

public:
    virtual ~EntityHandler() = default;

    void handle_post(const httplib::Request& req, httplib::Response& res);
    void handle_get_all(const httplib::Request& req, httplib::Response& res);
    void handle_get_one(const httplib::Request& req, httplib::Response& res, uint64_t id);
    void handle_delete(const httplib::Request& req, httplib::Response& res, uint64_t id);
    void handle_put(const httplib::Request& req, httplib::Response& res);

protected:
    std::vector<EntityT> m_entities;
    uint64_t lastId{1};
};

template <Entity EntityT>
inline void EntityHandler<EntityT>::handle_post(const httplib::Request &req, httplib::Response &res)
{
    auto data = json::parse(req.body);
    if (!data.contains("id")) {
        data["id"] = lastId++;
    } else {
        std::string id_str = data["id"];
        lastId = std::max(lastId, stoul(id_str) + 1);
    }
    auto entity = EntityT(data);
    m_entities.push_back(entity);

    res.status = 201;
    res.set_content(entity.to_json().dump(), "application/json");
}

template <Entity EntityT>
inline void EntityHandler<EntityT>::handle_get_all(const httplib::Request &req, httplib::Response &res)
{
    json entities_json = json::array();

    ranges::transform(m_entities, std::back_inserter(entities_json), [this](const EntityT & entity){
        return entity.to_json();
    });

    res.status = 200;
    res.set_content(entities_json.dump(), "application/json");
}

template <Entity EntityT>
inline void EntityHandler<EntityT>::handle_get_one(const httplib::Request &req, httplib::Response &res, uint64_t id)
{
    auto it = ranges::find_if(m_entities, [id](const EntityT & entity) { 
        return entity.id == id; 
    });

    if (it != m_entities.end()) {
        res.status = 200;
        res.set_content(it->to_json().dump(), "application/json");
    } else {
        res.status = 404;
        res.set_content(json{{"error", "Not found"}}.dump(), "application/json");
    }
}

template <Entity EntityT>
inline void EntityHandler<EntityT>::handle_delete(const httplib::Request &req, httplib::Response &res, uint64_t id)
{
    auto it = ranges::find_if(m_entities, [id](const EntityT & entity) { 
        return entity.id == id; 
    });

    if (it != m_entities.end()) {
        m_entities.erase(it);
        res.status = 204;
    } else {
        res.status = 404;
        res.set_content(json{{"error", "Not found"}}.dump(), "application/json");
    }
}

template <Entity EntityT>
inline void EntityHandler<EntityT>::handle_put(const httplib::Request &req, httplib::Response &res)
{
    auto data = json::parse(req.body);
    if (!data.contains("id")) {
        res.status = 404;
        res.set_content(json{{"error", "Not found"}}.dump(), "application/json");
        return;
    }
    auto entity = EntityT(data);

    auto it = ranges::find_if(m_entities, [id = entity.id](const EntityT & entity) { 
        return entity.id == id; 
    });

    if (it != m_entities.end()) {
        *it = entity;
        res.status = 200;
        res.set_content(it->to_json().dump(), "application/json");
    } else {
        res.status = 404;
        res.set_content(json{{"error", "Not found"}}.dump(), "application/json");
    }
}

template <>
inline void EntityHandler<Creator>::handle_put(const httplib::Request &req, httplib::Response &res)
{
    auto data = json::parse(req.body);
    if (!data.contains("id")) {
        res.status = 404;
        res.set_content(json{{"error", "Not found"}}.dump(), "application/json");
        return;
    }
    auto entity = Creator(data);
    if (entity.login.size() <= 1){
        res.status = 404;
        res.set_content(json{{"error", "Invalid login"}}.dump(), "application/json");
        return;
    }

    auto it = ranges::find_if(m_entities, [id = entity.id](const Creator & entity) { 
        return entity.id == id; 
    });

    if (it != m_entities.end()) {
        *it = entity;
        res.status = 200;
        res.set_content(it->to_json().dump(), "application/json");
    } else {
        res.status = 404;
        res.set_content(json{{"error", "Not found"}}.dump(), "application/json");
    }
}

template <>
inline void EntityHandler<Topic>::handle_put(const httplib::Request &req, httplib::Response &res)
{
    auto data = json::parse(req.body);
    if (!data.contains("id")) {
        res.status = 404;
        res.set_content(json{{"error", "Not found"}}.dump(), "application/json");
        return;
    }
    auto entity = Topic(data);
    if (entity.title.size() <= 1) {
        res.status = 404;
        res.set_content(json{{"error", "Invalid title"}}.dump(), "application/json");
        return;
    }

    auto it = ranges::find_if(m_entities, [id = entity.id](const Topic & entity) { 
        return entity.id == id; 
    });

    if (it != m_entities.end()) {
        *it = entity;
        res.status = 200;
        res.set_content(it->to_json().dump(), "application/json");
    } else {
        res.status = 404;
        res.set_content(json{{"error", "Not found"}}.dump(), "application/json");
    }
}
