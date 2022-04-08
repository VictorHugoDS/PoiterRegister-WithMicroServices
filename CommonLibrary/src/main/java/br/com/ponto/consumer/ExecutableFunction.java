package br.com.ponto.consumer;

import br.com.ponto.messageThings.Message;
import org.apache.kafka.clients.consumer.ConsumerRecord;


public interface ExecutableFunction<T> {
    public void consume(ConsumerRecord<String, Message<T>> records) throws Exception;
}
