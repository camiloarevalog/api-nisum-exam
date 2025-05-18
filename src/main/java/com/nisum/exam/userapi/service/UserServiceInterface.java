package com.nisum.exam.userapi.service;


import com.nisum.exam.userapi.dto.response.UserResponseDTO;
import com.nisum.exam.userapi.model.User;

import java.util.List;

public interface UserServiceInterface {

    public List<UserResponseDTO> getUsers();

    public UserResponseDTO saveUser(User user);

    public UserResponseDTO updateUser(User user);
}
