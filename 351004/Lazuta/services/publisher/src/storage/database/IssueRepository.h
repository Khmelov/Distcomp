#pragma once

#include <vector>
#include <cstdint>
#include <variant>
#include <drogon/orm/DbClient.h>
#include <drogon/orm/Mapper.h>
#include <drogon/HttpAppFramework.h>

#include "IDatabaseRepository.h"
#include <models/TblIssue.h>
#include <exceptions/DatabaseError.h>

namespace publisher
{

using namespace drogon_model::distcomp;

class IssueRepository : public IDatabaseRepository<TblIssue>
{
public:
    IssueRepository() = default;
    ~IssueRepository() = default;
    
    std::variant<int64_t, DatabaseError> Create(const TblIssue& entity) override;
    std::variant<TblIssue, DatabaseError> GetByID(int64_t id) override;
    std::variant<bool, DatabaseError> Update(int64_t id, const TblIssue& entity) override;
    std::variant<bool, DatabaseError> Delete(int64_t id) override;
    std::variant<std::vector<TblIssue>, DatabaseError> ReadAll() override;
    std::variant<bool, DatabaseError> Exists(int64_t id) override;
    
    std::variant<std::vector<TblIssue>, DatabaseError> FindByEditorId(int64_t editorId);
    std::variant<std::vector<TblIssue>, DatabaseError> FindByTitle(const std::string& title);
    std::variant<std::vector<TblIssue>, DatabaseError> FindRecent(int limit = 10);
    std::variant<std::vector<TblIssue>, DatabaseError> FindByDateRange(const trantor::Date& from, const trantor::Date& to);
};

};