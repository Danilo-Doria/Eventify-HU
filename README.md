# Eventify-HU

Eventify es un proyecto desarrollado con el propósito de fortalecer los conocimientos de programación utilizando **Java**, aplicando progresivamente conceptos de **Programación Orientada a Objetos**, desarrollo de **APIs REST**, arquitectura por capas, persistencia de datos y pruebas automatizadas.

El proyecto está construido de forma incremental, incorporando nuevas funcionalidades y tecnologías a medida que avanza su desarrollo.

## 📋 Requisitos

Antes de ejecutar el proyecto, asegúrate de tener instalado:

* Java JDK 21 o superior
* Apache Maven 3.9 o superior
* Git (opcional)

Puedes verificar las versiones instaladas con:

```bash
java -version
mvn -version
git --version
```

## 🚀 Instalación

1. Clona el repositorio:

```bash
git clone https://github.com/Danilo-Doria/Eventify-HU.git
```

2. Entra al directorio del proyecto:

```bash
cd Eventify-HU
```

3. Compila el proyecto:

```bash
mvn clean install
```

## ▶️ Ejecución

Para ejecutar la aplicación Spring Boot:

```bash
mvn spring-boot:run
```

También puedes ejecutar la clase principal directamente desde un IDE como:

* IntelliJ IDEA
* NetBeans
* Eclipse

## 🏗️ Arquitectura

Eventify utiliza una arquitectura por capas para separar responsabilidades y facilitar el mantenimiento y evolución del proyecto.

```text
Controller
    ↓
Service
    ↓
Repository
    ↓
Database
```

### Controller

Se encarga de recibir las solicitudes HTTP y devolver las respuestas correspondientes.

### Service

Contiene la lógica de negocio de la aplicación y realiza las validaciones necesarias antes de interactuar con los repositorios.

### Repository

Utiliza **Spring Data JPA** para realizar las operaciones de persistencia sobre la base de datos.

### Database

Actualmente el proyecto utiliza **H2** como base de datos, configurada para almacenar los datos de forma persistente.

## 📂 Estructura del proyecto

```text
Eventify-HU/
├── data/
│   └── eventify.mv.db
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── ...
│   │   └── resources/
│   │       └── application.properties
│   │
│   └── test/
│       └── java/
│           └── com/
│               └── ...
│
├── pom.xml
├── mvnw
├── mvnw.cmd
├── LICENSE
└── README.md
```

La estructura puede evolucionar a medida que se incorporen nuevas funcionalidades al proyecto.

## ⚙️ Funcionalidades

Actualmente Eventify cuenta con las siguientes funcionalidades:

### 📅 Gestión de eventos

* Crear eventos.
* Consultar todos los eventos.
* Consultar un evento por ID.
* Actualizar eventos.
* Eliminar eventos.
* Buscar eventos por nombre.
* Validar información obligatoria.
* Paginar resultados.
* Ordenar resultados.

### 📍 Gestión de lugares

* Crear lugares.
* Consultar todos los lugares.
* Consultar un lugar por ID.
* Actualizar lugares.
* Eliminar lugares.
* Validar información obligatoria.
* Validar la capacidad del lugar.
* Paginar resultados.
* Ordenar resultados.

## 💾 Persistencia

Eventify utiliza **Spring Data JPA** y **Hibernate** para gestionar la persistencia de los datos.

Los principales componentes utilizados son:

* `@Entity`
* `@Table`
* `@Id`
* `@GeneratedValue`
* `@Column`
* `JpaRepository`

Los datos se almacenan en una base de datos H2 configurada de forma persistente.

Esto permite que la información permanezca almacenada incluso después de detener y volver a iniciar la aplicación.

## 🔎 Paginación y ordenamiento

Los endpoints de consulta permiten utilizar paginación y ordenamiento mediante `Pageable`.

Ejemplo:

```http
GET /api/events?page=0&size=5&sort=nombre,asc
```

Parámetros disponibles:

| Parámetro | Descripción                       | Ejemplo      |
| --------- | --------------------------------- | ------------ |
| `page`    | Número de página                  | `0`          |
| `size`    | Cantidad de elementos por página  | `5`          |
| `sort`    | Campo y dirección de ordenamiento | `nombre,asc` |

La respuesta incluye información adicional de paginación, como:

* Contenido de la página.
* Número de página actual.
* Tamaño de página.
* Total de elementos.
* Total de páginas.

## 🛡️ Manejo de excepciones

El proyecto utiliza un manejador global mediante `@RestControllerAdvice` para centralizar el tratamiento de errores.

Actualmente se manejan, entre otras:

* `ResourceNotFoundException`
* `IllegalArgumentException`

Por ejemplo, cuando se consulta un recurso inexistente:

```http
GET /api/events/9999
```

La API devuelve:

```http
404 Not Found
```

con información sobre el error.

Las excepciones relacionadas con datos inválidos generan una respuesta:

