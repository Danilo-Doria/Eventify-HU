# Eventify-HU

Eventify es un proyecto desarrollado con el propósito de fortalecer los conocimientos de programación utilizando **Java**, aplicando progresivamente conceptos de **Programación Orientada a Objetos**, desarrollo de **APIs REST**, arquitectura por capas, persistencia de datos, relaciones entre entidades, migraciones de base de datos, optimización de consultas y pruebas automatizadas.

El proyecto está construido de forma incremental, incorporando nuevas funcionalidades y tecnologías a medida que avanza su desarrollo.

---

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

---

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

---

## ▶️ Ejecución

Para ejecutar la aplicación Spring Boot:

```bash
mvn spring-boot:run
```

También puedes ejecutar la clase principal directamente desde un IDE como:

* IntelliJ IDEA
* NetBeans
* Eclipse

---

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

El proyecto cuenta con controladores REST para la API y controladores MVC para el panel administrativo.

### Service

Contiene la lógica de negocio de la aplicación y realiza las validaciones necesarias antes de interactuar con los repositorios.

También se encarga de resolver las relaciones entre entidades antes de persistir los datos.

### Repository

Utiliza **Spring Data JPA** para realizar las operaciones de persistencia sobre la base de datos.

Además, contiene consultas derivadas y consultas JPQL optimizadas para búsquedas, filtros, proyecciones y paginación.

### Database

Actualmente el proyecto utiliza **H2** como base de datos.

La estructura de la base de datos es administrada mediante **Flyway**, evitando que Hibernate cree o modifique automáticamente las tablas.

---

## 📂 Estructura del proyecto

```text
Eventify-HU/
├── data/
│   └── eventify.mv.db
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── eventify/
│   │   │           └── semana_4/
│   │   │               ├── controller/
│   │   │               ├── dto/
│   │   │               ├── entity/
│   │   │               ├── exception/
│   │   │               ├── repository/
│   │   │               └── service/
│   │   │
│   │   └── resources/
│   │       ├── db/
│   │       │   └── migration/
│   │       │       ├── V1__...
│   │       │       ├── V2__...
│   │       │       └── V3__...
│   │       ├── templates/
│   │       │   └── admin/
│   │       └── application.properties
│   │
│   └── test/
│       └── java/
│           └── com/
│               └── eventify/
│                   └── semana_4/
│
├── pom.xml
├── mvnw
├── mvnw.cmd
├── LICENSE
└── README.md
```

La estructura puede evolucionar a medida que se incorporen nuevas funcionalidades al proyecto.

---

# ⚙️ Funcionalidades

Actualmente Eventify cuenta con las siguientes funcionalidades:

## 📅 Gestión de eventos

* Crear eventos.
* Consultar eventos.
* Consultar un evento por ID.
* Actualizar eventos.
* Eliminación lógica de eventos.
* Buscar eventos por nombre.
* Buscar eventos por ciudad.
* Buscar eventos por categoría.
* Buscar eventos por capacidad mínima del lugar.
* Buscar eventos por rango de fechas.
* Búsqueda parcial ignorando mayúsculas y minúsculas.
* Validación de información obligatoria.
* Paginación mediante `Pageable`.
* Paginación mediante `Slice` para consultas optimizadas.
* Ordenamiento cronológico descendente.
* Asociación obligatoria con un lugar.
* Asociación con múltiples categorías.

## 📍 Gestión de lugares

* Crear lugares.
* Consultar lugares.
* Consultar un lugar por ID.
* Actualizar lugares.
* Eliminar lugares.
* Validar información obligatoria.
* Validar la capacidad del lugar.
* Registrar la ciudad del lugar.
* Paginar resultados.
* Ordenar resultados.

## 🏷️ Gestión de categorías

Eventify incorpora categorías para clasificar los eventos.

Cada categoría contiene:

* Identificador.
* Nombre.
* Descripción.

Un evento puede estar asociado a múltiples categorías y una categoría puede estar asociada a múltiples eventos.

La relación se implementa mediante:

```text
Event
  ↕
ManyToMany
  ↕
Category
```

La relación utiliza una tabla intermedia:

```text
events_categories
```

con las columnas:

```text
event_id
category_id
```

---

# 🔗 Relaciones entre entidades

El modelo actual de Eventify utiliza relaciones JPA entre las entidades principales.

### Event → Venue

Cada evento debe estar asociado obligatoriamente a un lugar.

```text
Event
  │
  └── ManyToOne ──> Venue
```

La relación utiliza la columna:

```text
venue_id
```

### Event ↔ Category

Los eventos pueden tener múltiples categorías.

