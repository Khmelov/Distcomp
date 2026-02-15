#pragma once

#include <unordered_map>
#include <shared_mutex>
#include <algorithm>
#include <ctime>

template <typename T>
class BaseInMemoryStorage : public DAO<T> 
{
protected:
    std::unordered_map<uint64_t, T> storage;
    std::shared_mutex mutex;
    std::atomic<uint64_t> nextId{1};

    std::string getCurrentDateTime() 
    {
        time_t rawtime;
        struct tm * timeinfo;
        char buffer[80];

        time(&rawtime);
        timeinfo = localtime(&rawtime);
        strftime(buffer, sizeof(buffer), "%Y-%m-%d %H:%M:%S", timeinfo);
        
        return std::string(buffer);
    }

public:
    uint64_t Create(const T& entity) override 
    {
        std::unique_lock lock(mutex);
        uint64_t id = nextId.fetch_add(1);
        T copy = entity;
        copy.SetId(id);
        
        if constexpr (requires { copy.SetCreated(getCurrentDateTime()); }) 
        {
            copy.SetCreated(getCurrentDateTime());
        }
        
        if constexpr (requires { copy.SetModified(getCurrentDateTime()); }) 
        {
            copy.SetModified(getCurrentDateTime());
        }
        
        storage[id] = copy;
        return id;
    }

    std::optional<T> GetByID(uint64_t id) override 
    {
        std::shared_lock lock(mutex);
        auto it = storage.find(id);
        if (it != storage.end()) 
        {
            return it->second;
        }
        return std::nullopt;
    }

    bool Update(uint64_t id, const T& entity) override 
    {
        std::unique_lock lock(mutex);
        auto it = storage.find(id);
        if (it != storage.end()) 
        {
            T updated = entity;
            updated.SetId(id);
            
            if constexpr (requires { updated.SetModified(getCurrentDateTime()); }) 
            {
                updated.SetModified(getCurrentDateTime());
            }
            
            if constexpr (requires { updated.SetCreated(it->second.GetCreated()); }) 
            {
                updated.SetCreated(it->second.GetCreated());
            }
            
            storage[id] = std::move(updated);
            return true;
        }
        return false;
    }

    bool Delete(uint64_t id) override 
    {
        std::unique_lock lock(mutex);
        return storage.erase(id) > 0;
    }

    std::vector<T> ReadAll() override 
    {
        std::shared_lock lock(mutex);
        std::vector<T> result;
        result.reserve(storage.size());
        for (const auto& [_, entity] : storage) 
            result.push_back(entity);
        return result;
    }

    std::vector<T> FindBy(std::function<bool(const T&)> predicate) override 
    {
        std::shared_lock lock(mutex);
        std::vector<T> result;
        for (const auto& [key, entity] : storage) 
        {
            if (predicate(entity)) 
            {
                result.push_back(entity);
            }
        }
        return result;
    }

    bool Exists(uint64_t id) override 
    {
        std::shared_lock lock(mutex);
        return storage.find(id) != storage.end();
    }
};