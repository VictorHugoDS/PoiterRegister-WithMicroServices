package br.com.ponto;

import br.com.ponto.consumer.KafkaServiceExecute;
import br.com.ponto.databaseThings.DatabaseRequest;
import br.com.ponto.databaseThings.TypesOfRequest;
import br.com.ponto.messageThings.Message;
import br.com.ponto.producer.KafkaDispatcher;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.util.*;
import java.util.concurrent.ExecutionException;

public class ConnectRegisterToValidator {

    public static void main(String[] args) {
        var connect = new ConnectRegisterToValidator();
        var kafkaServiceExecute = new KafkaServiceExecute<>(
                "PONTO_POINT_REGISTERED",
                connect::parse,
                ConnectRegisterToValidator.class.getSimpleName(),
                Map.of()
        );
        kafkaServiceExecute.run();
    }

    private void parse(ConsumerRecord<String, Message<Point>> record) throws ExecutionException, InterruptedException {
        var point = record.value().getPayload();
        var request = new DatabaseRequest<>(
                TypesOfRequest.SELECT_ALL,
                "PONTO_ALL_POINTS_OF_USER",
                point);
        var kafkaDispatcher = new KafkaDispatcher<DatabaseRequest<Point>>("PONTO_POINT_DATABASE_REQUEST",Map.of(),ConnectRegisterToValidator.class.getSimpleName());
        kafkaDispatcher.send(
                point.getUser().getCpf(),
                UUID.randomUUID().toString(),
                request.getClass().getName(),
                request
        );
        System.out.println("Point list sent");
    }
}
