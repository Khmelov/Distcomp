package by.kapinskiy.Task310.controllers;

import by.kapinskiy.Task310.DTOs.Requests.UserRequestDTO;
import by.kapinskiy.Task310.DTOs.Responses.UserResponseDTO;
import by.kapinskiy.Task310.models.User;
import by.kapinskiy.Task310.services.UsersService;
import by.kapinskiy.Task310.utils.Mapper;
import by.kapinskiy.Task310.utils.UserValidator;
import by.kapinskiy.Task310.utils.exceptions.ValidationException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class    UsersController {
    private final UsersService usersService;
    private final Mapper mapper;

    private final UserValidator userValidator;
    @Autowired
    public UsersController(UsersService usersService, Mapper mapper, UserValidator validator) {
        this.usersService = usersService;
        this.mapper = mapper;
        this.userValidator = validator;
    }

    @PostMapping
    public ResponseEntity<UserResponseDTO> createUser(@RequestBody @Valid UserRequestDTO userRequestDTO, BindingResult bindingResult) throws MethodArgumentNotValidException {
        validate(userRequestDTO, bindingResult);
        User user = mapper.userRequestToUser(userRequestDTO);
        return new ResponseEntity<>(mapper.userToUserResponse(usersService.save(user)), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable long id){
        usersService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAllUsers(){
        List<User> users = usersService.findAll();
        return new ResponseEntity<>(mapper.usersToUserResponses(users), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUser(@PathVariable long id) {
        User user = usersService.findById(id);
        return new ResponseEntity<>(mapper.userToUserResponse(user), HttpStatus.OK);
    }

    // Because of test limitations we don't require id in the path, and also we send user in the response,
    // but we shouldn't do that
    @PutMapping
    public ResponseEntity<UserResponseDTO> updateUser(@RequestBody @Valid UserRequestDTO userRequestDTO, BindingResult bindingResult){
        validate(userRequestDTO, bindingResult);
        User updatedUser = usersService.update(mapper.userRequestToUser(userRequestDTO));
        return new ResponseEntity<>(mapper.userToUserResponse(updatedUser), HttpStatus.OK);
    }

    // Rest version
    @PutMapping("/{id}")
    public ResponseEntity<Long> updateUser(@PathVariable Long id, @RequestBody @Valid UserRequestDTO userRequestDTO, BindingResult bindingResult){
        validate(userRequestDTO, bindingResult);
        User updatedUser = usersService.update(id, mapper.userRequestToUser(userRequestDTO));
        return new ResponseEntity<>(updatedUser.getId(), HttpStatus.OK);
    }

    private void validate(UserRequestDTO userRequestDTO, BindingResult bindingResult){
        userValidator.validate(userRequestDTO, bindingResult);
        if (bindingResult.hasFieldErrors()) {
            throw new ValidationException(bindingResult);
        }
    }
}
