package by.bsuir.task361.publisher.service;

import by.bsuir.task361.publisher.dto.request.UserRequestTo;
import by.bsuir.task361.publisher.dto.response.UserResponseTo;
import by.bsuir.task361.publisher.entity.User;
import by.bsuir.task361.publisher.entity.UserRole;
import by.bsuir.task361.publisher.exception.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SecuredUserService {
    private final UserService userService;
    private final CurrentUserService currentUserService;

    public SecuredUserService(UserService userService, CurrentUserService currentUserService) {
        this.userService = userService;
        this.currentUserService = currentUserService;
    }

    public UserResponseTo register(UserRequestTo request) {
        UserRole role = request.role() == null ? UserRole.CUSTOMER : request.role();
        return userService.create(new UserRequestTo(
                request.id(),
                request.login(),
                request.password(),
                request.firstname(),
                request.lastname(),
                role
        ));
    }

    public List<UserResponseTo> findAll() {
        return userService.findAll();
    }

    public UserResponseTo findById(Long id) {
        return userService.findById(id);
    }

    public UserResponseTo update(UserRequestTo request) {
        if (request.id() == null || request.id() <= 0) {
            return userService.update(request);
        }
        User target = userService.getUser(request.id());
        if (currentUserService.isAdmin()) {
            return userService.update(request);
        }
        User currentUser = currentUserService.getCurrentUser();
        if (!currentUser.getId().equals(target.getId())) {
            throw new ApiException(HttpStatus.FORBIDDEN, 40301, "Access denied");
        }
        return userService.update(new UserRequestTo(
                request.id(),
                request.login(),
                request.password(),
                request.firstname(),
                request.lastname(),
                currentUser.getRole()
        ));
    }

    public void delete(Long id) {
        if (id == null || id <= 0) {
            userService.delete(id);
            return;
        }
        User target = userService.getUser(id);
        if (!currentUserService.isAdmin() && !currentUserService.getCurrentLogin().equals(target.getLogin())) {
            throw new ApiException(HttpStatus.FORBIDDEN, 40301, "Access denied");
        }
        userService.delete(id);
    }
}
