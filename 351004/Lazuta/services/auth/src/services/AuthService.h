#ifndef AUTHSERVICE_H
#define AUTHSERVICE_H

#include <storage/database/EditorRepository.h>
#include <dto/requests/LoginRequestTo.h>
#include <dto/requests/RegisterRequestTo.h>
#include <dto/responses/AuthResponseTo.h>
#include <dto/responses/EditorResponseTo.h>
#include <utils/JwtUtils.h>
#include <string>
#include <memory>
#include <bcrypt.h>

namespace auth
{
    using namespace auth::dto;

class AuthService {
public:
    AuthService(std::unique_ptr<EditorRepository> repo) : repository_(std::move(repo)) {}
    
    AuthResponseTo login(const LoginRequestTo& req);
    EditorResponseTo registerEditor(const RegisterRequestTo& req);
    std::optional<EditorResponseTo> getEditorByLogin(const std::string& login);
    
private:
    std::unique_ptr<EditorRepository> repository_;
    
    std::string hashPassword(const std::string& password);
    bool checkPassword(const std::string& password, const std::string& hash);
};

}


#endif