```http
400 Bad Request
```

Esto permite mantener respuestas HTTP consistentes en toda la aplicación.

## 📖 Documentación de la API

Eventify utiliza **Swagger / OpenAPI** para documentar y probar los endpoints de la API.

Una vez iniciada la aplicación, la documentación puede consultarse desde:

```text
http://localhost:8080/swagger-ui/index.html
```

Desde Swagger es posible visualizar y probar los diferentes endpoints disponibles.

## 🌐 Endpoints principales

### Events

| Método   | Endpoint             | Descripción               |
| -------- | -------------------- | ------------------------- |
| `POST`   | `/api/events`        | Crear un evento           |
| `GET`    | `/api/events`        | Obtener eventos paginados |
| `GET`    | `/api/events/search` | Buscar eventos por nombre |
| `GET`    | `/api/events/{id}`   | Obtener un evento         |
| `PUT`    | `/api/events/{id}`   | Actualizar un evento      |
| `DELETE` | `/api/events/{id}`   | Eliminar un evento        |

### Venues

| Método   | Endpoint           | Descripción               |
| -------- | ------------------ | ------------------------- |
| `POST`   | `/api/venues`      | Crear un lugar            |
| `GET`    | `/api/venues`      | Obtener lugares paginados |
| `GET`    | `/api/venues/{id}` | Obtener un lugar          |
| `PUT`    | `/api/venues/{id}` | Actualizar un lugar       |
| `DELETE` | `/api/venues/{id}` | Eliminar un lugar         |

## 🖥️ Panel administrativo

Eventify cuenta con una interfaz web administrativa desarrollada con **Spring MVC y Thymeleaf**, que permite gestionar visualmente los eventos y lugares registrados en el sistema.

El panel utiliza controladores MVC independientes de los controladores REST de la API.

### Funcionalidades

* Visualizar eventos registrados.
* Visualizar lugares registrados.
* Mostrar mensajes informativos cuando no existen registros.
* Acceder al formulario de creación de eventos.
* Acceder al formulario de creación de lugares.
* Registrar nuevos eventos desde la interfaz web.
* Registrar nuevos lugares desde la interfaz web.
* Redirigir al listado correspondiente después de crear un recurso.

Las rutas administrativas utilizan el prefijo `/admin/**`, mientras que la API REST continúa utilizando `/api/**`.

### Rutas principales

| Método | Endpoint | Descripción |
| ------ | -------- | ----------- |
| `GET` | `/admin/events` | Listar eventos |
| `GET` | `/admin/events/new` | Mostrar formulario de nuevo evento |
| `POST` | `/admin/events` | Crear evento |
| `GET` | `/admin/venues` | Listar lugares |
| `GET` | `/admin/venues/new` | Mostrar formulario de nuevo lugar |
| `POST` | `/admin/venues` | Crear lugar |

## 🎨 Vistas con Thymeleaf

Las vistas del panel administrativo se encuentran en:

```text
src/main/resources/templates/
``

## 🧪 Pruebas

El proyecto utiliza **JUnit** y las herramientas de testing de Spring Boot para verificar el correcto funcionamiento de sus componentes.

Entre las pruebas implementadas se encuentran:

* Creación de eventos y lugares.
* Consulta por ID.
* Validación de recursos inexistentes.
* Actualización.
* Eliminación.
* Búsqueda por nombre.
* Paginación.
* Persistencia mediante JPA.
* Pruebas de repositorios con `@DataJpaTest`.

Para ejecutar todas las pruebas:

```bash
mvn test
```

## 🛠️ Tecnologías utilizadas

* **Java 21**
* **Spring Boot**
* **Spring Web**
* **Spring Data JPA**
* **Hibernate**
* **H2 Database**
* **Maven**
* **JUnit**
* **Mockito**
* **Spring Boot Test**
* **Spring MVC**
* **Thymeleaf**
* **Springdoc OpenAPI / Swagger**

## 📦 Compilación

Para generar el archivo JAR:

```bash
mvn package
```

El archivo generado estará disponible en:

```text
target/
```

También puedes limpiar y volver a compilar todo el proyecto con:

```bash
mvn clean install
```

## 🔄 Evolución del proyecto

Eventify es un proyecto desarrollado de manera incremental. Cada etapa incorpora nuevos conocimientos y funcionalidades sobre la base construida anteriormente.

Por esta razón, este README documenta el **estado actual del proyecto** y puede actualizarse conforme se incorporen nuevas funcionalidades, tecnologías y mejoras.

## 👨‍💻 Autor

* GitHub: **[Danilo-Doria](https://github.com/Danilo-Doria)**
* LinkedIn: **[Danilo Doria Diaz](https://www.linkedin.com/in/danilodd)**
* Email: **[danilodoria519@gmail.com](mailto:danilodoria519@gmail.com)**

---

## 📄 License

This project is licensed under the MIT License.

See the [`LICENSE`](LICENSE) file for more information.
