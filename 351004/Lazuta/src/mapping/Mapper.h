#pragma once

#include <entities/Editor.h>
#include <entities/Issue.h>
#include <entities/Label.h>
#include <entities/Post.h>
#include <dto/requests/EditorRequestTo.h>
#include <dto/requests/IssueRequestTo.h>
#include <dto/requests/LabelRequestTo.h>
#include <dto/requests/PostRequestTo.h>
#include <dto/responses/EditorResponseTo.h>
#include <dto/responses/IssueResponseTo.h>
#include <dto/responses/LabelResponseTo.h>
#include <dto/responses/PostResponseTo.h>
#include <vector>

class Mapper 
{
public:
    // Editor mappings
    static Editor ToEntity(const EditorRequestTo& dto) 
    {
        Editor entity;
        entity.setLogin(dto.login);
        entity.setPassword(dto.password);
        entity.setFirstName(dto.firstName);
        entity.setLastName(dto.lastName);
        return entity;
    }

    static Editor ToEntityForUpdate(const EditorRequestTo& dto, unsigned long id) 
    {
        Editor entity;
        entity.SetId(id);
        entity.setLogin(dto.login);
        entity.setPassword(dto.password);
        entity.setFirstName(dto.firstName);
        entity.setLastName(dto.lastName);
        return entity;
    }

    static EditorResponseTo ToResponse(const Editor& entity) 
    {
        EditorResponseTo dto;
        dto.id = entity.GetID();
        dto.login = entity.getLogin();
        dto.firstName = entity.getFirstName();
        dto.lastName = entity.getLastName();
        return dto;
    }

    static std::vector<EditorResponseTo> ToResponseList(const std::vector<Editor>& entities) 
    {
        std::vector<EditorResponseTo> dtos;
        dtos.reserve(entities.size());
        for (const auto& entity : entities) 
        {
            dtos.push_back(ToResponse(entity));
        }
        return dtos;
    }

    // Issue mappings
    static Issue ToEntity(const IssueRequestTo& dto) 
    {
        Issue entity;
        entity.setEditorId(dto.editorId);
        entity.setTitle(dto.title);
        entity.setContent(dto.content);
        entity.setCreated(dto.created);
        entity.setModified(dto.modified);
        return entity;
    }

    static Issue ToEntityForUpdate(const IssueRequestTo& dto, unsigned long id) 
    {
        Issue entity;
        entity.SetId(id);
        entity.setEditorId(dto.editorId);
        entity.setTitle(dto.title);
        entity.setContent(dto.content);
        entity.setCreated(dto.created);
        entity.setModified(dto.modified);
        return entity;
    }

    static IssueResponseTo ToResponse(const Issue& entity) 
    {
        IssueResponseTo dto;
        dto.id = entity.GetID();
        dto.editorId = entity.getEditorId();
        dto.title = entity.getTitle();
        dto.content = entity.getContent();
        dto.created = entity.getCreated();
        dto.modified = entity.getModified();
        return dto;
    }

    static std::vector<IssueResponseTo> ToResponseList(const std::vector<Issue>& entities) 
    {
        std::vector<IssueResponseTo> dtos;
        dtos.reserve(entities.size());
        for (const auto& entity : entities) 
        {
            dtos.push_back(ToResponse(entity));
        }
        return dtos;
    }

    // Label mappings
    static Label ToEntity(const LabelRequestTo& dto) 
    {
        Label entity;
        entity.setName(dto.name);
        return entity;
    }

    static Label ToEntityForUpdate(const LabelRequestTo& dto, unsigned long id) 
    {
        Label entity;
        entity.SetId(id);
        entity.setName(dto.name);
        return entity;
    }

    static LabelResponseTo ToResponse(const Label& entity) 
    {
        LabelResponseTo dto;
        dto.id = entity.GetID();
        dto.name = entity.getName();
        return dto;
    }

    static std::vector<LabelResponseTo> ToResponseList(const std::vector<Label>& entities) 
    {
        std::vector<LabelResponseTo> dtos;
        dtos.reserve(entities.size());
        for (const auto& entity : entities) 
        {
            dtos.push_back(ToResponse(entity));
        }
        return dtos;
    }

    // Post mappings
    static ::Post ToEntity(const PostRequestTo& dto) 
    {
        ::Post entity;
        entity.setEditorId(dto.issueId);
        entity.setContent(dto.content);
        entity.setCreated(dto.created);
        entity.setModified(dto.modified);
        return entity;
    }

    static ::Post ToEntityForUpdate(const PostRequestTo& dto, unsigned long id) 
    {
        ::Post entity;
        entity.SetId(id);
        entity.setEditorId(dto.issueId);
        entity.setContent(dto.content);
        entity.setCreated(dto.created);
        entity.setModified(dto.modified);
        return entity;
    }

    static PostResponseTo ToResponse(const ::Post& entity) 
    {
        PostResponseTo dto;
        dto.id = entity.GetID();
        dto.issueId = entity.getEditorId();
        dto.content = entity.getContent();
        dto.created = entity.getCreated();
        dto.modified = entity.getModified();
        return dto;
    }

    static std::vector<PostResponseTo> ToResponseList(const std::vector<::Post>& entities) 
    {
        std::vector<PostResponseTo> dtos;
        dtos.reserve(entities.size());
        for (const auto& entity : entities) 
        {
            dtos.push_back(ToResponse(entity));
        }
        return dtos;
    }
};