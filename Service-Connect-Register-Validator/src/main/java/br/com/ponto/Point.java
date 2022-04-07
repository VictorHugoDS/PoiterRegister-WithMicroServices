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

    @Override
    public String toString() {
        return "Point{" +
                "id='" + id + '\'' +
                ", user=" + user +
                ", datePoint=" + datePoint +
                '}';
    }

    public User getUser() {
        return user;
    }

    public String getId() {
        return id;
    }

    public Calendar getDatePoint() {
        return datePoint;
    }
}
