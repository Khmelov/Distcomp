// PostRepository.cpp
#include "PostRepository.h"
#include <bsoncxx/types.hpp>
#include <mongocxx/options/find.hpp>
#include <mongocxx/options/update.hpp>
#include <mongocxx/result/insert_one.hpp>
#include <mongocxx/result/update.hpp>
#include <mongocxx/result/delete-fwd.hpp>
#include <iostream>

namespace discussion
{

PostRepository::PostRepository()
    : IMongoDBRepository<TblPost>("tbl_post")
{
}

int64_t PostRepository::GetNextId()
{
    auto counters = GetDatabase()["counters"];
    
    bsoncxx::builder::stream::document filter{};
    filter << "_id" << "post_id";
    
    bsoncxx::builder::stream::document update{};
    update << "$inc" << bsoncxx::builder::stream::open_document
           << "value" << 1
           << bsoncxx::builder::stream::close_document;
    
    mongocxx::options::find_one_and_update opts{};
    opts.return_document(mongocxx::options::return_document::k_after);
    opts.upsert(true);
    
    auto result = counters.find_one_and_update(filter.view(), update.view(), opts);
    
    if (result)
    {
        auto view = result->view();
        auto valueElem = view["value"];

        if (valueElem.type() == bsoncxx::type::k_int64)
        {
            return valueElem.get_int64().value;
        }
        else if (valueElem.type() == bsoncxx::type::k_int32)
        {
            return valueElem.get_int32().value;
        }
        else if (valueElem.type() == bsoncxx::type::k_double)
        {
            return static_cast<int64_t>(valueElem.get_double().value);
        }
        else
        {
            std::cerr << "[ERROR] Unexpected type for counter value" << std::endl;
            return 1;
        }
    }
    
    std::cerr << "[ERROR] Failed to get next id" << std::endl;
    return 1;
}

bsoncxx::document::value PostRepository::ToBson(const TblPost& entity)
{
    bsoncxx::builder::stream::document doc{};
    
    doc << "id" << entity.GetPostId()
        << "issue_id" << entity.GetIssueId()
        << "content" << entity.GetContent()
        << "created" << bsoncxx::types::b_date(entity.GetCreated())
        << "modified" << bsoncxx::types::b_date(entity.GetModified());
    
    return doc << bsoncxx::builder::stream::finalize;
}

TblPost PostRepository::FromBson(const bsoncxx::document::view& doc)
{
    TblPost entity;
    
    if (doc["id"])
    {
        entity.SetPostId(doc["id"].get_int64().value);
    }
    
    if (doc["issue_id"])
    {
        entity.SetIssueId(doc["issue_id"].get_int64().value);
    }
    
    if (doc["content"])
    {
        entity.SetContent(doc["content"].get_string().value.data());
    }
    
    if (doc["created"])
    {
        auto created_time = doc["created"].get_date().value;
        entity.SetCreated(std::chrono::system_clock::time_point(
            std::chrono::milliseconds(created_time)));
    }
    
    if (doc["modified"])
    {
        auto modified_time = doc["modified"].get_date().value;
        entity.SetModified(std::chrono::system_clock::time_point(
            std::chrono::milliseconds(modified_time)));
    }
    
    return entity;
}

std::variant<int64_t, DatabaseError> PostRepository::Create(const TblPost& entity)
{
    try
    {
        int64_t newId = GetNextId();
        
        auto now = std::chrono::system_clock::now();
        
        TblPost entityWithId = entity;
        entityWithId.SetPostId(newId);
        entityWithId.SetCreated(now);
        entityWithId.SetModified(now);

        auto doc = ToBson(entityWithId);
        auto result = GetCollection().insert_one(doc.view());
        
        if (result)
        {
            std::cout << "[DEBUG] Post created with id: " << newId << std::endl;
            return newId;
        }
        
        return DatabaseError::DatabaseError;
    }
    catch (const std::exception& e)
    {
        std::cerr << "[ERROR] Create failed: " << e.what() << std::endl;
        return DatabaseError::DatabaseError;
    }
}

std::variant<TblPost, DatabaseError> PostRepository::GetByID(int64_t id)
{
    try
    {
        auto filter = bsoncxx::builder::stream::document{};
        filter << "id" << id;
        
        auto result = GetCollection().find_one(filter.view());
        
        if (result)
        {
            return FromBson(result->view());
        }
        
        return DatabaseError::NotFound;
    }
    catch (const std::exception& e)
    {
        return DatabaseError::DatabaseError;
    }
}

std::variant<bool, DatabaseError> PostRepository::Update(int64_t id, const TblPost& entity)
{
    try
    {
        auto filter = bsoncxx::builder::stream::document{};
        filter << "id" << id;
        
        auto now = std::chrono::system_clock::now();
        
        auto update_doc = bsoncxx::builder::stream::document{};
        update_doc << "$set" << bsoncxx::builder::stream::open_document
                   << "issue_id" << entity.GetIssueId()
                   << "content" << entity.GetContent()
                   << "modified" << bsoncxx::types::b_date(now)
                   << bsoncxx::builder::stream::close_document;
        
        auto result = GetCollection().update_one(filter.view(), update_doc.view());
        
        if (result && result->modified_count() > 0)
        {
            return true;
        }
        
        return false;
    }
    catch (const std::exception& e)
    {
        return DatabaseError::DatabaseError;
    }
}

std::variant<bool, DatabaseError> PostRepository::Delete(int64_t id)
{
    try
    {
        auto filter = bsoncxx::builder::stream::document{};
        filter << "id" << id;
        
        auto result = GetCollection().delete_one(filter.view());
        
        if (result && result->deleted_count() > 0)
        {
            return true;
        }
        
        return false;
    }
    catch (const std::exception& e)
    {
        return DatabaseError::DatabaseError;
    }
}

std::variant<std::vector<TblPost>, DatabaseError> PostRepository::ReadAll()
{
    try
    {
        std::vector<TblPost> posts;
        auto cursor = GetCollection().find({});
        
        for (auto&& doc : cursor)
        {
            posts.push_back(FromBson(doc));
        }
        
        return posts;
    }
    catch (const std::exception& e)
    {
        return DatabaseError::DatabaseError;
    }
}

std::variant<bool, DatabaseError> PostRepository::Exists(int64_t id)
{
    try
    {
        auto filter = bsoncxx::builder::stream::document{};
        filter << "id" << id;
        
        auto count = GetCollection().count_documents(filter.view());
        
        return count > 0;
    }
    catch (const std::exception& e)
    {
        return DatabaseError::DatabaseError;
    }
}

std::variant<std::vector<TblPost>, DatabaseError> PostRepository::FindByIssueId(int64_t issueId)
{
    try
    {
        std::vector<TblPost> posts;
        auto filter = bsoncxx::builder::stream::document{};
        filter << "issue_id" << issueId;
        
        auto cursor = GetCollection().find(filter.view());
        
        for (auto&& doc : cursor)
        {
            posts.push_back(FromBson(doc));
        }
        
        return posts;
    }
    catch (const std::exception& e)
    {
        return DatabaseError::DatabaseError;
    }
}

std::variant<std::vector<TblPost>, DatabaseError> PostRepository::FindRecentByIssue(int64_t issueId, int limit)
{
    try
    {
        std::vector<TblPost> posts;
        auto filter = bsoncxx::builder::stream::document{};
        filter << "issue_id" << issueId;
        
        mongocxx::options::find opts{};
        opts.sort(bsoncxx::builder::stream::document{} << "id" << -1 << bsoncxx::builder::stream::finalize);
        opts.limit(limit);
        
        auto cursor = GetCollection().find(filter.view(), opts);
        
        for (auto&& doc : cursor)
        {
            posts.push_back(FromBson(doc));
        }
        
        return posts;
    }
    catch (const std::exception& e)
    {
        return DatabaseError::DatabaseError;
    }
}

std::variant<std::vector<TblPost>, DatabaseError> PostRepository::FindByContentContaining(const std::string& searchText)
{
    try
    {
        std::vector<TblPost> posts;
        auto filter = bsoncxx::builder::stream::document{};
        filter << "content" << bsoncxx::builder::stream::open_document
               << "$regex" << searchText
               << "$options" << "i"
               << bsoncxx::builder::stream::close_document;
        
        auto cursor = GetCollection().find(filter.view());
        
        for (auto&& doc : cursor)
        {
            posts.push_back(FromBson(doc));
        }
        
        return posts;
    }
    catch (const std::exception& e)
    {
        return DatabaseError::DatabaseError;
    }
}

}