
## Security
### The project uses Spring Security, JWT tokens and RBAC for authentication and authorization.
### Following the postman button link, you can find several test requests for the api. There is a home page with free access. [<img src="https://run.pstmn.io/button.svg" alt="Run In Postman" style="width: 128px; height: 32px;">](https://a60-team-3.postman.co/collection/36566589-2ccc65bd-2583-44bd-ac64-0a0a08703b9c?source=rip_markdown)
### Register and login endpoints have free access.
### To register - firstName, lastName, email, username, password in the request body.
### To login - username, password
### Additional info about endpoint access levels - [SecurityConfiguration](https://github.com/A60-Team3/BGGForumProject/blob/main/src/main/java/com/example/bggforumproject/security/SecurityConfig.java)
## Database
### A crude implementation of [Flyway](https://flywaydb.org/) database version control. Only used for database creation.
### Database seeding is carried out automatically on first start of the application through a DataLoader class.
### Read the [Custom Enviroment Variables](https://github.com/A60-Team3/BGGForumProject/blob/main/.env.local) file and follow the instructions.
## API documentation
### Swagger project [URL](http://localhost:8080/swagger-ui/index.html)

## To Do
* Full unit testing
* Proper endpoint documentation through [Swagger](https://swagger.io/) and testing
* Implement live database using containerization [Docker](https://www.docker.com/) and cloud services [AWS](https://aws.amazon.com/)
* Add user photo option
* Improve security by implementing best practices
* Add front-end support through [ThymeLeaf](https://www.thymeleaf.org/)
* Implement Pagination