```text
Event
  │
  └── ManyToMany ──> Category
```

La relación se almacena mediante:

```text
events_categories
```

Esto permite clasificar un evento en categorías como:

* Rock
* Pop
* Jazz
* Tecnología
* Deportes
* Arte
* Teatro
* Electrónica
* Reggaeton
* Conferencia

---

# 🗑️ Eliminación lógica

Los eventos no se eliminan físicamente de la base de datos.

La entidad `Event` utiliza el campo:

```java
private Boolean active = true;
```

Cuando se solicita eliminar un evento, el sistema cambia su estado:

```text
active = false
```

En lugar de ejecutar un `DELETE` físico.

Hibernate utiliza:

```java
@SQLRestriction("active = true")
```

para excluir automáticamente los eventos inactivos de las consultas normales.

Por lo tanto:

```text
DELETE /api/events/{id}
        ↓
active = false
        ↓
El registro permanece en la BD
        ↓
No aparece en consultas normales
```

Esto permite conservar la información histórica del evento.

---

# 💾 Persistencia

Eventify utiliza **Spring Data JPA** y **Hibernate** para gestionar la persistencia de los datos.

Los principales componentes utilizados son:

* `@Entity`
* `@Table`
* `@Id`
* `@GeneratedValue`
* `@Column`
* `@ManyToOne`
* `@ManyToMany`
* `@JoinColumn`
* `@JoinTable`
* `JpaRepository`

Las relaciones entre entidades se gestionan mediante JPA y Hibernate.

---

# 🗃️ Migraciones con Flyway

A partir de la Semana 4, la estructura y los datos iniciales de la base de datos son administrados mediante **Flyway**.

Hibernate tiene deshabilitada la generación automática del esquema:

```properties
spring.jpa.hibernate.ddl-auto=none
```

Flyway ejecuta las migraciones en orden:

```text
V1
 ↓
V2
 ↓
V3
```

### V1 — Esquema inicial

Crea las tablas principales:

```text
events
venues
```

### V2 — Evolución del modelo

Incorpora:

* Ciudad de los lugares.
* Estado `active` de los eventos.
* Relación Event → Venue.
* Tabla `categories`.
* Tabla intermedia `events_categories`.
* Restricciones de integridad referencial.

### V3 — Datos iniciales

Crea datos de prueba y demostración:

* 10 categorías.
* 20 lugares.
* 200 eventos.
* Relaciones entre eventos y categorías.

Esto permite probar el comportamiento del sistema con un volumen de datos mayor.

---

# 🔎 Consultas y filtros avanzados

Eventify permite realizar búsquedas utilizando diferentes criterios.

Los filtros disponibles incluyen:

* Nombre del evento.
* Ciudad.
* Categoría.
* Capacidad mínima del lugar.
* Fecha inicial.
* Fecha final.

Las búsquedas de texto utilizan comparación **case-insensitive** y permiten coincidencias parciales.

Por ejemplo:

```text
rock
```

puede encontrar:

```text
Rock
ROCK
Concierto de ROCK
```

De la misma manera:

```text
bog
```

puede encontrar:

```text
Bogotá
```

---

# ⚡ Consultas optimizadas

Para los listados masivos se utiliza una proyección mediante `EventSummaryDTO`.

En lugar de cargar toda la entidad `Event` y sus relaciones, la consulta obtiene únicamente los datos necesarios:

```text
EventSummaryDTO
├── nombre
├── fecha
├── venueNombre
└── ciudad
```

Esto permite reducir la cantidad de información recuperada de la base de datos.

La consulta utiliza un `JOIN` entre:

```text
events
    ↓
venues
```

y devuelve directamente el DTO.

---

# 📊 Paginación con Slice

Para los listados que pueden contener grandes cantidades de registros se utiliza `Slice`.

A diferencia de `Page`, `Slice` no necesita calcular el número total de elementos.

Esto evita consultas adicionales de tipo `COUNT(*)` cuando únicamente se necesita conocer si existe una página siguiente.

Ejemplo:

```http
GET /api/events/summary?page=0&size=10
```

La consulta obtiene los registros necesarios y permite determinar si existe una página siguiente.

La navegación administrativa utiliza:

* Página anterior.
* Página siguiente.
* Estado de página anterior.
* Estado de página siguiente.

---

# 📅 Ordenamiento

Los listados principales de eventos se ordenan cronológicamente desde la fecha más reciente hasta la más antigua.

Ejemplo:

```text
Evento 3 → 2026-12-20
Evento 2 → 2026-11-15
Evento 1 → 2026-10-20
```

