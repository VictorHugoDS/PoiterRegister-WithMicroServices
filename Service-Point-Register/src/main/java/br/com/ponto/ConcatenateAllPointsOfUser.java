package br.com.ponto;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.io.IOException;
import java.sql.*;
import java.util.Calendar;

public class ConcatenateAllPointsOfUser {

    private Connection connection;
    private final String tableName = "point_register";

    public ConcatenateAllPointsOfUser() {
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

    public static void main(String[] args) throws SQLException {
        var concatenateAllPointsOfUser = new ConcatenateAllPointsOfUser();
        concatenateAllPointsOfUser.verifyQuantityOfPoints(null);
    }

    private String[] getHourInterval(Point point){
        //Calendar cal = point.getDatePoint();
        Calendar cal = Calendar.getInstance();
        String date = new Date(cal.getTime().getTime()).toString();
        return new String[]{date + " 00:00:00", date + "23:59:59"};
    }

    private int verifyQuantityOfPoints(Point point) throws SQLException {
        String[] interval = getHourInterval(point);
        var statement = connection.prepareStatement("Select * from "+tableName+"  where" +
                "(register BETWEEN '"+interval[0]+"' AND '"+interval[1]+"')");
        var results = statement.executeQuery();
        System.out.println(results.toString());
        return 1;
    }

    private static void parse(ConsumerRecord<String, Message<Point>> record) throws IOException {
        var point = record.value().getPayload();

    }
}
