package br.com.ponto;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.kafka.clients.consumer.ConsumerRecord;


import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ExecutionException;

public class ServiceUser {

    public static void main(String[] args) {
        var kafkaService = new KafkaServiceExecute<UserRequest>(
                "PONTO_NEW_REQUEST",
                ServiceUser::parse,
                ServiceUser.class.getSimpleName(),
                Map.of()
                );
        kafkaService.run();
    }

    // Simulates a request from other microservice that returns all users from a dataBase
    private static List<User> returnUsers() throws IOException {
        var gson =  new Gson();
        var reader = Files.newBufferedReader(Path.of("Service-User-Authentication/src/main/resources/users.txt"));
        Type UsersArray = new TypeToken<ArrayList<User>>(){}.getType();
        List<User> users =  gson.fromJson(reader, UsersArray);
        return users;
    }

    private static User findsUser(UserRequest userRequest) throws IOException {
        List<User> users = returnUsers();
        return users.stream().filter(u -> Objects.equals(u.getCpf(), userRequest.getCpf())).findFirst().orElse(null);
    }

    private static void parse(ConsumerRecord<String, Message<UserRequest>> record) throws IOException {
        var possibleUser = record.value().getPayload();
        var user = findsUser(possibleUser);
        if( user != null){
            var kafkaDispatcher = new KafkaDispatcher<User>(
                    "PONTO_NEW_POINT_READY_TO_REGISTER",
                    Collections.emptyMap()
            );
            var id = UUID.randomUUID().toString();
            var type = User.class.getName();
            try {
                kafkaDispatcher.send(user.getCpf(),type,id,user);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

}











