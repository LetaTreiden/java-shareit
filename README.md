# ShareIt - Java Project Documentation

## Introduction

ShareIt is a Java-based application designed to facilitate the sharing of items among users. This documentation provides an overview of the project, installation instructions, usage guidelines, and details on the API endpoints.

## Table of Contents

1. [Introduction](#introduction)
2. [Installation](#installation)
3. [Usage](#usage)
4. [API Endpoints](#api-endpoints)
5. [Contributing](#contributing)
6. [License](#license)

## Installation

To set up the project locally, follow these steps:

1. **Clone the repository:**

   ```sh
   git clone https://github.com/LetaTreiden/java-shareit.git
   cd java-shareit
   
2. **Build the project:**

   Ensure you have Maven installed. Run the following command to build the project:
   ```sh
   mvn clean install

3. **Run the application:**

   Use the following command to run the application:
   ```sh
   java -jar target/shareit-0.0.1-SNAPSHOT.jar

## Usage

   To use the ShareIt application, you need to interact with its API. Below are some examples of how to use the API.
   
   Creating a new user:

```http
POST /users
Content-Type: application/json

{
    "name": "John Doe",
    "email": "john.doe@example.com"
}
```

   Listing all items:

```http
GET /items
```

   Adding a new item:

```http
POST /items
Content-Type: application/json

{
    "name": "Lawn Mower",
    "description": "A powerful lawn mower",
    "available": true,
    "ownerId": 1
}
```

## API Endpoints
##User Endpoints
   Create a User
```
POST /users
Request Body: { "name": "string", "email": "string" }
```

   Get All Users
```
GET /users
```

   Get User by ID
```
GET /users/{id}
```

   Update User
```
PUT /users/{id}
Request Body: { "name": "string", "email": "string" }
```

   Delete User
```
DELETE /users/{id}
```

##Item Endpoints
   Create an Item
```
POST /items
Request Body: { "name": "string", "description": "string", "available": boolean, "ownerId": number }
```

   Get All Items
```
GET /items
```

   Get Item by ID
```
GET /items/{id}
```

   Update Item
```
PUT /items/{id}
Request Body: { "name": "string", "description": "string", "available": boolean }
```

   Delete Item
```
DELETE /items/{id}
```

## Contributing
   Contributions are welcome! Please follow these steps to contribute:

   1. Fork the repository.
   2. Create a new branch (git checkout -b feature-branch).
   3. Commit your changes (git commit -am 'Add new feature').
   4. Push to the branch (git push origin feature-branch).
   5. Create a new Pull Request.

## License
   This project is licensed under the MIT License.
