package br.com.ponto;

import java.util.Calendar;

public class UserRequest {

    private final String name;
    private final String cpf;
    private final Calendar date;

    public UserRequest(String name, String cpf) {
        this.name = name;
        this.cpf = cpf;
        date = Calendar.getInstance();
    }
}
