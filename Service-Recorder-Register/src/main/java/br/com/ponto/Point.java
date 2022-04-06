package br.com.ponto;

import java.util.Calendar;

public class Point {

    private String id;
    private User user;
    private Calendar datePoint;
    private Validation validation;

    public Point(String id, User user, Calendar datePoint, Validation validation) {
        this.id = id;
        this.user = user;
        this.datePoint = datePoint;
        this.validation = validation;

    }

    @Override
    public String toString() {
        return "Point{" +
                "id='" + id + '\'' +
                ", user=" + user +
                ", datePoint=" + datePoint +
                ", validation=" + validation +
                '}';
    }


    public String getId() {
        return id;
    }


    public void setValidation(Validation validation) {
        this.validation = validation;
    }

    public Validation getValidation() {
        return validation;
    }

    public User getUser() {
        return user;
    }
}
