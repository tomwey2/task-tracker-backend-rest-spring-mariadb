# Task Tracker Backend
This project implements a REST API server that manages tasks for a task's tracker system.
The tasks are stored in a mariadb database. 

### Running with Docker

This setup runs the application and a custom MariaDB database in separate Docker containers.

**1. Create a Docker Network**

First, create a dedicated network for the containers to communicate:

    docker network create task-app-net

**2. Build and Start the Custom MariaDB Container**

We now use a custom Dockerfile (`Dockerfile.mariadb`) and an initialization script (`db/init.sql`) to create the database and user explicitly.

First, build the custom MariaDB image:

    docker build -t custom-mariadb -f Dockerfile.mariadb .

Now, run the custom database container. It still requires a root password for its initial setup.

    docker run -d \
      --name mariadb \
      --network task-app-net \
      -p 3306:3306 \
      -e MARIADB_ROOT_PASSWORD=secret \
      custom-mariadb

**3. Build and Run the Application Container**

Build the application's Docker image:

    docker build -t task-tracker-backend .

Now, run the application container. It connects to the database using the credentials we defined in the `init.sql` script (`user` and `password`).

    docker run --rm \
      --name task-tracker-app \
      --network task-app-net \
      -p 8080:8080 \
      -e MARIADB_USER=user \
      -e MARIADB_PASSWORD=mysecretpw \
      -e APP_JWT_SECRET=6d6319eb8e676a989ee3932b5cc9e916e2125fdc97a3d2db500eab6d63822812 \
      task-tracker-backend

## Definitions

### Data definition
#### Data model
The data model consists of three main entities: tasks, users and projects. 
Each project consists of zero or more tasks. Each task can be assigned only to 
one project. 

The user that creates a task is its reporter. A task can only 
be reported by exactly one user. The user that reports the task can assign
other users to the task. One task can be assigned to zero or more 
users. A user can be member of zero or more projects.  
 
![Data model](docs/datamodel.png)


### REST API
#### Endpoints for authentication
The following table shows the operations that affect the user authentication.

| Method | URL                | Action                                                 |
|--------|--------------------|--------------------------------------------------------|
| POST   | /api/auth/login    | sign in with email and password                        |
| GET    | /api/auth/me       | returns the current logged in user                     |
| POST   | /api/auth/register | register new user with name, email and password (TODO) |

After /register the user must sign in via the /login request.
The response of the /login request contains the user details and two tokens: an access token for
authorization the access to the resources, and a refresh token to renew the access token if it is expired.
The token must be sent in the authorization header as Bearer Token.

#### Endpoints for user management
The following table shows the operations that affect the user management. These operations 
can only perform by admins.

| Method | URL                | Action                           |
|--------|--------------------|----------------------------------|
| GET    | /api/users         | get the list of registered users |
| POST   | /api/users         | create a new user                |
| GET    | /api/users         | get the list of registered users |


#### Endpoints for tasks management
The following table shows the operations that affect the task management.
In order to use the tasks endpoints, the user must be authenticated before.

| Method | URL                        | Action                                      |
|--------|----------------------------|---------------------------------------------|
| GET    | /api/tasks                 | get all tasks from database                 |
| GET    | /api/tasks/{id}            | get task details based on id                |
| POST   | /api/tasks                 | create a new task                           |
| DELETE | /api/tasks/{id}            | remove/delete task by id                    |
| PUT    | /api/tasks/{id}            | update task details by id                   |
| GET    | /api/tasks/{id}/reportedby | get the user that has reported the task     |
| GET    | /api/tasks/{id}/assignees  | get the users that are assigned to the task |

Details of the REST api are described with Swagger: http://localhost:8080/swagger-ui/index.html

## Communication
The communication protocol between client and server is HTTP. 
It provides operations (HTTP methods) such as GET, POST, PUT, and DELETE.
The user must be registered.
Before the user can access to a resource, e.g. tasks, the user must be login.
if the server can authenticate the user, it responds with an access token and a
refresh token (both JSON web token). 
When the client requests a resource, he must send the access token in the 
authorization header as bearer token.

The following picture shows this process graphically.

![Communication](docs/communication.png)
