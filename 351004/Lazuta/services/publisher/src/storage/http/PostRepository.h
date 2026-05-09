// PostRepository.h
#pragma once

#include <vector>
#include <cstdint>
#include <variant>
#include <memory>
#include <drogon/HttpClient.h>
#include <models/TblPost.h>
#include <exceptions/DatabaseError.h>

namespace publisher
{

class PostRepository 
{
public:
    PostRepository();
    ~PostRepository() = default;
    
    std::variant<int64_t, DatabaseError> Create(const drogon_model::distcomp::TblPost& entity);
    std::variant<drogon_model::distcomp::TblPost, DatabaseError> GetByID(int64_t id);
    std::variant<bool, DatabaseError> Update(int64_t id, const drogon_model::distcomp::TblPost& entity);
    std::variant<bool, DatabaseError> Delete(int64_t id);
    std::variant<std::vector<drogon_model::distcomp::TblPost>, DatabaseError> ReadAll();
    std::variant<bool, DatabaseError> Exists(int64_t id);
    std::variant<std::vector<drogon_model::distcomp::TblPost>, DatabaseError> FindByIssueId(int64_t issueId);

private:
    drogon::HttpClientPtr m_client;


    // вообще есть встроенные методы
    // но почемуто они не работают
    // а почему документация такое говно???????
    Json::Value ModelToJson(const drogon_model::distcomp::TblPost& entity);
    drogon_model::distcomp::TblPost JsonToModel(const Json::Value& json);
    std::vector<drogon_model::distcomp::TblPost> JsonArrayToModelVector(const Json::Value& jsonArray);
};

}