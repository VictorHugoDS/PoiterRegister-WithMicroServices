package br.com.ponto;

import br.com.ponto.consumer.GsonAdvancedDeserializer;
import br.com.ponto.consumer.KafkaServiceExecute;
import br.com.ponto.messageThings.Message;
import com.google.gson.Gson;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ExecutionException;

public class GenerateReport {

    public static void main(String[] args) {
        var report = new GenerateReport();
        Map<String,String> map = new HashMap<>();
        map.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, GsonAdvancedDeserializer.class.getName());
        map.put(GsonAdvancedDeserializer.ADVANCED_SERIALIZER_UPPER_CLASS, ArrayList.class.getName());
        map.put(GsonAdvancedDeserializer.ADVANCED_SERIALIZER_SUB_CLASS, Point.class.getName());
        var kafkaService = new KafkaServiceExecute<>(
                "PONTO_GENERATE_REPORT",
                report::parse,
                GenerateReport.class.getSimpleName(),
                map
        );
        kafkaService.run();
    }

    private void parse(ConsumerRecord<String, Message<ArrayList<Point>>> record) throws IOException {

        List<Point> pointList = record.value().getPayload().stream().filter(p -> p.getValidation() == Validation.VALID).toList();
        User user = pointList.get(0).getUser();
        String userName = user.getName().replace(" ","-");
        String path = "src/main/java/resources/"+userName;


        Path p = Paths.get(path);

        try {
            Files.createDirectory(p);
        } catch (IOException e) {
            // it is not a problem that the file already exists
            e.printStackTrace();
        }


        Calendar cal = Calendar.getInstance();
        String nameTxt = cal.get(Calendar.YEAR) + "-" +  cal.get(Calendar.MONTH) + "-" + cal.get(Calendar.DAY_OF_MONTH) + ".txt";
        File tmp = new File(path, nameTxt);
        tmp.createNewFile();
        var file = path+"/"+nameTxt;
        Gson gson = new Gson();
        String json = gson.toJson(pointList).replace("},{","},\n{");

        Files.writeString(Paths.get(file), json);
    }
}
