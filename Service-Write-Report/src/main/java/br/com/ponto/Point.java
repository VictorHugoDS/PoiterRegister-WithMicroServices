package br.com.ponto;

import java.util.Calendar;

public class Point {

    private String id;
    private User user;
    private Calendar datePoint;
    private Validation validation;

    public Point(String id, User user, Calendar datePoint) {
        this.id = id;
        this.user = user;
        this.datePoint = datePoint;
        validation = Validation.PENDING;
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

    public Validation getValidation() {
        return validation;
    }

    public User getUser() {
        return user;
    }

    public void setValidation(Validation validation) {
        this.validation = validation;
    }

    public Calendar getDatePoint() {
        return datePoint;
    }
}
