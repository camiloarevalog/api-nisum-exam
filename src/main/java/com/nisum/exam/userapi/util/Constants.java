package com.nisum.exam.userapi.util;

public class Constants {

    //El formato del email debe ser (aaaaaaa@dominio.cl)
    public static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";

    // la contraseña debe tener minimo 8 y maximo 16 caracteres, números,
    // letras minúsculas y mayúsculas (123Acb1234*)
    public static final String PWD_REGEX = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{8,16}$";

    public static final long EXPIRATION_TIME = 10 * 60 * 60 * 1000; // 10 horas

}
