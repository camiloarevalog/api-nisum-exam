# Prueba de ingreso -Nisum

Api RestFull que su proposito es crear usuarios con numeros de telefonos, su desarrollo fue hecho con Spring Boot
ring Boot 2.7.9, Java 17 y Maven, también cuenta con pruebas unitarias, para
y el uso de lombok para evitar codigo repetitivo, se usa una base de datos H2 que es
en memoria para guardar los usuarios.

## Requerimientos (Importante)

- [Java 17](https://www.oracle.com/java/technologies/downloads/#java17)
- [Maven](https://maven.apache.org/download.cgi)
- [Spring Boot 2.7.9](https://spring.io/projects/spring-boot)

## Despliegue

1. Clonamos el repositorio:

   `https://github.com/camiloarevalog/api-nisum-exam`

2. Importar el proyecto en el IDE desde el archivo

   `/api-nisum-exam/pom.xml`

3. Compilar y empaquetar el proyecto con Maven, o desde el IDE:

   `1. Maven: cd repositorio mvn clean package`

   `2. IDE: Menú Maven -> userEntity-Api -> lifecycle / clean -> package -> install`

## Ejecutar ApiRest

1. Iniciar la aplicación con Maven, o desde el IDE:

   `1. Maven: mvn spring-boot:run`

   `2. IDE: Menú Run -> Run -> UserApiApplication`

2. Una vez que la aplicación se ha iniciado correctamente, podemos probar la API, utilizando alguna herramienta como
   Postman o preferencia. Por ejemplo, podemos enviar una solicitud POST al endpoint con el cuerpo JSON que contenga los datos
   del usuario que queremos crear. La respuesta debe incluir un código de estado HTTP 201 y los datos del usuario recién
   creado. (En la raiz del proyecto se deja un archivo .json para importar en postman)

3. También podemos probar las otras operaciones de la API (GET, POST) utilizando las URL correspondientes y los
   parámetros necesarios.

### URL's

Si desea acceder a la documentación de la API se puede acceder a la URL de la interfaz de Swagger
y asi poder probar los endpoints, igualmente si queremos ver la base de datos debemos ingresar en la consola de H2


* Endpoint: [http://localhost:8080/nisum/api/users](http://localhost:8080/nisum/api/users)
* Swagger: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
* H2 console: [http://localhost:8080/h2-console](http://localhost:8080/h2-console)

#### Credenciales H2 console

* usuario: nisumExam
* contraseña: nisumExam

### Validación de datos

La API cuenta con una serie de validaciones al momento de hacer una petición para guardar el usuario

1. Sí el email ya está registrado en la base de datos

`409 CONFLICT`

2. Sí el email tiene un formato incorrecto

`400 BAD REQUEST`

3. Sí la contraseña tiene un formato incorrecto

`400 BAD REQUEST`

## Endpoints

### Listar Usuarios

Obtiene la lista de usuarios y devuelve el estado `HTTP 200`, junto con los datos de los usuarios registrados.

**Endpoint**: `/nisum/api/users`

**Método HTTP**: `GET`

**Headers**:

- `Content-Type`: `application/json`

**Cuerpo de la respuesta**:

```json
[
  {
    "id": "string",
    "name": "string",
    "email": "string",
    "password": "string",
    "phones": [
      {
        "number": "string",
        "citycode": "string",
        "countrycode": "string"
      }
    ],
    "created": "date",
    "modified": "date",
    "lastLogin": "date",
    "token": "string",
    "isActive": "boolean"
  }
]
```

### Crear Usuario

Crea un nuevo usuario y devuelve el estado `HTTP 201`, los datos del usuario recién creado, y campos adicionales.

**Endpoint**: `/nisum/api/users`

**Método HTTP**: `POST`

**Headers**:

- `Content-Type`: `application/json`

**Cuerpo de la petición**:

```json
{
  "name": "string",
  "email": "string",
  "password": "string",
  "phones": [
    {
      "number": "string",
      "citycode": "string",
      "countrycode": "string"
    }
  ]
}
```

**Cuerpo de la respuesta**:

```json
[
  {
    "id": "string",
    "name": "string",
    "email": "string",
    "password": "string",
    "phones": [
      {
        "number": "string",
        "citycode": "string",
        "countrycode": "string"
      }
    ],
    "created": "date",
    "modified": "date",
    "lastLogin": "date",
    "token": "string",
    "isActive": "boolean"
  }
]
```
## ✅ Buenas Prácticas y Patrones de Diseño Implementados
Este proyecto ha sido desarrollado aplicando buenas prácticas de desarrollo de software y utilizando patrones de diseño reconocidos para garantizar un código limpio, mantenible, escalable y seguro. A continuación se detallan los principales aspectos implementados:

## Buenas Prácticas
- Separación de responsabilidades siguiendo la arquitectura MVC (Controller, Service, Repository).

- Uso de DTOs (UserRequestDTO, UserResponseDTO) para desacoplar la capa de presentación de la lógica de negocio y del modelo de persistencia.

- Validaciones robustas a nivel de DTO mediante Hibernate Validator, garantizando integridad de datos desde la entrada.

- Manejo global de excepciones mediante @ControllerAdvice, centralizando la gestión de errores y devolviendo respuestas claras y uniformes.

- Documentación automática de la API REST con Swagger (springdoc-openapi), facilitando el consumo por otros equipos o sistemas.

- Seguridad básica con JWT (Json Web Token) para autenticación y autorización, y encriptación de contraseñas.

- Pruebas unitarias para asegurar el correcto funcionamiento de la lógica de negocio y la API.

## Patrones de Diseño

- DTO (Data Transfer Object): encapsula datos entre capas, evitando exponer directamente las entidades del dominio.

- Builder: permite construir objetos complejos de manera fluida y legible, implementado con @Builder de Lombok en entidades como User.

- Strategy: se define una interfaz UserServiceInterface y se utiliza una implementación concreta (UserServiceImpl), permitiendo flexibilidad y testabilidad.

- Repository Pattern: se encapsula el acceso a datos con interfaces JPA como UserRepository, desacoplando la lógica de persistencia de la lógica de negocio.

## Diagrama de la solución

En el siguiente diagrama se muestra la estructura del proyecto y la arquitectura de la API:
![DiagramaSolucion](https://github.com/user-attachments/assets/44879e4b-e356-4508-9437-744640d5b754)
![ArbolProyecto](https://github.com/user-attachments/assets/7c822238-4493-446e-8a0f-ea679650e9c6)

