# Task Tracker Backend
This project implements a REST API server that manages tasks for a task's tracker system.
The tasks are stored in a mongodb database that runs in AWS. 

## Usage
Build the application with gradle:

    ./mvnw clean build

Run the application:

    ./mvnw bootrun

The REST API server runs at the port 5000.

> Note: add the file 'mongodb.properties' with the the MongoDb connection
> string. This string contains username and password of the db user and 
> should be not published in an open repository.

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
 
![Data model](datamodel.png)


### REST API
#### Endpoints for user management
| Method | URL        | Action                                            |
|--------|------------|---------------------------------------------------|
| POST   | /login     | sign in with email and password                   |
| POST   | /register  | register new user with name, email and password   |
| GET    | /api/users | get the list of registered users (only for admin) |

After /register the user must sign in via the /login request. 
The response of the /login request contains the user details and two tokens: an access token for 
authorization the access to the resources, and a refresh token to renew the access token if it is expired. 
The token must be sent in the authorization header as Bearer Token.

#### Endpoints for tasks management
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

Details of the REST api are scripted at:
[Definition of REST API tasks](doc/readme-rest-api-tasks.md)

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

![Communication](communication.png)

