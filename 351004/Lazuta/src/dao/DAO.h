#pragma once

#include <functional>
#include <vector>
#include <optional>
#include <cstdint>

template <typename T, typename K = uint64_t>
class DAO 
{
public:
    virtual ~DAO() = default;

    virtual K Create(const T& entity) = 0;
    virtual std::optional<T> GetByID(K id) = 0;
    virtual bool Update(K id, const T& entity) = 0;
    virtual bool Delete(K id) = 0;
    virtual std::vector<T> ReadAll() = 0;

    virtual std::vector<T> FindBy(std::function<bool(const T&)> predicate) = 0;
    virtual bool Exists(K id) = 0;
};