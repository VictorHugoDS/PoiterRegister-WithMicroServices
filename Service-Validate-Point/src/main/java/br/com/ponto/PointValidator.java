package br.com.ponto;

import br.com.ponto.consumer.GsonAdvancedDeserializer;
import br.com.ponto.consumer.KafkaServiceExecute;
import br.com.ponto.databaseThings.DatabaseRequest;
import br.com.ponto.databaseThings.TypesOfRequest;
import br.com.ponto.messageThings.Message;
import br.com.ponto.producer.KafkaDispatcher;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class PointValidator {

    private boolean generateReport = false;
    private User user;

    public static void main(String[] args) {
        var pointValidator = new PointValidator();
        Map<String,String> map = new HashMap<>();
        map.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, GsonAdvancedDeserializer.class.getName());
        map.put(GsonAdvancedDeserializer.ADVANCED_SERIALIZER_UPPER_CLASS,ArrayList.class.getName());
        map.put(GsonAdvancedDeserializer.ADVANCED_SERIALIZER_SUB_CLASS, Point.class.getName());
        var kafkaService = new KafkaServiceExecute<>(
                "PONTO_ALL_POINTS_OF_USER",
                pointValidator::parse,
                PointValidator.class.getSimpleName(),
                map
        );
        kafkaService.run();

    }

    private void parse(ConsumerRecord<String, Message<ArrayList<Point>>> record) throws ExecutionException, InterruptedException {

        ArrayList<Point> pointList = record.value().getPayload();
        System.out.println(pointList.toString());
        var pointsToValidate = getPointsValidated(pointList);

        for (var point: pointsToValidate) {
            var request = new DatabaseRequest<>(
                    TypesOfRequest.UPDATE,
                    "PONTO_POINT_VALIDATION_COMPLETE",
                    point,
                    "valid");
            var kafkaDispatcher = new KafkaDispatcher<DatabaseRequest<Point>>("PONTO_POINT_DATABASE_REQUEST",Map.of(),PointValidator.class.getSimpleName());
            kafkaDispatcher.send(
                    point.getUser().getCpf(),
                    UUID.randomUUID().toString(),
                    request.getClass().getName(),
                    request
            );
        }
        if(generateReport){
            sendToGenerateReport(user);
        }

    }

    private List<Point> getPointsValidated(ArrayList<Point> pointList)  {
        int count = 0;

        var pointsToValidate = pointList.stream().filter(p->p.getValidation()==Validation.PENDING)
                .collect(Collectors.toList());
        Date maxDate = pointsToValidate.stream().map(p->p.getDatePoint().getTime()).max(Date::compareTo).get();
        for (var point: pointList) {
            if (point.getValidation() == Validation.VALID){
                count += 1;
            }
        }

        for (var point : pointsToValidate) {
            if(!point.getDatePoint().getTime().equals(maxDate)){
                point.setValidation(Validation.INVALID);
            } else {
                if(count <= 3){
                    point.setValidation(Validation.VALID);
                    if(count==3){
                        generateReport = true;
                        user = point.getUser();
                    }
                } else {
                    point.setValidation(Validation.INVALID);
                }
            }
        }
        return pointsToValidate;
    }

    private void sendToGenerateReport(User user) throws ExecutionException, InterruptedException {
        System.out.println("Ready for report");
        var kafkaDispatcher = new KafkaDispatcher<User>("PONTO_USER_READY_FOR_REPORT",Map.of(),User.class.getSimpleName());
        kafkaDispatcher.send(
                user.getCpf(),
                UUID.randomUUID().toString(),
                user.getClass().getName(),
                user
        );
    }
}
