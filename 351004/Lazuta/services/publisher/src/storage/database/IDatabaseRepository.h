#pragma once

#include <drogon/orm/Mapper.h>
#include <dao/DAO.h>

namespace publisher 
{

template <typename T>
class IDatabaseRepository : public DAO<T>
{
protected:
    drogon::orm::DbClientPtr GetDbClient() const { return drogon::app().getDbClient(); };
    drogon::orm::Mapper<T> Mapper() { return drogon::orm::Mapper<T>(GetDbClient()); };
};

};