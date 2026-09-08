# Civilian Registry REST API

A RESTful web service for managing civilian records. The application exposes CRUD and search operations, persists data in MySQL, and is packaged as a WAR file for deployment to a Jakarta Servlet container.

## Features

- Add a civilian record
- Retrieve all civilian records
- Search using one or more fields
- Update a civilian's address and tax identification number
- Delete a civilian by ID
- Automatically create the database and table when they do not exist
- JSON request and response handling with Jersey and Jackson

## Technologies

- Java 17
- Maven
- Jakarta REST (JAX-RS)
- Jersey 3.1.11
- Jackson JSON provider
- MySQL 8
- MySQL Connector/J 8.0.33
- Jakarta Servlet 6 compatible server, such as Tomcat 10.1+

## Project structure

```text
src/main/java/gr/gnoome/
├── Domain/Person.java
├── Service/Civilian_Service.java
└── Utility/Database_Manager.java

src/main/resources/SQL_Scripts/
├── create_tables.sql
├── add_person.sql
├── exist_person.sql
├── remove_person.sql
└── update_person.sql
```

## Prerequisites

- JDK 17 or newer
- Maven 3.8+
- MySQL Server running locally
- A Jakarta Servlet 6 compatible server, such as Apache Tomcat 10.1+

## Database configuration

The current database connection is configured in `Database_Manager.java` as follows:

```text
Host: localhost
Port: 3306
Database: civilian_database
Username: root
Password: empty
```

The application attempts to create `civilian_database` and its `Civilian_Registry` table automatically on the first API request. The configured MySQL user must therefore have permission to create databases and tables.

> For a real deployment, replace the hard-coded credentials with environment variables or server-managed configuration.

## Build and deploy

1. Start MySQL Server.
2. Build the WAR file:

   ```bash
   mvn clean package
   ```

3. Deploy `target/Civilian_REST.war` to the application server.
4. Start the server on port `8080`.

The base endpoint is:

```text
http://localhost:8080/Civilian_REST/api/Civilians
```

## Data model

| JSON field | Required when creating | Format |
|---|---:|---|
| `id` | Yes | Exactly 8 characters |
| `name` | Yes | Non-empty string |
| `surname` | Yes | Non-empty string |
| `birthdate` | Yes | `dd-mm-yyyy` |
| `gender` | Yes | Intended values: `M` or `F` |
| `address` | No | String |
| `tax` | No | Exactly 9 digits |

Example:

```json
{
  "id": "12345678",
  "name": "Maria",
  "surname": "Papadopoulou",
  "birthdate": "15-04-1990",
  "gender": "F",
  "address": "Athens",
  "tax": "123456789"
}
```

## API reference

| Method | Endpoint | Description | Typical success status |
|---|---|---|---:|
| `POST` | `/Civilians` | Create a civilian | `201 Created` |
| `GET` | `/Civilians` | Retrieve all civilians | `200 OK` |
| `GET` | `/Civilians/search` | Search using query parameters | `200 OK` |
| `PATCH` | `/Civilians/{id}` | Update address and/or tax number | `200 OK` |
| `DELETE` | `/Civilians/{id}` | Delete a civilian | `202 Accepted` |

### Create a civilian

```bash
curl -X POST "http://localhost:8080/Civilian_REST/api/Civilians" \
  -H "Content-Type: application/json" \
  -d '{"id":"12345678","name":"Maria","surname":"Papadopoulou","birthdate":"15-04-1990","gender":"F","address":"Athens","tax":"123456789"}'
```

### Retrieve all civilians

```bash
curl "http://localhost:8080/Civilian_REST/api/Civilians"
```

### Search

The supported query parameters are `id`, `name`, `surname`, `birthdate`, `gender`, `address`, and `tax`. Parameters can be combined.

```bash
curl "http://localhost:8080/Civilian_REST/api/Civilians/search?surname=Papadopoulou&gender=F"
```

### Update a civilian

The current endpoint accepts `address` and `tax` as query parameters.

```bash
curl -X PATCH "http://localhost:8080/Civilian_REST/api/Civilians/12345678?address=Thessaloniki&tax=987654321"
```

### Delete a civilian

```bash
curl -X DELETE "http://localhost:8080/Civilian_REST/api/Civilians/12345678"
```

## Error responses

- `400 Bad Request`: invalid input, duplicate ID, or unsuccessful update
- `404 Not Found`: no civilian exists for the requested deletion
- `503 Service Unavailable`: the application cannot access or initialize MySQL

## Related project

The companion `civilian_registry_client` project provides an interactive command-line client for this API.

## License

No license has been specified for this project.
