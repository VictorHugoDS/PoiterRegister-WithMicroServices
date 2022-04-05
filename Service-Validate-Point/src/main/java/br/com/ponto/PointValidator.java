package br.com.ponto;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.internals.Sender;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class PointValidator {

    public static void main(String[] args) {
        var pointValidator = new PointValidator();
        var kafkaService = new KafkaServiceExecute<>(
                "PONTO_ALL_POINTS_OF_USER",
                pointValidator::parse,
                PointValidator.class.getSimpleName(),
                Map.of(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,GsonAdvancedDeserializer.class.getName())
        );
        System.out.println("constuctor =sdasdasdasd");
        kafkaService.run();



    }


    private void parse(ConsumerRecord<String, Message<ArrayList<?>>> record) throws SQLException, ParseException, ExecutionException, InterruptedException {
        ArrayList<?> pointList = record.value().getPayload();
        //var a = pointList.stream().findFirst().get().toString());
        System.out.println(record.value().toString());
        System.out.println(pointList.get(0).getClass().getSimpleName());
        System.out.println(pointList.get(0).toString());
        /*ArrayList<Point> pointList = record.value().getPayload();
        var a = pointList.stream().findFirst().get().toString();
        System.out.println( a + " " + a.getClass());
        int count = 0;
        System.out.println(pointList.getClass().getSimpleName()+ " " + pointList.toString());
        Point pointToValidate = null;

        for (var point: pointList ) {
            if (point.getValidation() == Validation.VALID){
                count += 1;
            } else if (point.getValidation() == Validation.PENDING){
                pointToValidate = point;
            }
        }
        if(count <= 3){
            pointToValidate.setValidation(Validation.VALID);
        } else {
            pointToValidate.setValidation(Validation.INVALID);
        }

        var dispatcher = new KafkaDispatcher<>("PONTO_VALIDATED_POINT",Map.of());
        dispatcher.send(pointToValidate.getUser().getCpf(),
                UUID.randomUUID().toString(),
                Point.class.getName(),
                pointToValidate
                );*/
    }
}
