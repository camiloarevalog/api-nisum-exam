package com.nisum.exam.userapi.exception;

public class InvalidPasswordException extends RuntimeException {
    public InvalidPasswordException() {
        super("La contraseña tiene un formato incorrecto");
    }
}
