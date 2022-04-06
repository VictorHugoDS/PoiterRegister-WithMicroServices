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
        map.put("Victor Hugo Duarte1","11111111111");
        map.put("Corrine Sargent1","51472540646");
        map.put("Lara Kelley1","51472540646");
        map.put("Victor Hugo Duarte2","11111111111");
        map.put("Corrine Sargent2","51472540646");
        map.put("Lara Kelley2","51472540646");
        map.put("Victor Hugo Duarte3","11111111111");
        map.put("Corrine Sargen3t","51472540646");
        map.put("Lara Kelle3y","51472540646");
        map.put("Victor Hugo D4uarte","11111111111");
        map.put("Corrine 4argent","51472540646");
        map.put("Lara K4elley","51472540646");
        map.put("Victor 5Hugo Duarte","11111111111");
        map.put("Corrin5e Sargent","51472540646");
        map.put("Lara 5Kelley","51472540646");
        map.put("Victor 6Hugo Duarte","11111111111");
        map.put("Corrin6e Sargent","51472540646");
        map.put("Lara 6Kelley","51472540646");
        map.put("Victo7r Hugo Duarte","11111111111");
        map.put("Corri8ne Sargent","51472540646");
        map.put("Lara Ke7lley","51472540646");
        map.put("Victo9r Hugo Duarte","11111111111");
        map.put("Cor8rine Sargent","51472540646");
        map.put("Lara Kelle8y","51472540646");
        map.put("Victor 8Hugo Duarte","11111111111");
        map.put("Cor87rine Sargent","51472540646");
        map.put("Lar7a Kelley","51472540646");
        map.put("Victor8 Hugo Duarte","11111111111");
        map.put("C7orrine6 Sargent","51472540646");
        map.put("Lara Kel6ley","51472540646");
        map.put("Victor H6ugo Duarte","11111111111");
        map.put("Cor7rine Sargent","51472540646");
        map.put("Lara K6elley","51472540646");map.put("Vict1or Hugo Duar1te","11111111111");
        map.put("Cor5rin6e Sargent","51472540646");
        map.put("Lara K7elley","51472540646");map.put("Victor Hug11o Duarte","11111111111");
        map.put("Corr8ine Sargent","51472540646");
        map.put("Lara 5Ke8lley","51472540646");
        map.put("Vict5or Hugo Duarte","11111111111");
        map.put("Corrine Sa7rgent","51472540646");
        map.put("Lara Ke8lley","51472540646");




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
