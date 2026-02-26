#pragma once

#include <dao/DAO.h>

template <typename T>
class IDatabaseRepository : public DAO<T>
{
    virtual ~IDatabaseRepository() = default;
};