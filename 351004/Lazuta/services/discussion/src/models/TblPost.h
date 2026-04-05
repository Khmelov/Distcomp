#pragma once

#include <cstdint>
#include <string>
#include <chrono>

namespace discussion
{

class TblPost
{
public:
    TblPost() = default;
    ~TblPost() = default;

    int64_t GetPostId() const { return post_id_; }
    void SetPostId(int64_t id) { post_id_ = id; }

    int64_t GetIssueId() const { return issue_id_; }
    void SetIssueId(int64_t id) { issue_id_ = id; }

    std::string GetContent() const { return content_; }
    void SetContent(const std::string& content) { content_ = content; }

    std::chrono::system_clock::time_point GetCreated() const { return created_; }
    void SetCreated(const std::chrono::system_clock::time_point& time) { created_ = time; }

    std::chrono::system_clock::time_point GetModified() const { return modified_; }
    void SetModified(const std::chrono::system_clock::time_point& time) { modified_ = time; }

private:
    int64_t post_id_ = 0;
    int64_t issue_id_ = 0;
    std::string content_;
    std::chrono::system_clock::time_point created_;
    std::chrono::system_clock::time_point modified_;
};

}