El ordenamiento se realiza mediante `Pageable`.

---

# 🛡️ Manejo de excepciones

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

---

# 📖 Documentación de la API

Eventify utiliza **Swagger / OpenAPI** para documentar y probar los endpoints de la API.

Una vez iniciada la aplicación, la documentación puede consultarse desde:

```text
http://localhost:8080/swagger-ui/index.html
```

Desde Swagger es posible visualizar y probar los diferentes endpoints disponibles, incluyendo:

* Paginación.
* Filtros.
* Consultas por categoría.
* Consultas por ciudad.
* Consultas por capacidad.
* Consultas por fechas.
* Resúmenes optimizados.
* Eliminación lógica.

---

# 🌐 Endpoints principales

## Events

| Método   | Endpoint                      | Descripción                           |
| -------- | ----------------------------- | ------------------------------------- |
| `POST`   | `/api/events`                 | Crear un evento                       |
| `GET`    | `/api/events`                 | Obtener eventos paginados             |
| `GET`    | `/api/events/search`          | Buscar eventos por nombre             |
| `GET`    | `/api/events/{id}`            | Obtener un evento                     |
| `PUT`    | `/api/events/{id}`            | Actualizar un evento                  |
| `DELETE` | `/api/events/{id}`            | Desactivar lógicamente un evento      |
| `GET`    | `/api/events/search/city`     | Buscar eventos por ciudad             |
| `GET`    | `/api/events/search/date`     | Buscar eventos por rango de fechas    |
| `GET`    | `/api/events/search/capacity` | Buscar por capacidad mínima           |
| `GET`    | `/api/events/search/category` | Buscar por categoría                  |
| `GET`    | `/api/events/summary`         | Obtener resumen optimizado de eventos |

### Ejemplo de consulta paginada

```http
GET /api/events/summary?page=0&size=10
```

### Ejemplo de búsqueda por categoría

```http
GET /api/events/search/category?nombre=rock&page=0&size=10
```

### Ejemplo de búsqueda por ciudad

```http
GET /api/events/search/city?ciudad=bog
```

### Ejemplo de búsqueda por capacidad

```http
GET /api/events/search/capacity?capacidad=5000&page=0&size=10
```

### Ejemplo de búsqueda por fechas

```http
GET /api/events/search/date?fechaInicio=2026-10-01T00:00:00&fechaFin=2026-12-31T23:59:59
```

---

## Venues

| Método   | Endpoint           | Descripción               |
| -------- | ------------------ | ------------------------- |
| `POST`   | `/api/venues`      | Crear un lugar            |
| `GET`    | `/api/venues`      | Obtener lugares paginados |
| `GET`    | `/api/venues/{id}` | Obtener un lugar          |
| `PUT`    | `/api/venues/{id}` | Actualizar un lugar       |
| `DELETE` | `/api/venues/{id}` | Eliminar un lugar         |

---

# 🖥️ Panel administrativo

Eventify cuenta con una interfaz web administrativa desarrollada con **Spring MVC y Thymeleaf**, que permite gestionar visualmente los eventos y lugares registrados en el sistema.

El panel utiliza controladores MVC independientes de los controladores REST de la API.

## Funcionalidades

* Visualizar eventos registrados.
* Visualizar lugares registrados.
* Mostrar mensajes informativos cuando no existen registros.
* Acceder al formulario de creación de eventos.
* Acceder al formulario de creación de lugares.
* Seleccionar un Venue al crear un evento.
* Seleccionar múltiples categorías para un evento.
* Filtrar eventos por ciudad.
* Filtrar eventos por categoría.
* Filtrar eventos por capacidad.
* Filtrar eventos por rango de fechas.
* Mantener los filtros durante la navegación entre páginas.
* Navegar mediante Previous / Next.
* Deshabilitar Previous / Next cuando no corresponde.
* Registrar nuevos eventos desde la interfaz web.
* Registrar nuevos lugares desde la interfaz web.
* Redirigir al listado correspondiente después de crear un recurso.

Las rutas administrativas utilizan el prefijo `/admin/**`, mientras que la API REST continúa utilizando `/api/**`.

---

## Rutas principales

| Método | Endpoint            | Descripción                        |
| ------ | ------------------- | ---------------------------------- |
| `GET`  | `/admin/events`     | Listar y filtrar eventos           |
| `GET`  | `/admin/events/new` | Mostrar formulario de nuevo evento |
| `POST` | `/admin/events`     | Crear evento                       |
| `GET`  | `/admin/venues`     | Listar lugares                     |
| `GET`  | `/admin/venues/new` | Mostrar formulario de nuevo lugar  |
| `POST` | `/admin/venues`     | Crear lugar                        |

