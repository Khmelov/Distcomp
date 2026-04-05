#pragma once

#include <string>
#include <jsoncpp/json/json.h>

<<<<<<< HEAD:351004/Lazuta/services/publisher/src/dto/responses/PostResponseTo.h
namespace publisher::dto
=======
namespace myapp::dto
>>>>>>> f26c601fbbe43710c18d4d0b9d78ec1d65a1357c:351004/Lazuta/src/dto/responses/PostResponseTo.h
{

class PostResponseTo 
{
public:
    unsigned long id;
    unsigned long issueId;
    std::string content;
    std::string created;
    std::string modified;

    Json::Value toJson() const 
    {
        Json::Value json;
        json["id"] = id;
        json["issueId"] = issueId;
        json["content"] = content;
        json["created"] = created;
        json["modified"] = modified;
        return json;
    }
};

};