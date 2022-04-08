package br.com.ponto;

import br.com.ponto.consumer.KafkaServiceExecute;
import br.com.ponto.databaseThings.DatabaseRequest;
import br.com.ponto.databaseThings.TypesOfRequest;
import br.com.ponto.messageThings.Message;
import br.com.ponto.producer.KafkaDispatcher;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class ConnectAuthenticationToRegister {

    public static void main(String[] args) {
        var analyser = new ConnectAuthenticationToRegister();


        var kafkaServiceExecute = new KafkaServiceExecute<>(
                "PONTO_INTERVAL_TIME_TO_ANALYSE",
                analyser::parse,
                ConnectAuthenticationToRegister.class.getSimpleName(),
                Map.of()
        );
        kafkaServiceExecute.run();
    }

    private void parse(ConsumerRecord<String, Message<User>> record) throws ExecutionException, InterruptedException, SQLException, ParseException {
        var user = record.value().getPayload();
        var request = new DatabaseRequest<>(
                TypesOfRequest.SELECT,
                "PONTO_NEW_POINT_REQUEST_JUST_APPEARED",
                new Point(null,user,null));
        var kafkaDispatcher = new KafkaDispatcher<DatabaseRequest<Point>>("PONTO_POINT_DATABASE_REQUEST",Map.of(),ConnectAuthenticationToRegister.class.getSimpleName());
        kafkaDispatcher.send(
                user.getCpf(),
                UUID.randomUUID().toString(),
                request.getClass().getName(),
                request
        );
    }
}
