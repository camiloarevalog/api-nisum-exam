package com.nisum.exam.userapi.controller;



import com.nisum.exam.userapi.dto.request.UserRequestDTO;
import com.nisum.exam.userapi.dto.response.UserResponseDTO;
import com.nisum.exam.userapi.service.UserServiceInterface;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * Clase Controlador, es donde se manejan las solicitudes HTTP relacionadas con usuarios.
 */
@RestController
@RequestMapping(value = "/nisum/api/users", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController {

    private UserServiceInterface userServiceInterface;

    public UserController(UserServiceInterface userServiceInterface) {
        this.userServiceInterface = userServiceInterface;
    }

    /**
     * Solicitud HTTP-GET para obtener todos los usuarios con su lista de telefonos.
     *
     * @return una lista de objetos User en el cuerpo de la respuesta HTTP
     */
    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getUsers() {
        return new ResponseEntity<>(userServiceInterface.getUsers(), HttpStatus.OK);
    }


    /**
     * Solicitud HTTP POST para guardar un nuevo usuario.
     *
     * @param userRequestDTO contiene la información del usuario a guardar
     * @return UserResponseDTO creado en el cuerpo de la respuesta HTTP
     */
    @PostMapping
    public ResponseEntity<UserResponseDTO> saveUser(@Valid @RequestBody UserRequestDTO userRequestDTO) {
        return new ResponseEntity<>(userServiceInterface.saveUser(userRequestDTO.toModel()), HttpStatus.CREATED);
    }

    /**
     * Solicitud HTTP PUT para actualizar un usuario ya guardado previamente.
     *
     * @param userRequestDTO contiene la información del usuario a actualizar
     * @return UserResponseDTO actualizado en el cuerpo de la respuesta HTTP
     */
    @PutMapping
    public ResponseEntity<UserResponseDTO> updateUser(@RequestBody UserRequestDTO userRequestDTO) {
        return new ResponseEntity<>(userServiceInterface.updateUser(userRequestDTO.toModel()), HttpStatus.OK);
    }
}
