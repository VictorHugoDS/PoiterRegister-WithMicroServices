package br.com.ponto.messageThings;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class AdvancedMessageAdapter implements JsonDeserializer<Message> {


    private Class<?> upperType;
    private Class<?> subType;

    @Override
    public Message deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
        try {
           var obj = jsonElement.getAsJsonObject();
            var payloadType = obj.get("type").getAsString();
            var id = obj.get("id").getAsString();
            Object payload = null;

            payload = context.deserialize(obj.get("payload"),getType(payloadType));

            return new Message(payloadType,id,payload);
        }
        catch (Exception e){
            e.printStackTrace();
            System.out.println("Error to process the payload, returning null");
            return null;
        }
    }

    private Type getType (String payloadType){
        try {
            System.out.println(upperType.toString() + " " + Class.forName(payloadType).toString());
            var type = TypeToken.getParameterized(upperType,subType);
            return  type.getType();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("Couldn't resolve the type of class given to the deserializer");
        }
    }

    public AdvancedMessageAdapter(String upperClassName, String subClassName){
        try {
            upperType = Class.forName(upperClassName);
            subType = Class.forName(subClassName);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("Couldn't resolve the type of class given to the deserializer");
        }

    }
}
