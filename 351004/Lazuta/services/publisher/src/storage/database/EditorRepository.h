#pragma once

#include <vector>
#include <cstdint>
#include <variant>
#include <drogon/orm/DbClient.h>
#include <drogon/orm/Mapper.h>
#include <drogon/HttpAppFramework.h>

#include "IDatabaseRepository.h"
#include <models/TblEditor.h>
#include <exceptions/DatabaseError.h>

namespace myapp
{

using namespace drogon_model::distcomp;

class EditorRepository : public IDatabaseRepository<TblEditor>
{
public:
    EditorRepository() = default;
    ~EditorRepository() = default;
    
    std::variant<int64_t, DatabaseError> Create(const TblEditor& entity) override;
    std::variant<TblEditor, DatabaseError> GetByID(int64_t id) override;
    std::variant<bool, DatabaseError> Update(int64_t id, const TblEditor& entity) override;
    std::variant<bool, DatabaseError> Delete(int64_t id) override;
    std::variant<std::vector<TblEditor>, DatabaseError> ReadAll() override;
    std::variant<bool, DatabaseError> Exists(int64_t id) override;
};

};