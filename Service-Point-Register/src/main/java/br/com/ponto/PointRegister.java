package br.com.ponto;

import br.com.ponto.consumer.KafkaServiceExecute;
import br.com.ponto.databaseThings.DatabaseRequest;
import br.com.ponto.databaseThings.TypesOfRequest;
import br.com.ponto.messageThings.Message;
import br.com.ponto.producer.KafkaDispatcher;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class PointRegister {

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

    private long minutesDifference (Calendar after, Calendar before){
        long milliseconds1 = before.getTimeInMillis();
        long milliseconds2 = after.getTimeInMillis();
        long diff = milliseconds2 - milliseconds1;
        return diff / (60 * 1000);
    }


    private Boolean requestIntervalValid(Point point) throws SQLException, ParseException {
        Calendar cal = point.getDatePoint();
        if(cal == null){
            return true;
        }
        long difference = minutesDifference(cal,Calendar.getInstance());
        //return difference > 15;
        //TO DO: em função do sistema ainda estar sendo construido está sempre retornando true
        return true;
    }


    private void parse(ConsumerRecord<String, Message<Point>> record) throws ExecutionException, InterruptedException, SQLException, ParseException {
        var point = record.value().getPayload();
        if(point.getId() != null && !requestIntervalValid(point)) return;
        var request = new DatabaseRequest<>(
                TypesOfRequest.INSERT,
                "PONTO_POINT_REGISTERED",
                point);
        var kafkaDispatcher = new KafkaDispatcher<DatabaseRequest<Point>>("PONTO_POINT_DATABASE_REQUEST",Map.of(),PointRegister.class.getSimpleName());
        kafkaDispatcher.send(
                point.getUser().getCpf(),
                UUID.randomUUID().toString(),
                request.getClass().getName(),
                request
        );
    }
}
