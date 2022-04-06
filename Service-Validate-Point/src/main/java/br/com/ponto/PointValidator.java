package br.com.ponto;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class PointValidator {

    public static void main(String[] args) {
        var pointValidator = new PointValidator();
        Map<String,String> map = new HashMap<>();
        map.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,GsonAdvancedDeserializer.class.getName());
        map.put(GsonAdvancedDeserializer.ADVANCED_SERIALIZER_UPPER_CLASS,ArrayList.class.getName());
        map.put(GsonAdvancedDeserializer.ADVANCED_SERIALIZER_SUB_CLASS, Point.class.getName());
        var kafkaService = new KafkaServiceExecute<>(
                "PONTO_ALL_POINTS_OF_USER_WITH_VALIDATION",
                pointValidator::parse,
                PointValidator.class.getSimpleName(),
                map
        );
        System.out.println("constuctor =sdasdasdasd");
        kafkaService.run();



    }

    private void parse(ConsumerRecord<String, Message<ArrayList<Point>>> record) throws ExecutionException, InterruptedException {

        ArrayList<Point> pointList = record.value().getPayload();
        int count = 0;
        Point pointToValidate = pointList.stream().filter(p->p.getValidation()==Validation.PENDING)
                .collect(Collectors.toList()).stream().findFirst().orElse(null);
        pointList.stream().forEach(p->System.out.println(p.getValidation()));
        System.out.println(pointToValidate);

        for (var point: pointList ) {
            if (point.getValidation() == Validation.VALID){
                count += 1;
            }
        }
        if(count <= 3){
            pointToValidate.setValidation(Validation.VALID);
        } else {
            pointToValidate.setValidation(Validation.INVALID);
        }

        var dispatcher = new KafkaDispatcher<>("PONTO_POINT_REGISTER",Map.of());
        dispatcher.send(pointToValidate.getUser().getCpf(),
                UUID.randomUUID().toString(),
                Point.class.getName(),
                pointToValidate
                );
        System.out.println("A point was processed and was marked as valid or invalid" + pointToValidate.toString());
    }
}
