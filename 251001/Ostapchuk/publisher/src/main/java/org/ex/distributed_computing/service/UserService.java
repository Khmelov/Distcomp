package org.ex.distributed_computing.service;

import java.util.List;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.ex.distributed_computing.dto.request.UserRequestDTO;
import org.ex.distributed_computing.dto.response.UserResponseDTO;
import org.ex.distributed_computing.exception.DuplicateDatabaseValueException;
import org.ex.distributed_computing.exception.NotFoundException;
import org.ex.distributed_computing.mapper.UserMapper;
import org.ex.distributed_computing.model.User;
import org.ex.distributed_computing.repository.UserRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.Cache;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;

  @Qualifier("userCache")
  private final Cache cache;

  public List<UserResponseDTO> getAllUsers() {
    return userMapper.toDtoList(userRepository.findAll());
  }

  public UserResponseDTO getUserById(Long id) {
    var cached = cache.get(id, UserResponseDTO.class);
    if (cached != null) {
      return cached;
    }

    var user = userRepository.findById(id)
            .map(userMapper::toDto)
            .orElseThrow(() -> new NotFoundException("User not found"));

    cache.put(id, user);
    return user;
  }

  public UserResponseDTO createUser(UserRequestDTO requestDTO) {
    User user = userMapper.toEntity(requestDTO);
    if (userRepository.existsByLogin(user.getLogin())) {
      throw new DuplicateDatabaseValueException();
    }
    userRepository.save(user);
    var userDto = userMapper.toDto(user);
    cache.put(user.getId(), userDto);
    return userDto;
  }

  public UserResponseDTO updateUser(UserRequestDTO dto) {
    User user = userRepository.findById(dto.id())
            .orElseThrow(() -> new NotFoundException("User not found"));

    user.setLogin(dto.login());
    user.setPassword(dto.password());
    user.setFirstname(dto.firstname());
    user.setLastname(dto.lastname());

    userRepository.save(user);
    var userDto = userMapper.toDto(user);
    cache.put(user.getId(), userDto);
    return userDto;
  }

  public void deleteUser(Long id) {
    User user = userRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("User not found"));
    userRepository.delete(user);
    cache.evictIfPresent(id);
  }
}

