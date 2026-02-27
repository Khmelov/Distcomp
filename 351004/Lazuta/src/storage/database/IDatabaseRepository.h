#pragma once

#include <dao/DAO.h>

namespace myapp 
{

template <typename T>
class IDatabaseRepository : public DAO<T>
{
protected:
    drogon::orm::DbClientPtr getDbClient() const { return drogon::app().getDbClient(); };
};

};