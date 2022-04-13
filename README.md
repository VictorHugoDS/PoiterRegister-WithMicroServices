# _Point Register_
A mini project for register points developed using the microservice architecture.

### Backstory

This project was developed as part of my study of how systems can be created using the microservice architecture. For this, it was used the Apache Kafka for the asynchronous connection between the microservices and Maven for software project management. 


### How it works?

The structure of the project is simplified in the image below: 

![zoe wants to register her point drawio](https://user-images.githubusercontent.com/51013266/162975580-62a7e7f2-89df-4e68-8d1e-30736d2930da.png)


### Features

- the system awaits for a user make an HTTP connection in the `localHost8080/register-point/new`;

- A user requests a point register passing the information through the URL parameters;

- After request a register, the user will be authenticated and then register in a local database;

- After the register, the point will be validated and determinate if it was register late or early;

- When 4 points are register, the system will generate a report in JSON format in a directory named as the name of the user, a and the file named as the date of the registers.

- After 4 poits, all registers will be invalid, because a user only can make 4 registers at a day;


### How to run the project ?

- You will need java 11 or a superior version;

- You need to run the [Apache Kafka](https://kafka.apache.org/) at `port 127.0.0.1:9092`;

After that, you will just need to run all the microservices. And that, register a point at `localHost8080/register-point/new?name=&cpf=`


### Some considerations

- The system was for training my skills in microservice development, so the system doesn’t have a user database, all the users are in a fixer [json file](https://github.com/VictorHugoDS/PoiterRegister-WithMicroServices/blob/main/Service-User-Authentication/src/main/resources/users.txt).

- The Report is generated at `src/main/java/resources`;

- It was very cool to develop it, I had some database problems and had to learn some things about serialization e deserialization to make it work all it works. But knowledge it’s power, isn't it?

#### What was used:

- [Kafka-Clients](https://mvnrepository.com/artifact/org.apache.kafka/kafka-clients)
- [IntelliJ IDEA](https://www.jetbrains.com/pt-br/idea/)
- [Maven](https://maven.apache.org/)
- [SLF4J Simple Binding](https://mvnrepository.com/artifact/org.slf4j/slf4j-simple)
- [Gson](https://mvnrepository.com/artifact/com.google.code.gson/gson )
- [Jetty Servlet](https://mvnrepository.com/artifact/org.eclipse.jetty/jetty-servlet)
- [Sqlite Jdbc](https://mvnrepository.com/artifact/org.xerial/sqlite-jdbc)
