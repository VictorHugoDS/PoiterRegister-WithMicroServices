package br.com.ponto;

public class User {

    private final String id;
    private final String name;
    private final String cpf;

    User(String id, String name, String cpf){
        this.id = id;
        this.name = name;
        this.cpf = cpf;
    }

    public String getCpf() {
        return cpf;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", cpf='" + cpf + '\'' +
                '}';
    }
}
