#pragma once

#include <drogon/orm/Mapper.h>
#include <dao/DAO.h>

namespace myapp 
{

using namespace drogon::orm;

template <typename T>
class IDatabaseRepository : public DAO<T>
{
protected:
    Mapper<T> mapper = Mapper<T>(GetDbClient());
    DbClientPtr GetDbClient() const { return drogon::app().getDbClient(); };
};

};