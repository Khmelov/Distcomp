#pragma once

#include <optional>
#include <vector>
#include <cstdint>
#include <drogon/orm/DbClient.h>
#include <drogon/orm/Mapper.h>
#include <drogon/HttpAppFramework.h>

#include "IDatabaseRepository.h"
#include <models/TblIssue.h>

namespace myapp 
{

using namespace drogon_model::myapp_dev;

class IssueRepository : public IDatabaseRepository<TblIssue>
{
public:
    IssueRepository() = default;
    ~IssueRepository() = default;
    
    int64_t Create(const TblIssue& entity) override;
    std::optional<TblIssue> GetByID(int64_t id) override;
    bool Update(int64_t id, const TblIssue& entity) override;
    bool Delete(int64_t id) override;
    std::vector<TblIssue> ReadAll() override;
    bool Exists(int64_t id) override;
    
    // Дополнительные методы
    std::vector<TblIssue> FindByEditorId(int64_t editorId);
    std::vector<TblIssue> FindRecent(int limit = 10);
    std::vector<TblIssue> FindByDateRange(const trantor::Date& from, const trantor::Date& to);
};

};