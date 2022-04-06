package br.com.ponto;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.io.Closeable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;

public class ValidatePoint implements Closeable {

    private final String tableName = "point_validation";
    private Connection connection;

    ValidatePoint()  {
        String url = "jdbc:sqlite:users_database.db";
        try {
            connection = DriverManager.getConnection(url);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            connection.createStatement().execute("create table "+tableName+" (" +
                    "id varchar(200) primary key," +
                    "pointId varchar(200)," +
                    "PointStatus varchar(200)," +
                    "register datetime" +
                    ")");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        var validatePoint = new ValidatePoint();
        var kafkaServiceExecute = new KafkaServiceExecute<>(
                "PONTO_VALIDATED_POINT",
                validatePoint::parse,
                PointRegister.class.getSimpleName(),
                Map.of()
        );
        kafkaServiceExecute.run();
    }

    private boolean updateToValidatePoint(Point point) throws SQLException {
        var statement = connection.prepareStatement(
                "UPDATE "+tableName+" set valid = ? where id = ?");
        //statement.setString(1,Validation.VALID.getValidationValue());
        statement.setString(2,point.getId());

        return statement.execute();
    }

    private void parse(ConsumerRecord<String, Message<Point>> record) throws SQLException {
        var point = record.value().getPayload();
        if (!updateToValidatePoint(point)){
            throw new RuntimeException("It was not possible to update a point register");
        } else {
            System.out.println("Point :" + point.getId() + " was successful validate");
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
