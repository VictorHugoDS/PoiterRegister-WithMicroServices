package br.com.ponto;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.kafka.common.serialization.Deserializer;

import java.util.ArrayList;
import java.util.Map;

public class GsonAdvancedDeserializer implements Deserializer {

    public static final String ADVANCED_SERIALIZER_UPPER_CLASS = "br.com.ponto.advanced_serializer_upper";
    public static final String ADVANCED_SERIALIZER_SUB_CLASS = "br.com.ponto.advanced_serializer_sub";
    private String upper;
    private String sub;

    @Override
    public Message deserialize(String s, byte[] bytes) {
        var gson = new GsonBuilder().registerTypeAdapter(Message.class,
                new AdvancedMessageAdapter(upper,sub)).create();
        return gson.fromJson(new String(bytes), Message.class);
    }

    @Override
    public void configure(Map configs, boolean isKey) {
        upper = (String) configs.get("br.com.ponto.advanced_serializer_upper");
        sub = (String) configs.get("br.com.ponto.advanced_serializer_sub");
    }
}
