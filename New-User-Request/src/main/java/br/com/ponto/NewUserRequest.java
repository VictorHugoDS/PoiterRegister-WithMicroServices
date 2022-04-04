package br.com.ponto;

import java.util.*;
import java.util.concurrent.ExecutionException;

public class NewUserRequest {

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        var kafkaDispatcher = new KafkaDispatcher<UserRequest>("PONTO_NEW_REQUEST", Collections.emptyMap());

        var map = new HashMap<String,String>();
        map.put("Victor Hugo Duarte","11111111111");
        map.put("Corrine Sargent","51472540646");
        map.put("Lara Kelley","51472540646");


        map.forEach((name, cpf) -> {
            var id = UUID.randomUUID().toString();
            var type = UserRequest.class.getName();
            var payload = new UserRequest(name,cpf);
            try {
                kafkaDispatcher.send(cpf,type,id,payload);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });
    }

}
