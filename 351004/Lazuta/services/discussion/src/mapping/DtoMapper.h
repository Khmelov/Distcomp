#pragma once

#include <vector>
#include <string>
#include <memory>
#include <chrono>
#include <iomanip>
#include <sstream>

#include <models/TblPost.h>
#include <dto/requests/PostRequestTo.h>
#include <dto/responses/PostResponseTo.h>

namespace discussion
{

using namespace dto;

class DtoMapper
{
public:
    // ==================== Post mappings ====================

    static TblPost ToEntity(const PostRequestTo& dto) 
    {
        TblPost entity;
        entity.SetIssueId(dto.issueId);
        entity.SetContent(dto.content);
        return entity;
    }

    static TblPost ToEntityForUpdate(const PostRequestTo& dto, int64_t id) 
    {
        TblPost entity;
        entity.SetPostId(id);
        entity.SetIssueId(dto.issueId);
        entity.SetContent(dto.content);
        return entity;
    }

    static PostResponseTo ToResponse(const TblPost& entity) 
    {
        PostResponseTo dto;
        dto.id = entity.GetPostId();
        dto.issueId = entity.GetIssueId();
        dto.content = entity.GetContent();
        dto.created = TimePointToString(entity.GetCreated());
        dto.modified = TimePointToString(entity.GetModified());
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

private:
    static std::string TimePointToString(const std::chrono::system_clock::time_point& tp)
    {
        auto time_t = std::chrono::system_clock::to_time_t(tp);
        auto ms = std::chrono::duration_cast<std::chrono::milliseconds>(
            tp.time_since_epoch()) % 1000;
        
        std::stringstream ss;
        ss << std::put_time(std::localtime(&time_t), "%Y-%m-%dT%H:%M:%S");
        ss << "." << std::setfill('0') << std::setw(3) << ms.count();
        return ss.str();
    }
};

}