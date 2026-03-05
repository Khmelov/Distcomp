#pragma once

#include <vector>
#include <string>
#include <memory>

#include <models/TblEditor.h>
#include <models/TblIssue.h>
#include <models/TblLabel.h>
#include <models/TblPost.h>
#include <models/TblIssueLabel.h>

#include <dto/requests/IssueLabelRequestTo.h>
#include <dto/requests/EditorRequestTo.h>
#include <dto/requests/IssueRequestTo.h>
#include <dto/requests/LabelRequestTo.h>
#include <dto/requests/PostRequestTo.h>

#include <dto/responses/IssueLabelResponseTo.h>
#include <dto/responses/EditorResponseTo.h>
#include <dto/responses/IssueResponseTo.h>
#include <dto/responses/LabelResponseTo.h>
#include <dto/responses/PostResponseTo.h>

namespace myapp
{

using namespace drogon_model::distcomp;
using namespace dto;

class DtoMapper
{
public:
    // ==================== Editor mappings ====================
    static TblEditor ToEntity(const EditorRequestTo& dto) 
    {
        TblEditor entity;
        entity.setLogin(dto.login);
        entity.setPassword(dto.password);
        entity.setFirstname(dto.firstName);
        entity.setLastname(dto.lastName);
        return entity;
    }

    static TblEditor ToEntityForUpdate(const EditorRequestTo& dto, int64_t id) 
    {
        TblEditor entity;
        entity.setId(id);
        entity.setLogin(dto.login);
        entity.setPassword(dto.password);
        entity.setFirstname(dto.firstName);
        entity.setLastname(dto.lastName);
        return entity;
    }

    static EditorResponseTo ToResponse(const TblEditor& entity) 
    {
        EditorResponseTo dto;
        dto.id = entity.getValueOfId();
        dto.login = entity.getValueOfLogin();
        dto.firstName = entity.getValueOfFirstname();
        dto.lastName = entity.getValueOfLastname();
        // Пароль не включаем в ответ!
        return dto;
    }

    static std::vector<EditorResponseTo> ToResponseList(const std::vector<TblEditor>& entities) 
    {
        std::vector<EditorResponseTo> dtos;
        dtos.reserve(entities.size());
        for (const auto& entity : entities) 
        {
            dtos.push_back(ToResponse(entity));
        }
        return dtos;
    }

    // ==================== Issue mappings ====================
    static TblIssue ToEntity(const IssueRequestTo& dto) 
    {
        TblIssue entity;
        entity.setEditorId(dto.editorId);
        entity.setTitle(dto.title);
        entity.setContent(dto.content);
        return entity;
    }

    static TblIssue ToEntityForUpdate(const IssueRequestTo& dto, int64_t id) 
    {
        TblIssue entity;
        entity.setId(id);
        entity.setEditorId(dto.editorId);
        entity.setTitle(dto.title);
        entity.setContent(dto.content);
        return entity;
    }

    static IssueResponseTo ToResponse(const TblIssue& entity) 
    {
        IssueResponseTo dto;
        dto.id = entity.getValueOfId();
        dto.editorId = entity.getValueOfEditorId();
        dto.title = entity.getValueOfTitle();
        dto.content = entity.getValueOfContent();
        dto.created = entity.getValueOfCreated().toFormattedString(false);
        dto.modified = entity.getValueOfModified().toFormattedString(false);
        return dto;
    }

    static std::vector<IssueResponseTo> ToResponseList(const std::vector<TblIssue>& entities) 
    {
        std::vector<IssueResponseTo> dtos;
        dtos.reserve(entities.size());
        for (const auto& entity : entities) 
        {
            dtos.push_back(ToResponse(entity));
        }
        return dtos;
    }

    // ==================== Label mappings ====================
    static TblLabel ToEntity(const LabelRequestTo& dto) 
    {
        TblLabel entity;
        entity.setName(dto.name);
        return entity;
    }

    static TblLabel ToEntityForUpdate(const LabelRequestTo& dto, int64_t id) 
    {
        TblLabel entity;
        entity.setId(id);
        entity.setName(dto.name);
        return entity;
    }

    static LabelResponseTo ToResponse(const TblLabel& entity) 
    {
        LabelResponseTo dto;
        dto.id = entity.getValueOfId();
        dto.name = entity.getValueOfName();
        return dto;
    }

    static std::vector<LabelResponseTo> ToResponseList(const std::vector<TblLabel>& entities) 
    {
        std::vector<LabelResponseTo> dtos;
        dtos.reserve(entities.size());
        for (const auto& entity : entities) 
        {
            dtos.push_back(ToResponse(entity));
        }
        return dtos;
    }

    // ==================== Post mappings ====================

    static TblPost ToEntity(const PostRequestTo& dto) 
    {
        TblPost entity;
        entity.setIssueId(dto.issueId);
        entity.setContent(dto.content);
        return entity;
    }

    static TblPost ToEntityForUpdate(const PostRequestTo& dto, int64_t id) 
    {
        TblPost entity;
        entity.setId(id);
        entity.setIssueId(dto.issueId);
        entity.setContent(dto.content);
        return entity;
    }

    static PostResponseTo ToResponse(const TblPost& entity) 
    {
        PostResponseTo dto;
        dto.id = entity.getValueOfId();
        dto.issueId = entity.getValueOfIssueId();
        dto.content = entity.getValueOfContent();
        return dto;
    }

    static std::vector<PostResponseTo> ToResponseList(const std::vector<TblPost>& entities) 
    {
        std::vector<PostResponseTo> dtos;
        dtos.reserve(entities.size());
        for (const auto& entity : entities) 
        {
            dtos.push_back(ToResponse(entity));
        }
        return dtos;
    }

    // ==================== IssueLabel mappings ====================
    static TblIssueLabel ToEntity(const IssueLabelRequestTo& dto) 
    {
        TblIssueLabel entity;
        entity.setIssueId(dto.issueId);
        entity.setLabelId(dto.labelId);
        return entity;
    }

    static TblIssueLabel ToEntityForUpdate(const IssueLabelRequestTo& dto, int64_t id) 
    {
        TblIssueLabel entity;
        entity.setId(id);
        entity.setIssueId(dto.issueId);
        entity.setLabelId(dto.labelId);
        return entity;
    }

    static IssueLabelResponseTo ToResponse(const TblIssueLabel& entity) 
    {
        IssueLabelResponseTo dto;
        dto.id = entity.getValueOfId();
        dto.issueId = entity.getValueOfIssueId();
        dto.labelId = entity.getValueOfLabelId();
        return dto;
    }

    static std::vector<IssueLabelResponseTo> ToResponseList(const std::vector<TblIssueLabel>& entities) 
    {
        std::vector<IssueLabelResponseTo> dtos;
        dtos.reserve(entities.size());
        for (const auto& entity : entities) 
        {
            dtos.push_back(ToResponse(entity));
        }
        return dtos;
    }
};

} // namespace myapp