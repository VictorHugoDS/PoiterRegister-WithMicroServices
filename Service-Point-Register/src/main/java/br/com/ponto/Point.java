package br.com.ponto;

import java.util.Calendar;

public class Point {

    private String id;
    private User user;
    private Calendar datePoint;

    public Point(String id, User user, Calendar datePoint) {
        this.id = id;
        this.user = user;
        this.datePoint = datePoint;
    }

    public User getUser() {
        return user;
    }

    public Calendar getDatePoint() {
        return datePoint;
    }
}
