package br.com.ponto;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.io.IOException;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class ConcatenateAllPointsOfUser {

    private Connection connection;

    public ConcatenateAllPointsOfUser() {
        String url = "jdbc:sqlite:users_database.db";
        try {
            connection = DriverManager.getConnection(url);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            connection.createStatement().execute("create table point_register (" +
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

    public static void main(String[] args){
        var concatenate = new ConcatenateAllPointsOfUser();
        var kafkaServiceExecute = new KafkaServiceExecute<>(
                "PONTO_POINT_REGISTERED",
                concatenate::parse,
                PointRegister.class.getSimpleName(),
                Map.of()
        );
        kafkaServiceExecute.run();
       }


    private String[] getHourInterval(Calendar dataToSearch){
        String date = new Date(dataToSearch.getTime().getTime()).toString();
        return new String[]{date + " 00:00:00", date + " 23:59:59"};
    }


    private ArrayList<Point> getAllPointsRegisteredToday(Calendar dataToSearch, User user) throws SQLException, ParseException {
        String[] interval = getHourInterval(dataToSearch);

        var statement = connection.prepareStatement("Select * from point_register where  cpf = ?" +
                " and (register BETWEEN ? AND ?)");
        statement.setString(1, user.getCpf());
        statement.setString(2, interval[0]);
        statement.setString(3, interval[1]);
        var results = statement.executeQuery();

        var pointArray = new ArrayList<Point>();
        System.out.println(results.next());
        while (results.next()){

            Point pointFromQuery = getPoint(results);
            System.out.println(pointFromQuery.toString());
            pointArray.add(pointFromQuery);

        }
        return pointArray ;
    }

    private Point getPoint(ResultSet results) throws SQLException, ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-DD HH:mm:ss");

        var id = results.getString("id");
        var userId = results.getString("userId");
        var name = results.getString("name");
        var cpf = results.getString("cpf");
        var valid = results.getString("valid");
        var status = results.getString("status");
        var register = sdf.parse(results.getString("register"));
        var user = new User(userId,name,cpf);
        Calendar cal = Calendar.getInstance();
        cal.setTime(register);
        var pointFromQuery = new Point(id,user,cal,Validation.findEnumByValue(valid),PointStatus.findEnumByValue(status));
        return pointFromQuery;
    }

    private void parse(ConsumerRecord<String, Message<Point>> record) throws SQLException, ParseException, ExecutionException, InterruptedException {
        var point = record.value().getPayload();
        var arrayPoint = getAllPointsRegisteredToday(point.getDatePoint(),point.getUser());
        var dispacher = new KafkaDispatcher<ArrayList<Point>>(
                "PONTO_ALL_POINTS_OF_USER",
                Map.of()
        );
        dispacher.send(point.getUser().getCpf(),
                UUID.randomUUID().toString(),
                ArrayList.class.getName(),
                arrayPoint
                );
    }
}
