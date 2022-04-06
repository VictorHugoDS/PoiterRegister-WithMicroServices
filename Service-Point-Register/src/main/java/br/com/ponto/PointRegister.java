package br.com.ponto;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.io.Closeable;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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

        try {
            connection.createStatement().execute("create table "+tableName+" (" +
                    "id varchar(200) primary key," +
                    "userId varchar(200)," +
                    "name varchar(200)," +
                    "cpf varchar(200)" +
                    "register datetime" +
                    ")");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        var pointerRegister = new PointRegister();
        var kafkaServiceExecute = new KafkaServiceExecute<>(
                "PONTO_NEW_POINT_REQUEST_JUST_APPEARED",
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

    public Point insertPointerRegister(User user) throws SQLException {
        var statement = connection.prepareStatement("Insert into "+tableName+" " +
                "(id,userId,name,cpf,register)" +
                "values (?,?,?,?,?)");
        var id = UUID.randomUUID().toString();
        statement.setString(1, id);
        statement.setString(2, user.getId());
        statement.setString(3, user.getName());
        statement.setString(4, user.getCpf());
        statement.setString(5, formatData());

        var point = new Point(id, user, exactlyCreationTime);
        statement.execute();
        System.out.println(point);
        System.out.println("A new Pointer Register was created!");
        return point;
    }


    private Calendar getLastPointTimeRegister(User user) throws SQLException, ParseException {
        var statement = connection.prepareStatement(
                "SELECT register FROM  point_register WHERE cpf = ? ORDER BY register DESC LIMIT 1");
        statement.setString(1,user.getCpf());
        var result = statement.executeQuery();
        SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-DD HH:mm:ss");


        if(result.next()){
            var register = sdf.parse(result.getString("register"));
            Calendar cal = Calendar.getInstance();
            cal.setTime(register);
            return cal;
        }
        return null;

    }

    private long minutesDifference (Calendar after, Calendar before){
        long milliseconds1 = before.getTimeInMillis();
        long milliseconds2 = after.getTimeInMillis();
        long diff = milliseconds2 - milliseconds1;
        return diff / (60 * 1000);
    }

    private Boolean requestIntervalValid(User user) throws SQLException, ParseException {
        Calendar cal = getLastPointTimeRegister(user);
        if(cal == null){
            return true;
        }
        long difference = minutesDifference(cal,Calendar.getInstance());
        //return difference > 15;
        //TO DO: em função do sistema ainda estar sendo construido
        return true;
    }


    private void parse(ConsumerRecord<String, Message<User>> record) throws ExecutionException, InterruptedException, SQLException, ParseException {
        var user = record.value().getPayload();
        System.out.println(requestIntervalValid(user));
        if(requestIntervalValid(user)){
            var point = insertPointerRegister(user);
            var kafkaDispatcher = new KafkaDispatcher<Point>("PONTO_POINT_REGISTER",Map.of());

            kafkaDispatcher.send(
                    point.getUser().getCpf(),
                    UUID.randomUUID().toString(),
                    point.getClass().getName(),
                    point
            );
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
