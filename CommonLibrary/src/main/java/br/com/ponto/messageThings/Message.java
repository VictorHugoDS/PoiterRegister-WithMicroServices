package br.com.ponto.messageThings;

public class Message<T> {

    private final String type;
    private String id;
    private final T payload;

    public Message(String type, String id, T payload) {
        this.type = type;
        this.id = id;
        this.payload = payload;
    }

    public String getId() {
        return id;
    }

    public T getPayload() {
        return payload;
    }

    public String getType() {
        return type;
    }

    public void concatenateWithId(String topic, String classSender){
        id = ", classSender: "+classSender+", to-topic: "+topic + ": " + id;
    }
}
