package br.com.ponto.consumer;

import br.com.ponto.producer.KafkaDispatcher;
import br.com.ponto.messageThings.Message;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ExecutionException;

public class KafkaServiceExecute<T> {

    private final String topic;
    private final ExecutableFunction<T> parse;
    private final KafkaConsumer<String, Message<T>> consumer;
    private final String simpleName;

    public KafkaServiceExecute(String topic, ExecutableFunction<T> parse, String simpleName, Map<String, String> otherProperties) {
        this.topic = topic;
        this.parse = parse;
        this.consumer = new KafkaConsumer<>(properties(simpleName,otherProperties));
        this.simpleName = simpleName;
    }


    public void run() {
        consumer.subscribe(Collections.singletonList(topic));
        while(true){
            var records = consumer.poll(Duration.ofMillis(100));
            if(records.count()!=0){
                for (var record: records ) {
                    try {
                        parse.consume(record);
                    } catch (Exception e) {
                        e.printStackTrace();
                        var kafkaDispatcher = new KafkaDispatcher<String>("PONTO_DEAD_LETTER",Collections.emptyMap(),simpleName);
                        var id =UUID.randomUUID().toString();
                        String payload = topic + " " + id;
                        try {
                            kafkaDispatcher.send(topic,id,String.class.getName(),payload);
                        } catch (InterruptedException | ExecutionException error) {
                            throw new RuntimeException("Couldn't send a message to DEAD-LETTER topic");
                        }
                    }
                }
            }
        }
    }


    private Properties properties(String simpleName, Map<String, String> map){
        var properties = new Properties();
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, GsonDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, simpleName);
        properties.setProperty(ConsumerConfig.CLIENT_ID_CONFIG, UUID.randomUUID().toString());
        properties.setProperty(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "1");
        properties.putAll(map);
        return properties;
    }
}
