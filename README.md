# _Point Register_
A mini project for register points developed using the micro-service architecture.

### Backstory

This project was developed as part of my study of how systems can be created using the micro-service architecture. For this, it was used the Apache Kafka for the asynchronous connection between the micro-services and Maven for software project management. 


### How it work?

The structure of the project can be simplify in the image below: 

![zoe wants to register her point drawio](https://user-images.githubusercontent.com/51013266/162975580-62a7e7f2-89df-4e68-8d1e-30736d2930da.png)


### Features

- the system awaits for a user make a HTTP connection in the `localHost8080/register-point/new`;

- A user requests a point register passing the information through the URL parameters;

- After request a register, the user will be authenticate and than register in a local database;

- After the register, the point will be validate and determinate if it was register late or early;

- When 4 points are register, the system will generate a report in JSON format in a directory named as the name of the user, a and the file named as the date of the registers.


### How to run the project ?

- You will need java 11 or a superior version;

- You need to run the [Apache Kafka](https://kafka.apache.org/) at `port 127.0.0.1:9092`;

After that, you will just need to run all the micro-services. And that, register a point at `localHost8080/register-point/new?name=&cpf=`


### How to run the project ?

- You will need java 11 or a superior version;

- You need to run the (apache kafka link) in the port 127.0.0.1:9092;

After that, you will just need to run the micro-services.


### Some considerations

- The system was for trening my skills in micro-service development, so the system doesn’t have a user database, all the users are in a fixer [json file](https://github.com/VictorHugoDS/PoiterRegister-WithMicroServices/blob/main/Service-User-Authentication/src/main/resources/users.txt).

- The Report is generate at `src/main/java/resources`;

- It’s was very cool to develop it, I had some database problems and had to learn some things about serialization e deserialization to make it work all it work. But knowledge it’s power, isn't it?
