package br.com.ponto;

import br.com.ponto.consumer.GsonAdvancedDeserializer;
import br.com.ponto.consumer.KafkaServiceExecute;
import br.com.ponto.databaseThings.DatabaseRequest;
import br.com.ponto.databaseThings.TypesOfRequest;
import br.com.ponto.messageThings.Message;
import br.com.ponto.producer.KafkaDispatcher;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutionException;

public class StatusValidate {


    public static void main(String[] args) {
        var validate = new StatusValidate();
        Map<String,String> map = new HashMap<>();
        map.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, GsonAdvancedDeserializer.class.getName());
        map.put(GsonAdvancedDeserializer.ADVANCED_SERIALIZER_UPPER_CLASS, ArrayList.class.getName());
        map.put(GsonAdvancedDeserializer.ADVANCED_SERIALIZER_SUB_CLASS, Point.class.getName());
        var kafkaServiceExecute = new KafkaServiceExecute<>(
                "PONTO_ALL_POINTS_OF_USER",
                validate::parse,
                StatusValidate.class.getSimpleName(),
                map
        );
        kafkaServiceExecute.run();
    }

    private Point determineIfLateOrEarly(String hour, Point point) throws ParseException {
        Calendar cal = Calendar.getInstance();
        String date = new java.sql.Date(cal.getTime().getTime()).toString();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateHour = date +" "+ hour;
        Date data = sdf.parse(dateHour);

        Calendar superiorLimit = Calendar.getInstance();
        superiorLimit.setTime(data);
        superiorLimit.add(Calendar.MINUTE,15);

        Calendar inferiorLimit = Calendar.getInstance();
        inferiorLimit.setTime(data);
        inferiorLimit.add(Calendar.MINUTE,-15);

        Calendar pointHour = point.getDatePoint();

        if (pointHour.after(superiorLimit)){
            point.setPointStatus(PointStatus.LATE);
        } else if(pointHour.before(inferiorLimit)){
            point.setPointStatus(PointStatus.EARLY);
        } else{
            point.setPointStatus(PointStatus.OK);
        }
        return point;
    }


    private List<Point> validateStatusPoint(ArrayList<Point> points) throws ParseException {
        var validPoints = points.stream().filter(p->p.getValidation() == Validation.VALID).toList();
        Date maxDate = points.stream().filter(p->p.getValidation() == Validation.PENDING).toList()
                .stream().map(p->p.getDatePoint().getTime()).max(Date::compareTo).get();
        var pointsToValidate = points.stream().filter(p->p.getValidation() == Validation.PENDING).toList();
        for (var pointToValidate : pointsToValidate) {
            if(!pointToValidate.getDatePoint().getTime().equals(maxDate)){
                pointToValidate.setPointStatus(PointStatus.INVALID);
            } else {
                switch (validPoints.size()){
                    case 0:
                        determineIfLateOrEarly("07:00:00", pointToValidate);
                    case 1:
                        determineIfLateOrEarly("12:00:00", pointToValidate);
                    case 2:
                        determineIfLateOrEarly("13:00:00", pointToValidate);
                    case 3:
                        determineIfLateOrEarly("17:00:00", pointToValidate);
                        break;
                    default:
                        pointToValidate.setPointStatus(PointStatus.INVALID);

                }
                return pointsToValidate;
            }
        }
        sendToDeadLetter(Objects.requireNonNull(pointsToValidate.stream().findFirst().orElse(null)), validPoints);
        throw new IllegalStateException("The array of Values was empty for the status validator");
    }

    private void sendToDeadLetter(Point pointToValidate, List<Point> validPoints) {
        var kafkaDispatcher = new KafkaDispatcher<List<Point>>("PONTO_DEAD_LETTER",Collections.emptyMap(),StatusValidate.class.getSimpleName());
        var id =UUID.randomUUID().toString();
        try {
            kafkaDispatcher.send(pointToValidate.getId(),id,String.class.getName(), validPoints);
        } catch (InterruptedException | ExecutionException error) {
            throw new RuntimeException("Couldn't send a message to DEAD-LETTER topic");
        }
    }

    private void parse(ConsumerRecord<String, Message<ArrayList<Point>>> record) throws ExecutionException, InterruptedException, ParseException {
        var point = record.value().getPayload();
        var statedPoints = validateStatusPoint(point);
        for (var statedPoint : statedPoints ) {
            var request = new DatabaseRequest<>(
                    TypesOfRequest.UPDATE,
                    "PONTO_POINT_STATUS_COMPLETE",
                    statedPoint,
                    "status");
            var kafkaDispatcher = new KafkaDispatcher<DatabaseRequest<Point>>("PONTO_POINT_DATABASE_REQUEST",Map.of(),StatusValidate.class.getSimpleName());
            kafkaDispatcher.send(
                    statedPoint.getUser().getCpf(),
                    UUID.randomUUID().toString(),
                    request.getClass().getName(),
                    request
            );

        }

        System.out.println("A point was successful classified");
    }
}
