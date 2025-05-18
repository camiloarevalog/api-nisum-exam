package com.nisum.exam.userapi.exception;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(String email) {
        super("No se encontró ningún usuario con el email: " + email);
    }

}
