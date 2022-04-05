package br.com.ponto;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class AdvancedMessageAdapter implements JsonDeserializer<Message> {


    private Type fooType;
    private String payloadType;

    @Override
    public Message deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
        var obj = jsonElement.getAsJsonObject();
        payloadType = obj.get("type").getAsString();
        var id = obj.get("id").getAsString();
        Object payload = null;
        payload = context.deserialize(obj.get("payload"),fooType);
        return new Message(payloadType,id,payload);
    }

    public AdvancedMessageAdapter(String className){
        try {
            this.fooType = TypeToken.getParameterized(Class.forName(className),Class.forName(payloadType)).getType();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("It was not possible to deserializer the message with the types given");
        }

    }
}
