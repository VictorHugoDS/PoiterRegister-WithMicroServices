package br.com.ponto.producer;

import br.com.ponto.messageThings.Message;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class KafkaDispatcher<T> {

    private final String simpleName;
    private final String topic;
    private final KafkaProducer<String, Message<T>> producer;

    public KafkaDispatcher(String topic, Map<String, String> otherProperties, String simpleName) {
        this.topic = topic;
        producer = new KafkaProducer<>(properties(otherProperties));
        this.simpleName = simpleName;
    }

    public void send(String key, String id, String type, T payload) throws InterruptedException, ExecutionException {

        var message = new Message<>(type,id,payload);
        message.concatenateWithId(topic,simpleName);
        var record = new ProducerRecord<>(topic,
                key,
                message);
        Callback callback = (data, ex) -> {
            if(ex != null) {
                ex.printStackTrace();
                return;
            }
            System.out.println("Success sending " + data.topic() + ":::partition " + data.partition() + "/ offset "  + data.offset() + "/ timestamp " + data.timestamp());
        };
        producer.send(record,callback).get();
    }

    public static Properties properties(Map<String, String> otherProperties) {
        var properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, GsonSerializer.class.getName());
        properties.setProperty(ProducerConfig.ACKS_CONFIG, "all");
        properties.putAll(otherProperties);
        return properties;
    }

    public void close() {
        producer.close();
    }
}
