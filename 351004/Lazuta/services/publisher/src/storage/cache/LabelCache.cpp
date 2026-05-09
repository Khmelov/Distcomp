//
// Created by dmitry on 30.04.2026.
//

#include "LabelCache.h"
#include <drogon/HttpAppFramework.h>
#include <drogon/nosql/RedisClient.h>
#include <drogon/nosql/RedisResult.h>
#include <drogon/nosql/RedisException.h>

#include <json/json.h>
#include <sstream>

using namespace drogon_model::distcomp;

namespace publisher
{

static drogon::nosql::RedisClientPtr GetRedisClient()
{
    return drogon::app().getRedisClient();
}

static std::string LabelKey(int64_t id)
{
    return "label:" + std::to_string(id);
}

static Json::Value LabelToJson(const TblLabel& entity)
{
    Json::Value json;
    json["id"] = static_cast<Json::Int64>(entity.getValueOfId());
    json["name"] = entity.getValueOfName();
    return json;
}

static TblLabel JsonToLabel(const Json::Value& json)
{
    TblLabel label;
    label.setId(json["id"].asInt64());
    label.setName(json["name"].asString());
    return label;
}

std::variant<int64_t, DatabaseError> LabelCache::Create(const TblLabel& entity)
{
    try
    {
        int64_t id = entity.getValueOfId();
        std::string key = LabelKey(id);
        std::string jsonStr = Json::FastWriter().write(LabelToJson(entity));

        bool error = false;
        GetRedisClient()->execCommandSync<bool>(
            [](const drogon::nosql::RedisResult& r) {
                return r.type() == drogon::nosql::RedisResultType::kError;
            },
            "set %s %s",
            key.c_str(),
            jsonStr.c_str());

        GetRedisClient()->execCommandSync<bool>(
            [](const drogon::nosql::RedisResult& r) {
                return r.type() == drogon::nosql::RedisResultType::kError;
            },
            "sadd labels:all %s",
            key.c_str());

        if (error)
            return DatabaseError::DatabaseError;

        return id;
    }
    catch (const std::exception&)
    {
        return DatabaseError::DatabaseError;
    }
}

std::variant<TblLabel, DatabaseError> LabelCache::GetByID(int64_t id)
{
    try
    {
        std::string key = LabelKey(id);

        auto result = GetRedisClient()->execCommandSync<std::string>(
            [](const drogon::nosql::RedisResult& r) {
                if (r.type() == drogon::nosql::RedisResultType::kNil)
                    throw std::runtime_error("not found");
                return r.asString();
            },
            "get %s",
            key.c_str());

        Json::Value json;
        Json::Reader reader;
        if (!reader.parse(result, json))
            return DatabaseError::DatabaseError;

        return JsonToLabel(json);
    }
    catch (const std::runtime_error& e)
    {
        if (std::string(e.what()) == "not found")
            return DatabaseError::NotFound;
        return DatabaseError::DatabaseError;
    }
    catch (const std::exception&)
    {
        return DatabaseError::DatabaseError;
    }
}

std::variant<bool, DatabaseError> LabelCache::Update(int64_t id, const TblLabel& entity)
{
    try
    {
        std::string key = LabelKey(id);
        std::string jsonStr = Json::FastWriter().write(LabelToJson(entity));

        auto exists = GetRedisClient()->execCommandSync<bool>(
            [](const drogon::nosql::RedisResult& r) {
                return r.type() != drogon::nosql::RedisResultType::kNil;
            },
            "get %s",
            key.c_str());

        if (!exists)
            return DatabaseError::NotFound;

        GetRedisClient()->execCommandSync<bool>(
            [](const drogon::nosql::RedisResult& r) {
                return r.type() == drogon::nosql::RedisResultType::kError;
            },
            "set %s %s",
            key.c_str(),
            jsonStr.c_str());

        return true;
    }
    catch (const std::exception&)
    {
        return DatabaseError::DatabaseError;
    }
}

std::variant<bool, DatabaseError> LabelCache::Delete(int64_t id)
{
    try
    {
        std::string key = LabelKey(id);

        auto deleted = GetRedisClient()->execCommandSync<bool>(
            [](const drogon::nosql::RedisResult& r) {
                return r.asInteger() > 0;
            },
            "del %s",
            key.c_str());

        GetRedisClient()->execCommandSync<bool>(
            [](const drogon::nosql::RedisResult& r) {
                return r.type() == drogon::nosql::RedisResultType::kError;
            },
            "srem labels:all %s",
            key.c_str());

        return deleted;
    }
    catch (const std::exception&)
    {
        return DatabaseError::DatabaseError;
    }
}

std::variant<std::vector<TblLabel>, DatabaseError> LabelCache::ReadAll()
{
    try
    {
        auto ids = GetRedisClient()->execCommandSync<std::vector<drogon::nosql::RedisResult>>(
            [](const drogon::nosql::RedisResult& r) {
                if (r.type() == drogon::nosql::RedisResultType::kNil)
                    return std::vector<drogon::nosql::RedisResult>{};
                return r.asArray();
            },
            "smembers labels:all");

        std::vector<TblLabel> labels;
        for (const auto& idResult : ids)
        {
            std::string key = idResult.asString();

            auto labelData = GetRedisClient()->execCommandSync<std::string>(
                [](const drogon::nosql::RedisResult& r) {
                    return r.asString();
                },
                "get %s",
                key.c_str());

            Json::Value json;
            Json::Reader reader;
            if (reader.parse(labelData, json))
                labels.push_back(JsonToLabel(json));
        }

        return labels;
    }
    catch (const std::exception&)
    {
        return DatabaseError::DatabaseError;
    }
}

std::variant<bool, DatabaseError> LabelCache::Exists(int64_t id)
{
    try
    {
        std::string key = LabelKey(id);

        auto exists = GetRedisClient()->execCommandSync<bool>(
            [](const drogon::nosql::RedisResult& r) {
                return r.type() != drogon::nosql::RedisResultType::kNil;
            },
            "get %s",
            key.c_str());

        return exists;
    }
    catch (const std::exception&)
    {
        return DatabaseError::DatabaseError;
    }
}

std::variant<TblLabel, DatabaseError> LabelCache::FindByName(const std::string& name)
{
    try
    {
        auto allResult = ReadAll();
        if (std::holds_alternative<DatabaseError>(allResult))
            return std::get<DatabaseError>(allResult);

        auto labels = std::get<std::vector<TblLabel>>(allResult);
        for (const auto& label : labels)
        {
            if (label.getValueOfName() == name)
                return label;
        }

        return DatabaseError::NotFound;
    }
    catch (const std::exception&)
    {
        return DatabaseError::DatabaseError;
    }
}

std::variant<std::vector<TblLabel>, DatabaseError> LabelCache::FindByNameContaining(const std::string& substring)
{
    try
    {
        auto allResult = ReadAll();
        if (std::holds_alternative<DatabaseError>(allResult))
            return std::get<DatabaseError>(allResult);

        auto labels = std::get<std::vector<TblLabel>>(allResult);
        std::vector<TblLabel> result;
        for (const auto& label : labels)
        {
            if (label.getValueOfName().find(substring) != std::string::npos)
                result.push_back(label);
        }

        return result;
    }
    catch (const std::exception&)
    {
        return DatabaseError::DatabaseError;
    }
}

}