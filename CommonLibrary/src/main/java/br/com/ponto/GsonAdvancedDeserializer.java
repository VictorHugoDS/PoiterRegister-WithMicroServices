package br.com.ponto;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.kafka.common.serialization.Deserializer;

import java.util.ArrayList;

public class GsonAdvancedDeserializer implements Deserializer {

    public static final String ADVANCED_SERIALIZER = "br.com.ponto.advanced_serializer";

    private final Gson gson = new GsonBuilder().registerTypeAdapter(Message.class, new AdvancedMessageAdapter(ArrayList.class.getName())).create();

    @Override
    public Message deserialize(String s, byte[] bytes) {
        return gson.fromJson(new String(bytes), Message.class);
    }

}
