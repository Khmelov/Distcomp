#include "AuthService.h"
#include <exceptions/AuthException.h>
#include <chrono>

namespace auth
{

AuthResponseTo AuthService::login(const LoginRequestTo& req) {
    auto editor = repository_->findByLogin(req.login);
    if (!editor) {
        throw UnauthorizedException("Invalid login or password");
    }
    
    if (!checkPassword(req.password, editor->getValueOfPassword())) {
        throw UnauthorizedException("Invalid login or password");
    }
    
    std::string role = editor->getValueOfRole();
    std::string token = JwtUtils::generateToken(req.login, role);
    
    AuthResponseTo response;
    response.accessToken = token;
    return response;
}

EditorResponseTo AuthService::registerEditor(const RegisterRequestTo& req) {
    if (repository_->existsByLogin(req.login)) {
        throw ValidationException("Login already exists");
    }
    
    if (req.role != "ADMIN" && req.role != "CUSTOMER") {
        throw ValidationException("Invalid role. Must be ADMIN or CUSTOMER");
    }
    
    TblEditor editor;
    editor.setLogin(req.login);
    editor.setPassword(hashPassword(req.password));
    editor.setFirstname(req.firstName);
    editor.setLastname(req.lastName);
    editor.setRole(req.role);
    
    auto result = repository_->Create(editor);

    if (std::holds_alternative<DatabaseError>(result))
    {
        throw;
    }

    auto createdId = std::get<int64_t>(result);
    
    EditorResponseTo response;
    response.id = createdId;
    response.login = editor.getValueOfLogin();
    response.firstName = editor.getValueOfFirstname();
    response.lastName = editor.getValueOfLastname();
    response.role = editor.getValueOfRole();
    return response;
}

std::optional<EditorResponseTo> AuthService::getEditorByLogin(const std::string& login) {
    auto editor = repository_->findByLogin(login);
    if (!editor) {
        return std::nullopt;
    }
    
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
