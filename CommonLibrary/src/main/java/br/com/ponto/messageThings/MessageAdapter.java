package br.com.ponto.messageThings;

import com.google.gson.*;

import java.lang.reflect.Type;


public class MessageAdapter implements JsonSerializer<Message>, JsonDeserializer<Message> {

    @Override
    public Message deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
        var obj = jsonElement.getAsJsonObject();
        var payloadType = obj.get("type").getAsString();
        var id = obj.get("id").getAsString();
        Object payload = null;
        try {
            payload = context.deserialize(obj.get("payload"),Class.forName(payloadType));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Its was not possible to convert from the type given");
        }
        return new Message(payloadType,id,payload);
        // var message = new Message<>();
    }

    @Override
    public JsonElement serialize(Message message, Type type, JsonSerializationContext context) {
        JsonObject obj = new JsonObject();
        obj.add("id",context.serialize(message.getId()));
        obj.add("payload",context.serialize(message.getPayload()));
        obj.addProperty("type",message.getPayload().getClass().getName());
        return obj;
    }
}
