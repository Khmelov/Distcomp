package by.kapinskiy.Distcomp.services;

import by.kapinskiy.Distcomp.DTOs.Requests.UserRequestDTO;
import by.kapinskiy.Distcomp.DTOs.Responses.UserResponseDTO;
import by.kapinskiy.Distcomp.models.User;
import by.kapinskiy.Distcomp.repositories.UsersRepository;
import by.kapinskiy.Distcomp.utils.exceptions.NotFoundException;
import by.kapinskiy.Distcomp.utils.mappers.UsersMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UsersService {
    private final UsersRepository usersRepository;
    private final UsersMapper usersMapper;

    @Autowired
    public UsersService(UsersRepository usersRepository, UsersMapper usersMapper) {
        this.usersRepository = usersRepository;
        this.usersMapper = usersMapper;
    }


    @Transactional
    public UserResponseDTO save(UserRequestDTO userRequestDTO) {
        User user = usersMapper.toUser(userRequestDTO);
        return usersMapper.toUserResponse(usersRepository.save(user));
    }

    @Transactional
    public void deleteById(long id) {
        if (!usersRepository.existsById(id)) {
            throw new NotFoundException("User not found");
        }
        usersRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<UserResponseDTO> findAll() {
        return usersMapper.toUserResponseList(usersRepository.findAll());
    }

    @Transactional(readOnly = true)
    public UserResponseDTO findById(long id){
        User user = usersRepository.findById(id).orElseThrow(() -> new NotFoundException("User not found"));
        return usersMapper.toUserResponse(user);
    }

    @Transactional
    public UserResponseDTO update(long id, UserRequestDTO userRequestDTO) {
        userRequestDTO.setId(id);
        return update(userRequestDTO);
    }

    @Transactional
    public UserResponseDTO update(UserRequestDTO userRequestDTO) {
        User user = usersMapper.toUser(userRequestDTO);
        if (!usersRepository.existsById(user.getId())) {
            throw new NotFoundException("User not found");
        }

        return usersMapper.toUserResponse(usersRepository.save(user));
    }

    @Transactional(readOnly = true)
    public boolean existsByLogin(String login){
        return usersRepository.existsByLogin(login);
    }
}
