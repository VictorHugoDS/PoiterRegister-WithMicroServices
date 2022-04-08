package br.com.ponto;

import br.com.ponto.consumer.GsonAdvancedDeserializer;
import br.com.ponto.consumer.KafkaServiceExecute;
import br.com.ponto.databaseThings.DatabaseRequest;
import br.com.ponto.messageThings.Message;
import br.com.ponto.producer.KafkaDispatcher;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.sql.*;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutionException;

public class PointDataBaseManagement {
    private final String tableName = "point_register";
    private Connection connection;
    private Calendar exactlyCreationTime;
    private KafkaDispatcher<Point> kafkaDispatcher;

    PointDataBaseManagement() {

        String url = "jdbc:sqlite:users_database.db";
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
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
                    "cpf varchar(200)," +
                    "valid varchar(1)," +
                    "status varchar(1)," +
                    "register datetime" +
                    ")");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        var dataBaseManagement = new PointDataBaseManagement();

        Map<String,String> map = new HashMap<>();
        map.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, GsonAdvancedDeserializer.class.getName());
        map.put(GsonAdvancedDeserializer.ADVANCED_SERIALIZER_UPPER_CLASS, DatabaseRequest.class.getName());
        map.put(GsonAdvancedDeserializer.ADVANCED_SERIALIZER_SUB_CLASS, Point.class.getName());

        var kafkaServiceExecute = new KafkaServiceExecute<>(
                "PONTO_POINT_DATABASE_REQUEST",
                dataBaseManagement::parse,
                PointDataBaseManagement.class.getSimpleName(),
                map
        );
        kafkaServiceExecute.run();
    }

    private void parse(ConsumerRecord<String, Message<DatabaseRequest<Point>>> record) throws ExecutionException, InterruptedException, SQLException, ParseException, NoSuchMethodException {
        var request = record.value().getPayload();
        var typeRequest = request.getTypeOfRequest();
        var point = request.getPayload();
        var topic = request.getTopicToSend();
        if(topic != null){
            kafkaDispatcher = new KafkaDispatcher<>(topic,Map.of(),PointDataBaseManagement.class.getSimpleName());
        }
        switch (typeRequest){
            case INSERT -> insertPoint(point);
            case SELECT -> selectPoint(point);
            case UPDATE -> updatePoint(point,request.getOption());
            case SELECT_ALL -> selectAllBasedOnPoint(point,topic);
        }
    }

    private void updatePoint(Point point, String columnNome) throws SQLException, ExecutionException, InterruptedException, NoSuchMethodException {
        updateValidityOfPoint(point,columnNome);
        SendPoint(point);
    }

    private void selectAllBasedOnPoint(Point point,String topic) throws SQLException, ParseException, ExecutionException, InterruptedException {
        var newPoint = getAllPointsRegisteredTodayOfAUser(point.getDatePoint(),point.getUser());
        if(topic != null){
            var dispatcher = new KafkaDispatcher<ArrayList<Point>>(topic,Map.of(),PointDataBaseManagement.class.getSimpleName());
            dispatcher.send(
                    point.getUser().getCpf(),
                    UUID.randomUUID().toString(),
                    newPoint.getClass().getName(),
                    newPoint
            );
        }
    }

    private void insertPoint(Point point) throws SQLException, ExecutionException, InterruptedException {
        SendPoint(insertPointerRegister(point.getUser()));
    }

    private void selectPoint(Point point) throws SQLException, ExecutionException, InterruptedException, ParseException {
        SendPoint(getLastPoint(point.getUser()));
    }

    private void SendPoint(Point point) throws InterruptedException, ExecutionException {
        if(kafkaDispatcher!= null){
            kafkaDispatcher.send(
                    point.getUser().getCpf(),
                    UUID.randomUUID().toString(),
                    point.getClass().getName(),
                    point
            );
        }
    }

    
    
    private Point getLastPoint(User user) throws SQLException, ParseException {
        var statement = connection.prepareStatement(
                "SELECT * FROM  point_register WHERE cpf = ? ORDER BY register DESC LIMIT 1");
        statement.setString(1,user.getCpf());
        var result = statement.executeQuery();
        SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-DD HH:mm:ss");

        if(result.next()){
            return getPoint(result);
        }
        return new Point(null,user,null);
    }

    private Point getPoint(ResultSet results) throws SQLException, ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-DD HH:mm:ss");

        var id = results.getString("id");
        var userId = results.getString("userId");
        var name = results.getString("name");
        var cpf = results.getString("cpf");
        var register = sdf.parse(results.getString("register"));
        var valid = Validation.findEnumByValue(results.getString("valid"));
        var status = PointStatus.findEnumByValue(results.getString("status"));
        var user = new User(userId,name,cpf);
        Calendar cal = Calendar.getInstance();
        cal.setTime(register);
        return new Point(id,user,cal,valid,status);
    }


    private String formatData (){
        Calendar cal = Calendar.getInstance();
        exactlyCreationTime = cal;
        String date = new Date(cal.getTime().getTime()).toString();
        date += " " + String.format("%02d:%02d:%02d",
                cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE),
                cal.get(Calendar.SECOND));
        return date;
    }

    public Point insertPointerRegister(User user) throws SQLException {
        var statement = connection.prepareStatement("Insert into "+tableName+" " +
                "(id,userId,name,cpf,valid,status,register)" +
                "values (?,?,?,?,?,?,?)");
        var id = UUID.randomUUID().toString();
        statement.setString(1, id);
        statement.setString(2, user.getId());
        statement.setString(3, user.getName());
        statement.setString(4, user.getCpf());
        statement.setString(5, Validation.PENDING.getValidationValue());
        statement.setString(6, PointStatus.PENDING.getPointStatus());
        statement.setString(7, formatData());

        var point = new Point(id, user, exactlyCreationTime);
        statement.execute();
        System.out.println("A new Pointer Register was created!");
        System.out.println(point);
        return point;
    }


    private String[] getHourInterval(Calendar dataToSearch){
        String date = new Date(dataToSearch.getTime().getTime()).toString();
        return new String[]{date + " 00:00:00", date + " 23:59:59"};
    }

    private ArrayList<Point> getAllPointsRegisteredTodayOfAUser(Calendar dataToSearch, User user) throws SQLException, ParseException {
        String[] interval = getHourInterval(dataToSearch);

        var statement = connection.prepareStatement("Select * from "+tableName+" where  cpf = ?" +
                " and (register BETWEEN ? AND ?)");
        statement.setString(1, user.getCpf());
        statement.setString(2, interval[0]);
        statement.setString(3, interval[1]);
        var results = statement.executeQuery();

        var pointArray = new ArrayList<Point>();
        while (results.next()){

            Point pointFromQuery = getPoint(results);
            pointArray.add(pointFromQuery);

        }
        return pointArray ;
    }

    private void updateValidityOfPoint(Point point, String columnNome) throws SQLException, NoSuchMethodException {
        var statement = connection.prepareStatement(
                "UPDATE "+tableName+" set "+columnNome+" = ? where id = ?");
        statement.setString(1,point.getFunction(columnNome).get());
        statement.setString(2,point.getId());

        statement.execute();
    }

}
