package br.com.ponto;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.io.Closeable;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class RecorderValidlyPoint implements Closeable{

    private final String tableName = "validly_point";
    private Connection connection;

    RecorderValidlyPoint(){
        try {
            Class.forName("org.sqlite.JDBC");
            String url = "jdbc:sqlite:point_validly_database.db";
            connection = DriverManager.getConnection(url);
            //connection.setAutoCommit(false);

            connection.createStatement().execute("create table "+tableName+" (" +
                    "id varchar(200) primary key," +
                    "pointId varchar(200)," +
                    "PointStatus varchar(200)," +
                    "register datetime" +
                    ")");
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args)  {
        var recorderValidlyPoint = new RecorderValidlyPoint();
        var kafkaServiceExecute = new KafkaServiceExecute<>(
                "PONTO_POINT_REGISTER",
                recorderValidlyPoint::parse,
                RecorderValidlyPoint.class.getSimpleName(),
                Map.of()
        );
        kafkaServiceExecute.run();
    }

    private String formatData (){
        Calendar cal = Calendar.getInstance();
        String date = new Date(cal.getTime().getTime()).toString();
        date += " " + String.format("%02d:%02d:%02d",cal.get(Calendar.HOUR),
                cal.get(Calendar.MINUTE),
                cal.get(Calendar.SECOND));
        return date;
    }

    public Point insertPointerRegister(Point point) throws SQLException {
        var statement = connection.prepareStatement("Insert into "+tableName+" " +
                "(id,pointId,PointStatus,register)" +
                "values (?,?,?,?)");
        Validation status;
        if(point.getValidation()==null){
            status = Validation.PENDING;
        } else {
            status = point.getValidation();
        }
        var id = UUID.randomUUID().toString();
        statement.setString(1, UUID.randomUUID().toString());
        statement.setString(2, point.getId());
        statement.setString(3, status.getValidationValue());
        statement.setString(4, formatData());

        point.setValidation(Validation.PENDING);
        statement.execute();
        System.out.println(point);
        System.out.println("A new Validation Point was created!");

        return point;
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


    private void parse(ConsumerRecord<String, Message<Point>> record) throws SQLException, ExecutionException, InterruptedException {
        var point = record.value().getPayload();
        var newPoint = insertPointerRegister(point);
        if(newPoint.getValidation() == Validation.PENDING){
            var kafkaDispatcher = new KafkaDispatcher<Point>("PONTO_POINT_REGISTERED",Map.of());

            kafkaDispatcher.send(
                    point.getUser().getCpf(),
                    UUID.randomUUID().toString(),
                    point.getClass().getName(),
                    point
            );
            System.out.println("Sending: " + point.toString());
        }
    }

    @Override
    public void close() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
