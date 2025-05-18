package com.nisum.exam.userapi.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.nisum.exam.userapi.dto.request.UserRequestDTO;
import com.nisum.exam.userapi.dto.response.UserResponseDTO;
import com.nisum.exam.userapi.model.Phone;
import com.nisum.exam.userapi.model.User;
import com.nisum.exam.userapi.service.UserServiceInterface;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
public class UserControllerTest {


    private UserServiceInterface userServiceInterface;
    private UserController userController;

    @BeforeEach
    void setUp() {
        userServiceInterface = mock(UserServiceInterface.class);
        userController = new UserController(userServiceInterface);
    }

    @Test
    void getUsers_ReturnsListOfUsers() {
        UserResponseDTO user1 = new UserResponseDTO();
        UserResponseDTO user2 = new UserResponseDTO();
        List<UserResponseDTO> users = Arrays.asList(user1, user2);

        when(userServiceInterface.getUsers()).thenReturn(users);

        ResponseEntity<List<UserResponseDTO>> response = userController.getUsers();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(2, response.getBody().size());
        verify(userServiceInterface, times(1)).getUsers();
    }

    @Test
    void saveUser_ReturnsCreatedUser() {
        UserRequestDTO requestDTO = mock(UserRequestDTO.class);
        User user = new User();
        UserResponseDTO responseDTO = new UserResponseDTO();

        when(requestDTO.toModel()).thenReturn(user);
        when(userServiceInterface.saveUser(user)).thenReturn(responseDTO);

        ResponseEntity<UserResponseDTO> response = userController.saveUser(requestDTO);

        assertEquals(201, response.getStatusCodeValue());
        assertEquals(responseDTO, response.getBody());
        verify(userServiceInterface, times(1)).saveUser(user);
    }

    @Test
    void updateUser_ReturnsUpdatedUser() {
        UserRequestDTO requestDTO = mock(UserRequestDTO.class);
        User user = new User();
        UserResponseDTO responseDTO = new UserResponseDTO();

        when(requestDTO.toModel()).thenReturn(user);
        when(userServiceInterface.updateUser(user)).thenReturn(responseDTO);

        ResponseEntity<UserResponseDTO> response = userController.updateUser(requestDTO);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(responseDTO, response.getBody());
        verify(userServiceInterface, times(1)).updateUser(user);
    }

}
