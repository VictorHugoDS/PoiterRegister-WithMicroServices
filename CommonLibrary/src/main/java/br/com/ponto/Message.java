package br.com.ponto;

public class Message<T> {

    private final String type;
    private final String id;
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
}
