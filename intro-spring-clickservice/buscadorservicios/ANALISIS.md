# Análisis del programa ClickService

## Arquitectura general
ClickService es una aplicación Spring Boot que arranca desde `ClickServiceApplication` y combina una API REST con vistas Thymeleaf para cubrir distintos flujos de uso.【F:src/main/java/ar/edu/huergo/clickservice/buscadorservicios/ClickServiceApplication.java†L1-L14】 El proyecto sigue una arquitectura en capas clásica (controladores, servicios, repositorios y entidades) y utiliza Spring Data JPA para persistir la información en una base H2 en memoria durante el arranque de la aplicación.【F:build.gradle†L20-L41】【F:src/main/resources/application.properties†L1-L26】

## Modelo de datos y entidades
- **Servicio**: representa los oficios ofrecidos (plomería, electricidad, etc.), con validaciones de nombre único y precio positivo.【F:src/main/java/ar/edu/huergo/clickservice/buscadorservicios/entity/Servicio.java†L1-L49】
- **Profesional**: encapsula datos del proveedor, incluyendo relación uno a uno con `Usuario`, servicios asociados, métricas y disponibilidad.【F:src/main/java/ar/edu/huergo/clickservice/buscadorservicios/entity/Profesional.java†L1-L96】
- **SolicitudServicio**: registra pedidos de clientes con referencias al servicio y usuario, presupuesto, fecha y estado.【F:src/main/java/ar/edu/huergo/clickservice/buscadorservicios/entity/SolicitudServicio.java†L1-L118】
- **Usuario** y **Rol**: conforman el modelo de seguridad con roles asignados a través de una relación muchos-a-muchos.【F:src/main/java/ar/edu/huergo/clickservice/buscadorservicios/entity/security/Usuario.java†L1-L45】【F:src/main/java/ar/edu/huergo/clickservice/buscadorservicios/entity/security/Rol.java†L1-L27】

## Capa de acceso a datos
Los repositorios extienden `JpaRepository`, lo que habilita consultas estándar y métodos derivados para filtrar por disponibilidad, usuario o estado.【F:src/main/java/ar/edu/huergo/clickservice/buscadorservicios/repository/ProfesionalRepository.java†L1-L20】【F:src/main/java/ar/edu/huergo/clickservice/buscadorservicios/repository/SolicitudServicioRepository.java†L1-L19】 Esta capa se complementa con repositorios específicos para seguridad (`UsuarioRepository`, `RolRepository`).【F:src/main/java/ar/edu/huergo/clickservice/buscadorservicios/repository/security/UsuarioRepository.java†L1-L11】【F:src/main/java/ar/edu/huergo/clickservice/buscadorservicios/repository/security/RolRepository.java†L1-L9】

## Servicios de negocio
Los servicios encapsulan la lógica principal: gestión CRUD de servicios, profesionales y solicitudes, asignación de servicios y cambios de estado, todo apoyado en validaciones JPA y excepciones `EntityNotFoundException` cuando corresponde.【F:src/main/java/ar/edu/huergo/clickservice/buscadorservicios/service/ServicioService.java†L1-L38】【F:src/main/java/ar/edu/huergo/clickservice/buscadorservicios/service/ProfesionalService.java†L1-L71】【F:src/main/java/ar/edu/huergo/clickservice/buscadorservicios/service/SolicitudServicioService.java†L1-L66】 Para seguridad, `UsuarioService` se encarga del registro de clientes y profesionales, encriptando contraseñas, validando reglas y orquestando la creación del `Profesional` asociado cuando aplica.【F:src/main/java/ar/edu/huergo/clickservice/buscadorservicios/service/security/UsuarioService.java†L1-L103】

## Exposición de funcionalidades
- **API REST**: los controladores REST publican operaciones CRUD y consultas específicas para servicios, profesionales y solicitudes, devolviendo DTOs mapeados mediante componentes dedicados.【F:src/main/java/ar/edu/huergo/clickservice/buscadorservicios/controller/ServicioController.java†L1-L69】【F:src/main/java/ar/edu/huergo/clickservice/buscadorservicios/controller/ProfesionalController.java†L1-L87】【F:src/main/java/ar/edu/huergo/clickservice/buscadorservicios/controller/SolicitudServicioController.java†L1-L89】
- **Autenticación**: `AuthController` expone `/api/auth/login` para emitir tokens JWT, mientras que `UsuarioController` gestiona los registros y la consulta de usuarios.【F:src/main/java/ar/edu/huergo/clickservice/buscadorservicios/controller/security/AuthController.java†L1-L43】【F:src/main/java/ar/edu/huergo/clickservice/buscadorservicios/controller/security/UsuarioController.java†L1-L44】
- **Vistas Thymeleaf**: `BuscadorServiciosWebController` sirve páginas web para listado de servicios, registro/login y dashboard, reutilizando los servicios de dominio para poblar los modelos.【F:src/main/java/ar/edu/huergo/clickservice/buscadorservicios/controller/web/BuscadorServiciosWebController.java†L1-L142】

