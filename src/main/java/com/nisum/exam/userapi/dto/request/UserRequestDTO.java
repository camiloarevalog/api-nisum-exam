package com.nisum.exam.userapi.dto.request;


import com.nisum.exam.userapi.model.Phone;
import com.nisum.exam.userapi.model.User;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;


/**
 * Clase DTO, encargada de tener la
 * información que se va usar en la petición entrante del usuario.
 */
@Data
public class UserRequestDTO {

    private String userId;

    @NotNull
    @NotEmpty
    private String name;

    @NotNull
    @NotEmpty
    @Email
    private String email;

    @NotNull
    @NotEmpty
    private String password;

    @NotNull
    @NotEmpty
    private List<Phone> phones;

    public User toModel() {
        return User.builder()
                .id(userId != null ? userId : null)
                .name(name)
                .email(email)
                .password(password)
                .phones(phones)
                .build();
    }

}
