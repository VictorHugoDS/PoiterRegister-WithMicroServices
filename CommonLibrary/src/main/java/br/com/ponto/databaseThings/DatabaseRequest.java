package br.com.ponto.databaseThings;

public class DatabaseRequest<T> {

    private TypesOfRequest typeOfRequest;
    private String topicToSend;
    private String option;
    private T payload;

    public DatabaseRequest(TypesOfRequest typesOfRequest, String topicToSend, T payload) {
        this.typeOfRequest = typesOfRequest;
        this.topicToSend = topicToSend;
        this.payload = payload;
    }
    public DatabaseRequest(TypesOfRequest typesOfRequest, String topicToSend, T payload, String option) {
        this(typesOfRequest, topicToSend, payload);
        this.option = option;
    }

    public TypesOfRequest getTypeOfRequest() {
        return typeOfRequest;
    }

    public T getPayload() {
        return payload;
    }

    public String getTopicToSend() {
        return topicToSend;
    }

    public String getOption() {
        return option;
    }
}
