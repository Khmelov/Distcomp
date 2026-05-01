#include "AuthService.h"
#include <exceptions/AuthException.h>
#include <chrono>
#include <iostream>

namespace auth
{

AuthResponseTo AuthService::login(const LoginRequestTo& req) {
    std::cout << "[AUTH SERVICE] Login attempt for: " << req.login << std::endl;
    
    auto editor = repository_->findByLogin(req.login);
    if (!editor) {
        std::cout << "[AUTH SERVICE] Login failed: user not found - " << req.login << std::endl;
        throw UnauthorizedException("Invalid login or password");
    }
    
    if (!checkPassword(req.password, editor->getValueOfPassword())) {
        std::cout << "[AUTH SERVICE] Login failed: invalid password for - " << req.login << std::endl;
        throw UnauthorizedException("Invalid login or password");
    }
    
    std::string role = editor->getValueOfRole();

    std::string token = JwtUtils::generateToken(req.login, role);
    
    std::cout << "[AUTH SERVICE] Login successful for: " << req.login << " (role: " << role << ")" << std::endl;
    
    AuthResponseTo response;
    response.accessToken = token;
    return response;
}

EditorResponseTo AuthService::registerEditor(const RegisterRequestTo& req) {
    std::cout << "[AUTH SERVICE] Registration attempt for: " << req.login << " (role: " << req.role << ")" << std::endl;
    
    if (repository_->existsByLogin(req.login)) {
        std::cout << "[AUTH SERVICE] Registration failed: login already exists - " << req.login << std::endl;
        throw ValidationException("Login already exists");
    }

    
    if (req.role != "" && req.role != "ADMIN" && req.role != "CUSTOMER") {
        std::cout << "[AUTH SERVICE] Registration failed: invalid role - " << req.role << std::endl;
        throw ValidationException("Invalid role. Must be ADMIN or CUSTOMER");
    }
    
    TblEditor editor;
    editor.setLogin(req.login);
    editor.setPassword(hashPassword(req.password));
    editor.setFirstname(req.firstName);
    editor.setLastname(req.lastName);
    editor.setRole(
                    (req.role == "")
                    ? "CUSTOMER"
                    : req.role
                    );
    
    auto result = repository_->Create(editor);

    if (std::holds_alternative<DatabaseError>(result))
    {
        std::cout << "[AUTH SERVICE] Registration failed: database error for - " << req.login << std::endl;
        throw AuthException(1, "error ban ban ban");
    }

    auto createdId = std::get<int64_t>(result);
    auto createdEditor = std::get<TblEditor>(repository_->GetByID(createdId));
    
    std::cout << "[AUTH SERVICE] Registration successful for: " << req.firstName << " (id: " << createdId << ")" << std::endl;
    
    EditorResponseTo response;
    response.id = createdEditor.getValueOfId();
    response.login = createdEditor.getValueOfLogin();
    response.firstName = *createdEditor.getFirstname();
    response.lastName = *createdEditor.getLastname();
    response.role = editor.getValueOfRole();
    return response;
}

std::optional<EditorResponseTo> AuthService::getEditorByLogin(const std::string& login) {
    std::cout << "[AUTH SERVICE] Get editor by login: " << login << std::endl;
    
    auto editor = repository_->findByLogin(login);
    if (!editor) {
        std::cout << "[AUTH SERVICE] Editor not found: " << login << std::endl;
        return std::nullopt;
    }
    
    std::cout << "[AUTH SERVICE] Editor found: " << login << " (id: " << editor->getValueOfId() << ")" << std::endl;
    
    EditorResponseTo response;
    response.id = editor->getValueOfId();
    response.login = editor->getValueOfLogin();
    response.firstName = editor->getValueOfFirstname();
    response.lastName = editor->getValueOfLastname();
    response.role = editor->getValueOfRole();
    return response;
}

std::string AuthService::hashPassword(const std::string& password) {
    return bcrypt::generateHash(password);
}

bool AuthService::checkPassword(const std::string& password, const std::string& hash) {
    return bcrypt::validatePassword(password, hash);
}

}