Los DTOs y mappers centralizan la transformación entre entidades y representaciones expuestas, evitando fugas de detalles internos hacia la API.【F:src/main/java/ar/edu/huergo/clickservice/buscadorservicios/dto/ProfesionalDTO.java†L1-L58】【F:src/main/java/ar/edu/huergo/clickservice/buscadorservicios/mapper/ProfesionalMapper.java†L1-L98】

## Seguridad y autenticación
La configuración divide el tráfico en dos filtros: rutas web con sesión y formulario y rutas API con JWT. `SecurityConfig` define permisos granularmente por método HTTP y rol, incorpora un `JwtAuthenticationFilter` que valida tokens y se apoya en `JwtTokenService` para generarlos y verificar su vigencia.【F:src/main/java/ar/edu/huergo/clickservice/buscadorservicios/config/security/SecurityConfig.java†L1-L153】【F:src/main/java/ar/edu/huergo/clickservice/buscadorservicios/config/security/JwtAuthenticationFilter.java†L1-L88】【F:src/main/java/ar/edu/huergo/clickservice/buscadorservicios/service/security/JwtTokenService.java†L1-L85】 Las contraseñas se cifran con `BCryptPasswordEncoder` y los roles determinan el acceso a cada endpoint.【F:src/main/java/ar/edu/huergo/clickservice/buscadorservicios/config/security/SecurityConfig.java†L155-L199】

## Manejo de errores
`GlobalExceptionHandler` traduce excepciones comunes en respuestas con formato RFC 7807, registrando logs con SLF4J y proporcionando detalles de validación cuando corresponde.【F:src/main/java/ar/edu/huergo/clickservice/buscadorservicios/controller/GlobalExceptionHandler.java†L1-L92】

## Inicialización de datos y configuración
`DataInitializer` crea roles predefinidos, usuarios base y servicios de ejemplo al arrancar, asegurando un entorno funcional para pruebas y demostraciones.【F:src/main/java/ar/edu/huergo/clickservice/buscadorservicios/config/DataInitializer.java†L1-L53】 La configuración en `application.properties` activa H2 en memoria, la consola de administración, SQL en logs y define el secreto/expiración JWT.【F:src/main/resources/application.properties†L1-L26】

## Validación y utilidades
Además de las anotaciones de Bean Validation en entidades y DTOs, `PasswordValidator` refuerza la complejidad de contraseñas usadas en los flujos de registro y carga inicial.【F:src/main/java/ar/edu/huergo/clickservice/buscadorservicios/util/PasswordValidator.java†L1-L44】 Los DTOs de seguridad exigen contraseñas fuertes y correos válidos antes de llegar a la capa de servicio.【F:src/main/java/ar/edu/huergo/clickservice/buscadorservicios/dto/security/RegistrarProfesionalDTO.java†L1-L52】【F:src/main/java/ar/edu/huergo/clickservice/buscadorservicios/dto/security/RegistrarDTO.java†L1-L18】

## Capa de presentación
Las plantillas Thymeleaf para login y registro muestran formularios simples y mensajes de error/éxito, enlazados con las rutas definidas por el controlador web.【F:src/main/resources/templates/auth/login.html†L1-L46】【F:src/main/resources/templates/auth/registro.html†L1-L48】 (La carpeta incluye también un marcador vacío `d`, probablemente pendiente de contenido futuro.)

## Dependencias y construcción
El archivo `build.gradle` declara los starters de Spring necesarios (web, data, security, validation, thymeleaf), la integración con JWT y Lombok, además del motor de pruebas configurado para JUnit Platform.【F:build.gradle†L1-L41】 La aplicación apunta a Java 21 y utiliza `create-drop` para reconstruir el esquema en cada arranque, facilitando la iteración durante el desarrollo.【F:build.gradle†L8-L18】【F:src/main/resources/application.properties†L15-L18】

## Oportunidades de mejora
- Completar las vistas faltantes (`servicios/lista`, `dashboard`, etc.) para alinear los controladores web con los recursos disponibles.
- Incorporar pruebas automatizadas para servicios y controladores, aprovechando los repositorios en memoria.
- Considerar la activación de CSRF en entornos productivos y ajustar las políticas JWT (secreto externo, expiración configurable).
