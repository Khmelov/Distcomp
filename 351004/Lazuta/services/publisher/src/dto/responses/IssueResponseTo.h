#pragma once

#include <string>
#include <jsoncpp/json/json.h>

<<<<<<< HEAD:351004/Lazuta/services/publisher/src/dto/responses/IssueResponseTo.h
namespace publisher::dto
=======
namespace myapp::dto
>>>>>>> f26c601fbbe43710c18d4d0b9d78ec1d65a1357c:351004/Lazuta/src/dto/responses/IssueResponseTo.h
{

class IssueResponseTo 
{
public:
    unsigned long id;
    unsigned long editorId;
    std::string title;
    std::string content;
    std::string created;
    std::string modified;

    Json::Value toJson() const 
    {
        Json::Value json;
        json["id"] = id;
        json["editorId"] = editorId;
        json["title"] = title;
        json["content"] = content;
        json["created"] = created;
        json["modified"] = modified;
        return json;
    }
};

};