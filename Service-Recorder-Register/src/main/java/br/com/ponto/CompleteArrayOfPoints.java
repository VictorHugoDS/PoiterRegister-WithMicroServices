package br.com.ponto;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class CompleteArrayOfPoints {

    private final String tableName = "validly_point";
    private Connection connection;

    CompleteArrayOfPoints()  {
        String url = "jdbc:sqlite:point_validly_database.db";
        try {
            connection = DriverManager.getConnection(url);
            //connection.setAutoCommit(false);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            connection.createStatement().execute("create table "+tableName+" (" +
                    "id varchar(200) primary key," +
                    "pointId varchar(200)," +
                    "PointStatus varchar(200)" +
                    "register datetime" +
                    ")");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Map<String,String> map = new HashMap<>();
        map.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,GsonAdvancedDeserializer.class.getName());
        map.put(GsonAdvancedDeserializer.ADVANCED_SERIALIZER_UPPER_CLASS, ArrayList.class.getName());
        map.put(GsonAdvancedDeserializer.ADVANCED_SERIALIZER_SUB_CLASS, Point.class.getName());
        var completeArrayOfPoints = new CompleteArrayOfPoints();
        var kafkaServiceExecute = new KafkaServiceExecute<>(
                "PONTO_ALL_POINTS_OF_USER",
                completeArrayOfPoints::parse,
                RecorderValidlyPoint.class.getSimpleName(),
                map
        );
        kafkaServiceExecute.run();
    }

    private Validation getLastPointTimeRegister(Point point) throws SQLException, ParseException {
        var statement = connection.prepareStatement(
                "SELECT PointStatus FROM  "+tableName+" WHERE pointId = ? ORDER BY register DESC LIMIT 1");
        statement.setString(1,point.getId());
        var result = statement.executeQuery();
        SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-DD HH:mm:ss");

        if(result.next()){
            var valueOfValidation = result.getString("PointStatus");

            return Validation.findEnumByValue(valueOfValidation);
        }
        return null;

    }

    private Point findAndInsertTheValidationOfAPPoint(Point point) throws SQLException, ParseException {
        Validation val = getLastPointTimeRegister(point);
        point.setValidation(val);
        System.out.println(point.getValidation());
        return point;
    }


    private List<Point> generateListWithValidation(ConsumerRecord<String, Message<ArrayList<Point>>> record) {
        ArrayList<Point> pointList = record.value().getPayload();
        return pointList.stream().map(p-> {
            try {
                return findAndInsertTheValidationOfAPPoint(p);
            } catch (SQLException | ParseException e) {
                e.printStackTrace();
            }
            return null;
        }).collect(Collectors.toList());
    }

    private void parse(ConsumerRecord<String, Message<ArrayList<Point>>> record) throws ExecutionException, InterruptedException {

        List<Point> pointWithValidation = generateListWithValidation(record);


        var dispatcher = new KafkaDispatcher<>("PONTO_ALL_POINTS_OF_USER_WITH_VALIDATION",Map.of());
        dispatcher.send(pointWithValidation.get(0).getUser().getCpf(),
                UUID.randomUUID().toString(),
                Point.class.getName(),
                pointWithValidation
        );
        System.out.println("Sending!!!");
    }

}
