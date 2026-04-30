//
// Created by dmitry on 30.04.2026.
//

#include "PostCache.h"
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

static std::string PostKey(int64_t id)
{
    return "post:" + std::to_string(id);
}

static Json::Value PostToJson(const TblPost& entity)
{
    Json::Value json;
    json["id"] = static_cast<Json::Int64>(entity.getValueOfId());
    json["issue_id"] = static_cast<Json::Int64>(entity.getValueOfIssueId());
    json["content"] = entity.getValueOfContent();
    return json;
}

static TblPost JsonToPost(const Json::Value& json)
{
    TblPost post;
    post.setId(json["id"].asInt64());
    post.setIssueId(json["issue_id"].asInt64());
    post.setContent(json["content"].asString());
    return post;
}

std::variant<int64_t, DatabaseError> PostCache::Create(const TblPost& entity)
{
    try
    {
        int64_t id = entity.getValueOfId();
        int64_t issueId = entity.getValueOfIssueId();
        std::string key = PostKey(id);
        std::string jsonStr = Json::FastWriter().write(PostToJson(entity));

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
            "sadd posts:all %s",
            key.c_str());

        GetRedisClient()->execCommandSync<bool>(
            [](const drogon::nosql::RedisResult& r) {
                return r.type() == drogon::nosql::RedisResultType::kError;
            },
            "sadd posts:issue:%lld %s",
            (long long)issueId,
            key.c_str());

        return id;
    }
    catch (const std::exception&)
    {
        return DatabaseError::DatabaseError;
    }
}

std::variant<TblPost, DatabaseError> PostCache::GetByID(int64_t id)
{
    try
    {
        std::string key = PostKey(id);

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

        return JsonToPost(json);
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

std::variant<bool, DatabaseError> PostCache::Update(int64_t id, const TblPost& entity)
{
    try
    {
        std::string key = PostKey(id);
        std::string jsonStr = Json::FastWriter().write(PostToJson(entity));

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

std::variant<bool, DatabaseError> PostCache::Delete(int64_t id)
{
    try
    {
        std::string key = PostKey(id);

        auto postResult = GetByID(id);
        int64_t issueId = 0;
        if (std::holds_alternative<TblPost>(postResult))
            issueId = std::get<TblPost>(postResult).getValueOfIssueId();

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
            "srem posts:all %s",
            key.c_str());

        GetRedisClient()->execCommandSync<bool>(
            [](const drogon::nosql::RedisResult& r) {
                return r.type() == drogon::nosql::RedisResultType::kError;
            },
            "srem posts:issue:%lld %s",
            (long long)issueId,
            key.c_str());

        return deleted;
    }
    catch (const std::exception&)
    {
        return DatabaseError::DatabaseError;
    }
}

std::variant<std::vector<TblPost>, DatabaseError> PostCache::ReadAll()
{
    try
    {
        auto keys = GetRedisClient()->execCommandSync<std::vector<drogon::nosql::RedisResult>>(
            [](const drogon::nosql::RedisResult& r) {
                if (r.type() == drogon::nosql::RedisResultType::kNil)
                    return std::vector<drogon::nosql::RedisResult>{};
                return r.asArray();
            },
            "smembers posts:all");

        std::vector<TblPost> posts;
        for (const auto& keyResult : keys)
        {
            std::string key = keyResult.asString();

            auto postData = GetRedisClient()->execCommandSync<std::string>(
                [](const drogon::nosql::RedisResult& r) {
                    return r.asString();
                },
                "get %s",
                key.c_str());

            Json::Value json;
            Json::Reader reader;
            if (reader.parse(postData, json))
                posts.push_back(JsonToPost(json));
        }

        return posts;
    }
    catch (const std::exception&)
    {
        return DatabaseError::DatabaseError;
    }
}

std::variant<bool, DatabaseError> PostCache::Exists(int64_t id)
{
    try
    {
        std::string key = PostKey(id);

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

std::variant<std::vector<TblPost>, DatabaseError> PostCache::FindByIssueId(int64_t issueId)
{
    try
    {
        std::string setKey = "posts:issue:" + std::to_string(issueId);

        auto keys = GetRedisClient()->execCommandSync<std::vector<drogon::nosql::RedisResult>>(
            [](const drogon::nosql::RedisResult& r) {
                if (r.type() == drogon::nosql::RedisResultType::kNil)
                    return std::vector<drogon::nosql::RedisResult>{};
                return r.asArray();
            },
            "smembers %s",
            setKey.c_str());

        std::vector<TblPost> posts;
        for (const auto& keyResult : keys)
        {
            std::string key = keyResult.asString();

            auto postData = GetRedisClient()->execCommandSync<std::string>(
                [](const drogon::nosql::RedisResult& r) {
                    return r.asString();
                },
                "get %s",
                key.c_str());

            Json::Value json;
            Json::Reader reader;
            if (reader.parse(postData, json))
                posts.push_back(JsonToPost(json));
        }

        return posts;
    }
    catch (const std::exception&)
    {
        return DatabaseError::DatabaseError;
    }
}

}
