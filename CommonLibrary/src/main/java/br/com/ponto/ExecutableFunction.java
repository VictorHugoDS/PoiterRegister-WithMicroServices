package br.com.ponto;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.io.IOException;


public interface ExecutableFunction<T> {
    public void consume(ConsumerRecord<String,Message<T>> records) throws Exception;
}
