#pragma once

#include <optional>
#include <vector>
#include <cstdint>
#include <drogon/orm/DbClient.h>
#include <drogon/orm/Mapper.h>
#include <drogon/HttpAppFramework.h>

#include "IDatabaseRepository.h"
#include <models/TblEditor.h>

namespace myapp 
{

using namespace drogon_model::myapp_dev;

class EditorRepository : public IDatabaseRepository<TblEditor>
{  
public:
    EditorRepository() = default;
    ~EditorRepository() = default;
    
    int64_t Create(const TblEditor& entity) override;
    std::optional<TblEditor> GetByID(int64_t id) override;
    bool Update(int64_t id, const TblEditor& entity) override;
    bool Delete(int64_t id) override;
    std::vector<TblEditor> ReadAll() override;
    bool Exists(int64_t id) override;
    
    // Дополнительные методы
    std::optional<TblEditor> FindByLogin(const std::string& login);
    std::optional<TblEditor> FindByLoginAndPassword(const std::string& login, const std::string& password);
};

};