package com.nisum.exam.userapi.exception;

public class InvalidEmailException extends RuntimeException {
    public InvalidEmailException() {
        super("El formato del correo no es el indicado");
    }
}
