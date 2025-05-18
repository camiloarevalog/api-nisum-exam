package com.nisum.exam.userapi.service;


import com.nisum.exam.userapi.config.EmailProperties;
import com.nisum.exam.userapi.config.PasswordProperties;
import com.nisum.exam.userapi.dto.response.UserResponseDTO;
import com.nisum.exam.userapi.entity.UserEntity;
import com.nisum.exam.userapi.exception.EmailAlreadyExistsException;
import com.nisum.exam.userapi.exception.InvalidEmailException;
import com.nisum.exam.userapi.exception.InvalidPasswordException;
import com.nisum.exam.userapi.exception.UserNotFoundException;
import com.nisum.exam.userapi.model.Phone;
import com.nisum.exam.userapi.model.User;
import com.nisum.exam.userapi.repository.PhoneRepository;
import com.nisum.exam.userapi.repository.UserRepository;
import com.nisum.exam.userapi.service.impl.UserServiceInterfaceImpl;
import com.nisum.exam.userapi.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    private UserRepository userRepository;
    private PhoneRepository phoneRepository;
    private PasswordEncoder passwordEncoder;
    private JwtUtil jwtUtil;
    private EmailProperties emailProperties;
    private PasswordProperties passwordProperties;

    private UserServiceInterfaceImpl userService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        phoneRepository = mock(PhoneRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        jwtUtil = mock(JwtUtil.class);
        emailProperties = mock(EmailProperties.class);
        passwordProperties = mock(PasswordProperties.class);

        userService = new UserServiceInterfaceImpl(
                userRepository, phoneRepository, passwordEncoder,
                jwtUtil, emailProperties, passwordProperties
        );
    }

    @Test
    void getUsers_ReturnsUserResponseDTOList() {
        UserEntity entity = new UserEntity();
        entity.setId(UUID.randomUUID().toString());
        entity.setEmail("test@test.com");
        entity.setName("Test");
        entity.setPassword("pass");
        entity.setCreated(LocalDate.now());
        entity.setLastLogin(LocalDate.now());

        when(userRepository.findAll()).thenReturn(List.of(entity));

        List<UserResponseDTO> result = userService.getUsers();

        assertEquals(1, result.size());
        assertEquals("test@test.com", result.get(0).getEmail());
    }

    @Test
    void saveUser_SuccessfullyCreatesUser() {
        User user = new User();
        user.setEmail("test@test.com");
        user.setPassword("Abc123!@");
        user.setName("John");

        Phone phone = new Phone();
        phone.setNumber("123456789");
        phone.setCitycode("1");
        phone.setCountrycode("57");

        user.setPhones(List.of(phone));

        when(emailProperties.getRegex()).thenReturn("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$");
        when(passwordProperties.getRegex()).thenReturn("^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{8,16}$");
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(any())).thenReturn("encodedPass");
        when(jwtUtil.generateToken(any())).thenReturn("jwt-token");
        when(userRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        UserResponseDTO response = userService.saveUser(user);

        assertEquals("test@test.com", response.getEmail());
        assertEquals("jwt-token", response.getToken());
        verify(userRepository).save(any());
    }

    @Test
    void validatePasswordPattern_AllowsSpecialCharsAndValidFormat() {
        String regex = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{8,16}$";
        String password = "Abc123!@";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(password);

        assertTrue(matcher.matches());
    }

    @Test
    void saveUser_ThrowsInvalidEmailException() {
        User user = new User();
        user.setEmail("invalid");
        user.setPassword("Pass123!");

        when(emailProperties.getRegex()).thenReturn("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$");

        assertThrows(InvalidEmailException.class, () -> userService.saveUser(user));
    }

    @Test
    void saveUser_ThrowsEmailAlreadyExistsException() {
        User user = new User();
        user.setEmail("test@test.com");
        user.setPassword("Pass123!");

        when(emailProperties.getRegex()).thenReturn(".*");
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(new UserEntity()));

        assertThrows(EmailAlreadyExistsException.class, () -> userService.saveUser(user));
    }

    @Test
    void saveUser_ThrowsInvalidPasswordException() {
        User user = new User();
        user.setEmail("test@test.com");
        user.setPassword("bad");

        when(emailProperties.getRegex()).thenReturn(".*");
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.empty());
        when(passwordProperties.getRegex()).thenReturn("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{6,}$");

        assertThrows(InvalidPasswordException.class, () -> userService.saveUser(user));
    }

    @Test
    void updateUser_SuccessfullyUpdatesUser() {
        UserEntity existing = new UserEntity();
        existing.setId(UUID.randomUUID().toString());
        existing.setEmail("test@test.com");
        existing.setCreated(LocalDate.now());
        existing.setToken("old-token");

        User user = new User();
        user.setEmail("test@test.com");
        user.setPassword("Pass123!");
        user.setName("Updated Name");

        // ✅ Prevenir NullPointerException
        Phone phone = new Phone();
        phone.setNumber("123456789");
        phone.setCitycode("1");
        phone.setCountrycode("57");
        user.setPhones(List.of(phone));

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(existing));
        when(passwordEncoder.encode(any())).thenReturn("encodedPass");
        when(userRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        UserResponseDTO result = userService.updateUser(user);

        assertEquals("test@test.com", result.getEmail());
        assertEquals("encodedPass", result.getPassword());
    }


    @Test
    void updateUser_ThrowsUserNotFoundException() {
        User user = new User();
        user.setEmail("nonexistent@test.com");

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.updateUser(user));
    }
}
