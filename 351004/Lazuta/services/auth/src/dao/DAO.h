#pragma once

#include <functional>
#include <vector>
#include <optional>
#include <variant>
#include <cstdint>
#include <exceptions/DatabaseError.h>

template <typename T, typename K = int64_t, typename E = DatabaseError>
class DAO 
{
public:
    virtual ~DAO() = default;

    virtual std::variant<K, E> Create(const T& entity) = 0;
    virtual std::variant<T, E> GetByID(K id) = 0;
    virtual std::variant<bool, E> Update(K id, const T& entity) = 0;
    virtual std::variant<bool, E> Delete(K id) = 0;
    virtual std::variant<std::vector<T>, E> ReadAll() = 0;
    virtual std::variant<bool, E> Exists(K id) = 0;
};