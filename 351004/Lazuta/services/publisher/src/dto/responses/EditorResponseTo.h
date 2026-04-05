#pragma once

#include <string>
#include <jsoncpp/json/json.h>

<<<<<<< HEAD:351004/Lazuta/services/publisher/src/dto/responses/EditorResponseTo.h
namespace publisher::dto
=======
namespace myapp::dto
>>>>>>> f26c601fbbe43710c18d4d0b9d78ec1d65a1357c:351004/Lazuta/src/dto/responses/EditorResponseTo.h
{

class EditorResponseTo 
{
public:
    unsigned long id;
    std::string login;
    std::string firstName;
    std::string lastName;

    Json::Value toJson() const 
    {
        Json::Value json;
        json["id"] = id;
        json["login"] = login;
        json["firstname"] = firstName;
        json["lastname"] = lastName;
        return json;
    }
};

};