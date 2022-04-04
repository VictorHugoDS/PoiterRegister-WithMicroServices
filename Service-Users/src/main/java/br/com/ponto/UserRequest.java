package br.com.ponto;

public class UserRequest {

    private final Long id;
    private final String cpf;

    UserRequest(Long id, String cpf) {
        this.id = id;
        this.cpf = cpf;
    }

    public Long getId() {
        return id;
    }

    public String getCpf() {
        return cpf;
    }
}
