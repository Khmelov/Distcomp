package by.kapinskiy.Task310.services;

import by.kapinskiy.Task310.models.User;
import by.kapinskiy.Task310.repositories.UsersRepository;
import by.kapinskiy.Task310.utils.exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsersService {
    private final UsersRepository usersRepository;

    @Autowired
    public UsersService(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    public User save(User user) {
        return usersRepository.save(user);
    }

    public void deleteById(long id) {
        if (!usersRepository.existsById(id)) {
            throw new NotFoundException("User not found");
        }
        usersRepository.deleteById(id);
    }

    public List<User> findAll() {
        return usersRepository.findAll();
    }

    public User findById(long id){
        return usersRepository.findById(id).orElseThrow(() -> new NotFoundException("User not found"));
    }

    public User update(long id, User user) {
        user.setId(id);
        return update(user);
    }

    public User update(User user) {
        if (!usersRepository.existsById(user.getId())) {
            throw new NotFoundException("User not found");
        }

        return usersRepository.save(user);
    }

    public boolean existsByLogin(String login){
        return usersRepository.existsByLogin(login);
    }
}
