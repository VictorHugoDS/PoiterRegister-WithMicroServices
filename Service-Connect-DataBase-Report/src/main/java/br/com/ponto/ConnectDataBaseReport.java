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

public class ConnectDataBaseReport {

    public static void main(String[] args) {
        var connect = new ConnectDataBaseReport();


        var kafkaServiceExecute = new KafkaServiceExecute<>(
                "PONTO_USER_READY_FOR_REPORT",
                connect::parse,
                ConnectDataBaseReport.class.getSimpleName(),
                Map.of()
        );
        kafkaServiceExecute.run();
    }

    private void parse(ConsumerRecord<String, Message<User>> record) throws ExecutionException, InterruptedException, SQLException, ParseException {
        var user = record.value().getPayload();
        var request = new DatabaseRequest<>(
                TypesOfRequest.SELECT_ALL,
                "PONTO_GENERATE_REPORT",
                new Point(null,user, Calendar.getInstance()));
        var kafkaDispatcher = new KafkaDispatcher<DatabaseRequest<Point>>("PONTO_POINT_DATABASE_REQUEST",Map.of(),ConnectDataBaseReport.class.getSimpleName());
        kafkaDispatcher.send(
                user.getCpf(),
                UUID.randomUUID().toString(),
                request.getClass().getName(),
                request
        );
        System.out.println("Enviando para PONTO_POINT_DATABASE_REQUEST");
    }
}
