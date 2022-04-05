package br.com.ponto;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.io.Closeable;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class PointRegister implements Closeable {

    private final String tableName = "point_register";
    private Connection connection;
    private Calendar exactlyCreationTime;

    PointRegister()  {
        String url = "jdbc:sqlite:users_database.db";
        try {
            connection = DriverManager.getConnection(url);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println(connection);

        try {
            connection.createStatement().execute("create table "+tableName+" (" +
                    "id varchar(200) primary key," +
                    "userId varchar(200)," +
                    "name varchar(200)," +
                    "cpf varchar(200)," +
                    "valid varchar(1)," +
                    "status varchar(1)," +
                    "register datetime" +
                    ")");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        var pointerRegister = new PointRegister();
        var kafkaServiceExecute = new KafkaServiceExecute<>(
                "PONTO_NEW_POINT_READY_TO_REGISTER",
                pointerRegister::parse,
                PointRegister.class.getSimpleName(),
                Map.of()
        );
        kafkaServiceExecute.run();
    }

    private String formatData (){
        Calendar cal = Calendar.getInstance();
        exactlyCreationTime = cal;
        String date = new Date(cal.getTime().getTime()).toString();
        date += " " + String.format("%02d:%02d:%02d",cal.get(Calendar.HOUR),
                cal.get(Calendar.MINUTE),
                cal.get(Calendar.SECOND));
        return date;
    }

    public Point insertNewUser(User user) throws SQLException {
        var statement = connection.prepareStatement("Insert into "+tableName+" " +
                "(id,userId,name,cpf,register,valid,status)" +
                "values (?,?,?,?,?,?,?)");
        statement.setString(1, UUID.randomUUID().toString());
        statement.setString(2, user.getId());
        statement.setString(3, user.getName());
        statement.setString(4, user.getCpf());
        statement.setString(5, formatData());
        statement.setString(6, Validation.PENDING.getValidationValue());
        statement.setString(7, PointStatus.PENDING.getPointStatus());

        var point = new Point(UUID.randomUUID().toString(), user, exactlyCreationTime,
                Validation.PENDING, PointStatus.PENDING);
        statement.execute();
        System.out.println(point);
        System.out.println("A new Pointer Register was created!");
        return point;
    }

    private void parse(ConsumerRecord<String, Message<User>> record) throws ExecutionException, InterruptedException {
        var user = record.value().getPayload();
        try {
            var point = insertNewUser(user);
            var kafkaDispatcher = new KafkaDispatcher<Point>("PONTO_POINT_REGISTERED",Map.of());

            kafkaDispatcher.send(
                    point.getUser().getCpf(),
                    UUID.randomUUID().toString(),
                    point.getClass().getName(),
                    point
            );
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Something went wrong when saving this user: " +user.getId());
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
