#include "Editor.h"
#include <iostream>

Editor::Editor() : Entity()
{
    m_login = "";
    m_password = "";
    m_firstName = "";
    m_lastName = "";
}

Editor::Editor(const std::string& login, const std::string& password, 
               const std::string& firstName, const std::string& lastName)
    : Entity()
{
    m_login = login;
    m_password = password;
    m_firstName = firstName;
    m_lastName = lastName;
}

Editor::~Editor()
{
}

std::string Editor::getLogin() const
{
    return m_login;
}

std::string Editor::getPassword() const
{
    return m_password;
}

std::string Editor::getFirstName() const
{
    return m_firstName;
}

std::string Editor::getLastName() const
{
    return m_lastName;
}

void Editor::setLogin(const std::string& login)
{
    m_login = login;
}

void Editor::setPassword(const std::string& password)
{
    m_password = password;
}

void Editor::setFirstName(const std::string& firstName)
{
    m_firstName = firstName;
}

void Editor::setLastName(const std::string& lastName)
{
    m_lastName = lastName;
}