---

# 🎨 Vistas con Thymeleaf

Las vistas del panel administrativo se encuentran en:

```text
src/main/resources/templates/admin/
```

Entre ellas se encuentran:

```text
events.html
event-form.html
venues.html
venue-form.html
```

Los formularios utilizan Thymeleaf para realizar el binding de los objetos y enviar la información al controlador MVC.

El formulario de eventos permite seleccionar:

```text
Venue
Categories
```

y enviar las asociaciones correspondientes al backend.

---

# 📝 Persistencia de filtros

El catálogo administrativo mantiene los filtros seleccionados durante la paginación.

Por ejemplo:

```text
/admin/events?ciudad=bog&categoria=rock&capacidad=&fechaInicio=&fechaFin=
```

Al navegar a la siguiente página, los filtros continúan presentes en la URL.

Esto permite conservar el contexto de búsqueda mientras el usuario navega por los resultados.

---

# 🪵 Logging SQL

Hibernate está configurado para mostrar las consultas SQL generadas y los valores enviados como parámetros.

Configuración utilizada:

```properties
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.orm.jdbc.bind=TRACE
spring.jpa.properties.hibernate.format_sql=true
```

Esto permite observar:

* Consultas SQL.
* Parámetros enviados.
* Inserts.
* Relaciones.
* Consultas `JOIN`.
* Filtros aplicados.
* Paginación.

Por ejemplo, al crear un evento con Venue y categorías pueden observarse:

```text
INSERT INTO events ...
INSERT INTO events_categories ...
INSERT INTO events_categories ...
```

junto con los valores utilizados como parámetros.

---

# 🧪 Pruebas

El proyecto utiliza **JUnit**, **Mockito** y las herramientas de testing de Spring Boot para verificar el correcto funcionamiento de sus componentes.

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
* Pruebas de controladores con `@WebMvcTest`.
* Pruebas con `MockMvc`.
* Pruebas de servicios con Mockito.

Para ejecutar todas las pruebas:

```bash
mvn test
```

El conjunto actual de pruebas se ejecuta correctamente.

---

# 🛠️ Tecnologías utilizadas

* **Java 21**
* **Spring Boot**
* **Spring Web**
* **Spring MVC**
* **Spring Data JPA**
* **Hibernate**
* **H2 Database**
* **Flyway**
* **Maven**
* **JUnit**
* **Mockito**
* **Spring Boot Test**
* **Thymeleaf**
* **Springdoc OpenAPI / Swagger**

---

# 📦 Compilación

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

---

# 🔄 Evolución del proyecto

Eventify es un proyecto desarrollado de manera incremental. Cada etapa incorpora nuevos conocimientos y funcionalidades sobre la base construida anteriormente.

Las principales etapas desarrolladas hasta el momento incluyen:

### Etapa inicial

* Programación en Java.
* Programación Orientada a Objetos.
* Arquitectura por capas.
* API REST.
* CRUD de eventos y lugares.

### Persistencia

* Spring Data JPA.
* Hibernate.
* H2.
* Repositorios.
* Paginación.
* Ordenamiento.
* Pruebas automatizadas.

### Panel administrativo

* Spring MVC.
* Thymeleaf.
* Formularios.
* Model.
* Redirect.
* Patrón Post/Redirect/Get.
* Pruebas con MockMvc.

### Relaciones y optimización

* Relación `ManyToOne` entre Event y Venue.
* Relación `ManyToMany` entre Event y Category.
* Tabla intermedia `events_categories`.
* Eliminación lógica.
* `@SQLRestriction`.
* Proyecciones mediante Records.
* `EventSummaryDTO`.
* Consultas JPQL optimizadas.
* `JOIN`.
* `Slice`.
* Filtros avanzados.
* Búsqueda parcial e ignore-case.
* Persistencia de filtros en Thymeleaf.
* Migraciones versionadas con Flyway.
* Datos iniciales y masivos.
* Logging SQL y parámetros.

Este README documenta el **estado actual del proyecto** y puede actualizarse conforme se incorporen nuevas funcionalidades, tecnologías y mejoras.

---

# 👨‍💻 Autor

* GitHub: **[Danilo-Doria](https://github.com/Danilo-Doria)**
* LinkedIn: **[Danilo Doria Diaz](https://www.linkedin.com/in/danilodd)**
* Email: **[danilodoria519@gmail.com](mailto:danilodoria519@gmail.com)**

---

# 📄 License

This project is licensed under the MIT License.

See the [`LICENSE`](LICENSE) file for more information.
