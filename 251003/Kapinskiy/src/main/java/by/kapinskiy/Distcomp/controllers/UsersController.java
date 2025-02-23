package by.kapinskiy.Distcomp.controllers;

import by.kapinskiy.Distcomp.DTOs.Requests.UserRequestDTO;
import by.kapinskiy.Distcomp.DTOs.Responses.UserResponseDTO;
import by.kapinskiy.Distcomp.models.User;
import by.kapinskiy.Distcomp.services.UsersService;
import by.kapinskiy.Distcomp.utils.UserValidator;
import by.kapinskiy.Distcomp.utils.exceptions.ValidationException;
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
    private final UserValidator userValidator;
    @Autowired
    public UsersController(UsersService usersService, UserValidator validator) {
        this.usersService = usersService;
        this.userValidator = validator;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseDTO createUser(@RequestBody @Valid UserRequestDTO userRequestDTO, BindingResult bindingResult) throws MethodArgumentNotValidException {
        validate(userRequestDTO, bindingResult);
        return usersService.save(userRequestDTO);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable long id){
        usersService.deleteById(id);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<UserResponseDTO> getAllUsers(){
        return usersService.findAll();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserResponseDTO getUser(@PathVariable long id) {
        return usersService.findById(id);
    }

    // Because of test limitations we don't require id in the path, and also we send user in the response,
    // but we shouldn't do that
    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public UserResponseDTO updateUser(@RequestBody @Valid UserRequestDTO userRequestDTO, BindingResult bindingResult){
        validate(userRequestDTO, bindingResult);
        return usersService.update(userRequestDTO);
    }

    // Rest version
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Long updateUser(@PathVariable Long id, @RequestBody @Valid UserRequestDTO userRequestDTO, BindingResult bindingResult){
        validate(userRequestDTO, bindingResult);
        return usersService.update(id, userRequestDTO).getId();
    }

    private void validate(UserRequestDTO userRequestDTO, BindingResult bindingResult){
        userValidator.validate(userRequestDTO, bindingResult);
        if (bindingResult.hasFieldErrors()) {
            throw new ValidationException(bindingResult);
        }
    }
}
