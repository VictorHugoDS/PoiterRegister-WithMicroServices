import br.com.ponto.Message;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.io.IOException;
import java.sql.SQLException;

public class ValidityDetector {
    public static void main(String[] args) {

    }

    private void parse(ConsumerRecord<String, Message<Point>> record) throws IOException {
        var point = record.value().getPayload();

    }

}
