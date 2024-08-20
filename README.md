
## Security
### The project uses Spring Security standard login and RBAC for authentication and authorization.
### [Register](http://localhost:8080/BGGForum/register.html), [Login](http://localhost:8080/BGGForum/login.html) and [Main](http://localhost:8080/BGGForum) endpoints have free access.
#### **Admin user**: 
##### Username: _john_doe_ Password: _password123_
#### Moderators: None - _must promote user from admin menu_
### Additional info about endpoint access levels - [SecurityConfiguration](https://github.com/A60-Team3/BGGForumProject/blob/main/src/main/java/com/example/bggforumproject/security/SecurityConfiguration.java)
## Database
### A crude implementation of [Flyway](https://flywaydb.org/) database version control. Only used for database creation.
### Database seeding is carried out automatically on first start of the application through a DataLoader class.
### Read the [Custom Enviroment Variables](https://github.com/A60-Team3/BGGForumProject/blob/main/.env.local) file and follow the instructions.
## API documentation
### Swagger project [URL](http://localhost:8080/swagger-ui/index.html) - removed for the MVC project
## Image Uploading
### Must have a valid [Cloudinary](https://cloudinary.com/) account. It is free. Input your info into the .env file.
## To Do
* Implement live database using containerization [Docker](https://www.docker.com/) and cloud services [AWS](https://aws.amazon.com/)


