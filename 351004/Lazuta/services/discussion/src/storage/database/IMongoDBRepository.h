#pragma once

#include <dao/DAO.h>
#include <mongocxx/client.hpp>
#include <mongocxx/instance.hpp>
#include <mongocxx/database.hpp>
#include <mongocxx/collection.hpp>
#include <bsoncxx/document/value.hpp>
#include <bsoncxx/document/view.hpp>

namespace discussion 
{

template <typename T>
class IMongoDBRepository : public DAO<T>
{
private:
    static mongocxx::instance m_instance;
    mongocxx::client m_client;
    mongocxx::database m_db;
    mongocxx::collection m_collection;

protected:
    IMongoDBRepository(const std::string& collection_name)
        : m_client(mongocxx::uri{"mongodb://localhost:9042"})
        , m_db(m_client["distcomp"])
        , m_collection(m_db[collection_name])
    {
    }

    mongocxx::collection& GetCollection() 
    { 
        return m_collection; 
    }

    mongocxx::database& GetDatabase() 
    { 
        return m_db;
    }

    virtual ~IMongoDBRepository() = default;
};

}