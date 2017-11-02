# ClientDatabase

It's a back-end server which uses Spring MVC and Hibernate frameworks to communicate with MySQL database.

`ClientDatabase` also have RESTful Service, to check how it works you can use another project from my github
`ClientDatabaseAngularFront`


## Quick setup

```
1. You need to have some relational database management system installed, I used MYSQL.
2. Create two schemas clients and clients_test in you database.
3. Download or clone repo.
4a) This repo has setUp.sql file which can populate your sql database with some data.
4b) This repo also has a createSecurityTables.sql to generate tables required for authentication.
5. Import project files in your IDE.
6. This project does not use Spring Boot, you need to have tomcat installed.
7. Run build from you IDE.
8. Go to this link http://localhost:8080/ or whatever address and port you are running your server.
9. To login use - Username: admin, Password: admin [ROLE_ADMIN] or Username: user, Password: user [ROLE_USER]
```

### But what it exactly does?

This project is an CRUD web application which can help you mange your client data by web page.


## Screenshots

![main page](https://github.com/Kreattehak/ClientDatabase/blob/screenshots/main_page.png "Main page with table of clients")
Main page with table of clients.

![client without address](https://github.com/Kreattehak/ClientDatabase/blob/screenshots/client_without_address.png "Active client without address")
Buttons disabled when client has no address.

![localized app](https://github.com/Kreattehak/ClientDatabase/blob/screenshots/localized_app.png "Polish version of project")
Localized version of project.


## More about this project
This project is an CRUD web application, which allows you to store Clients with their addresses 
in database. You can use standard Spring MVC with JSP part of this app or consume REST with front-end 
app like `ClientDatabaseAngularFront` from my github. You can check it [here](https://github.com/Kreattehak/ClientDatabaseAngularFront).

MVC part contains multiple jsp pages which allow you to add, delete, edit clients and their addresses,
you can also mark one of them as main address.

This project has convenient log system. Logs are saved at your user home location in ClientDatabase folder. 
So if you account is named 'User', this path looks like that `C:\Users\User\ClientDatabase\logs`.

### Some of design choices I took:
This project still contains 'old' version of this app which was based on JSP and some basic Spring Security authentication, 
it's updated to get authentication token from cookie.
JUnit 5 class `org.junit.Assert` does not provide `assertThat()` method, 
to prevent test failure in newer versions of Junit I use `assertThat()` method from `org.hamcrest.MatcherAssert`.

### How this app was developed over time
First and foremost, I created this app to learn how Object-Relational Mapping works. Before, 
I was developing `ElectronicShop` (now postponed) app and I have encountered a several ORM problems e.g. 
with storing lists of products that users have bought or various relationships between entities. 
I choose Hibernate ORM to perform simple CRUD operations on MYSQL Relational Database. 
After creating basic relationships between entities (@OneToMany, @ManyToOne), 
and storing them in underling database with Hibernate support I decided to change this app to web app, 
cause creating objects from command line isn't the best design choice.

Another stage of developing was separation of code to a front and a back-end.
For front-end I initially used pure JavaScript, 
but after I studied REST topics I came to an conclusion that Angular framework 
would perfectly fit my needs.

### Problems I encountered while developing this app:
Change of thinking while writing JS code (ES5) in comparison to Java,
e.g. that blocks doesn’t create scope, behavior of `this` keyword in various situations (Typescript 'this' in callbacks).

How to manage Spring security configuration which allows both jwt authentication and default form login with changed cookie data.

How to store clients addresses. I used HashSet with hashCode() method overridden to hash address ids,
but that didn't eliminated data duplication in database. 

How to test both mvc and rest part of this project.
 
How to handle exceptions when client violate rules and send handmade requests.

Problem with transactions in early version when controllers had some logic that used queries to database.
Now it's refactored and all logic is in services.
### Why you haven't use Spring Boot?
This app from beginning was developed with clear purpose to learn how Spring and Hibernate frameworks
work and later I wanted to know how Angular framework can be used to consume REST.

## REST resources
Same as in ClientDatabaseAngularFront
```
/api/getAllClients - return all clients in array [GET]
/api/admin/getClient - with id parameter, to get desired client [GET]
/api/admin/updateClient - updates client in database [PUT]
/api/admin/deleteClient - deletes client from database [POST - because angular doesn't send body with DELETE]
/api/admin/saveNewClient - saves client in database after data validation [POST]

/api/admin/getAllAddresses - with id parameter, get all client's addresses [GET]
/api/admin/saveNewAddress - with id parameter, save new address to database after validation [POST]
/api/admin/updateAddress - updates address in database after validation [PUT]
/api/admin/deleteAddress - delete one of client's addresses [POST - why? Check few above]
/api/admin/editMainAddress - edit client's main address [PUT]
```

## Future of this project
My purpose is to transform this project into 'AnimalShelterManagement' project working on Spring Boot.