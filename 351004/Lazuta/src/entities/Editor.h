#pragma once

#include "Entity.h"
#include <string>

class Editor : public Entity
{
private:
    std::string m_login;
    std::string m_password;
    std::string m_firstName;
    std::string m_lastName;

public:
    Editor();
    Editor(const std::string& login, const std::string& password, 
           const std::string& firstName, const std::string& lastName);
    
    ~Editor();
    
    std::string getLogin() const;
    std::string getPassword() const;
    std::string getFirstName() const;
    std::string getLastName() const;
    
    void setLogin(const std::string& login);
    void setPassword(const std::string& password);
    void setFirstName(const std::string& firstName);
    void setLastName(const std::string& lastName);
};