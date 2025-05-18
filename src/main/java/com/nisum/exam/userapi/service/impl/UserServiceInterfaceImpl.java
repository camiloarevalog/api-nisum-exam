package com.nisum.exam.userapi.service.impl;


import com.nisum.exam.userapi.config.EmailProperties;
import com.nisum.exam.userapi.config.PasswordProperties;
import com.nisum.exam.userapi.dto.response.PhoneResponseDTO;
import com.nisum.exam.userapi.dto.response.UserResponseDTO;
import com.nisum.exam.userapi.entity.PhoneEntity;
import com.nisum.exam.userapi.entity.UserEntity;
import com.nisum.exam.userapi.exception.EmailAlreadyExistsException;
import com.nisum.exam.userapi.exception.InvalidEmailException;
import com.nisum.exam.userapi.exception.InvalidPasswordException;
import com.nisum.exam.userapi.exception.UserNotFoundException;
import com.nisum.exam.userapi.mapper.UserMapper;
import com.nisum.exam.userapi.model.User;
import com.nisum.exam.userapi.repository.PhoneRepository;
import com.nisum.exam.userapi.repository.UserRepository;
import com.nisum.exam.userapi.service.UserServiceInterface;
import com.nisum.exam.userapi.util.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;



/**
 * Servicio encargado de la lógica de negocio respecto a los usuarios.
 */
@Service
@Transactional
public class UserServiceInterfaceImpl implements UserServiceInterface {

    private final UserRepository userRepository;

    private final PhoneRepository phoneRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtUtil jwtUtil;

    private final EmailProperties emailProperties;

    private final PasswordProperties passwordProperties;

    public UserServiceInterfaceImpl(UserRepository userRepository, PhoneRepository phoneRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil, EmailProperties emailProperties, PasswordProperties passwordProperties) {
        this.userRepository = userRepository;
        this.phoneRepository = phoneRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.emailProperties = emailProperties;
        this.passwordProperties = passwordProperties;
    }

    @Override
    /**
     * Se obtiene la lista de todos los usuarios.
     *
     * @return Lista de usuarios.
     */
    public List<UserResponseDTO> getUsers() {
        List<UserEntity> userEntityList = userRepository.findAll();

        return userEntityList.stream()
                .map(UserMapper::toUserModel)
                .map(this::setResponse)
                .collect(Collectors.toList());
    }

    @Override

    /**
     * Guarda un nuevo usuario con una lista de telefonos.
     *
     * @param user Usuario a guardar.
     * @return UserResponseDTO DTO de respuesta con información del usuario guardado.
     */
    public UserResponseDTO saveUser(User user) {
        //Se valida que el email y contraseña cumplan con los formatos establecidos
        validateFormatEmail(user.getEmail());
        validatePassword(user.getPassword());

        // Guarda en User los campos que faltan por guardan
        user.setId(UUID.randomUUID().toString());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setCreated(LocalDate.now());
        user.setLastLogin(user.getLastLogin() != null ? user.getLastLogin() : user.getCreated());
        user.setToken(jwtUtil.generateToken(user.getEmail()));
        user.setIsActive(true);

        //Convertir el User(Modelo de usuario) a UserEntity para guardarlo en base de datos
        UserEntity userEntity = UserMapper.toUserEntity(user);
        userEntity.setPhones(user.getPhones().stream().map(phone -> {
                    PhoneEntity phoneEntity = UserMapper.toPhoneEntity(phone);
                    phoneEntity.setUser(userEntity);
                    return phoneEntity;
                }
        ).collect(Collectors.toList()));

        //Llama el metodo para guardar el UserEntity y devolver un userEntity
        UserEntity createdUser = userRepository.save(userEntity);

        //Convertir el UserEntity en User(Modelo de usuario)
        User createdUserResponse = UserMapper.toUserModel(createdUser);

        //Retorna una respuesta de User luego de invocar el metodo SetResponse
        return setResponse(createdUserResponse);
    }

    @Override
    /**
     * Actualiza los datos de un usuario existente.
     *
     * @param user Usuario a actualizar.
     * @return UserResponseDTO DTO de respuesta con información del usuario actualizado.
     */
    public UserResponseDTO updateUser(User user) {

        //Se busca el usuario por email a ver si existe
        Optional<UserEntity> searchUser = userRepository.findByEmail(user.getEmail());

        if (searchUser.isEmpty()) {
            throw new UserNotFoundException(user.getEmail());
        }

        // Actualiza los datos del usuario
        user.setId(searchUser.get().getId());
        user.setName(user.getName());
        user.setEmail(user.getEmail());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setPhones(user.getPhones());
        user.setCreated(searchUser.get().getCreated());
        user.setModified(LocalDate.now());
        user.setLastLogin(searchUser.get().getLastLogin());
        user.setToken(searchUser.get().getToken());

        UserEntity userEntity = UserMapper.toUserEntity(user);
        userEntity.setPhones(user.getPhones().stream().map(p -> {
            PhoneEntity phoneEntity = UserMapper.toPhoneEntity(p);
            phoneEntity.setUser(userEntity);
            return phoneEntity;
        }).collect(Collectors.toList()));

        // Guarda el usuario en la base de datos
        UserEntity modifiedUser = userRepository.save(userEntity);

        // Convierte el UserEntity a un User para poder retornarlo en el response
        User updatedUser = UserMapper.toUserModel(modifiedUser);

        // Arma y devuelve la respuesta
        return setResponse(updatedUser);


    }

    /**
     * Valida que el email tenga el formato correcto y no esté registrado previamente.
     *
     * @param email correo electrónico a validar
     * @throws ResponseStatusException si el email tiene un formato incorrecto o ya está registrado
     */
    private void validateFormatEmail(String email) {
        Pattern pattern = Pattern.compile(emailProperties.getRegex());
        Matcher matcher = pattern.matcher(email);
        if (!matcher.matches()) {
            throw new InvalidEmailException();
                    }
        if (userRepository.findByEmail(email).isPresent()) {
            throw new EmailAlreadyExistsException(email);
        }
    }

    /**
     * Valida que la contraseña tenga el formato correcto.
     *
     * @param password contraseña a validar
     * @throws ResponseStatusException si la contraseña tiene un formato incorrecto
     */
    private void validatePassword(String password) {
        Pattern pattern = Pattern.compile(passwordProperties.getRegex());
        Matcher matcher = pattern.matcher(password);
        if (!matcher.matches()) {
            throw new InvalidPasswordException();
        }
    }

    /**
     * Crea una respuesta de usuario a partir del modelo de usuario.
     *
     * @param user modelo de usuario
     * @return UserResponseDTO DTO de respuesta de usuario
     */
    private UserResponseDTO setResponse(User user) {
        List<PhoneResponseDTO> phones = new ArrayList<>();
        if (user.getPhones() != null) {
            user.getPhones().forEach(p -> {
                phones.add(PhoneResponseDTO.builder()
                        .number(p.getNumber())
                        .citycode(p.getCitycode())
                        .countrycode(p.getCountrycode())
                        .build());
            });
        }

        return UserResponseDTO.builder()
                .id(UUID.fromString(user.getId()))
                .name(user.getName())
                .email(user.getEmail())
                .password(user.getPassword())
                .phones(phones)
                .created(user.getCreated())
                .modified(user.getModified())
                .lastLogin(user.getLastLogin() != null ? user.getLastLogin() : user.getCreated())
                .token(user.getToken())
                .isActive(user.getIsActive() != null && user.getIsActive())
                .build();
    }

}
