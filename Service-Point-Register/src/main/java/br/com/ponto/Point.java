package br.com.ponto;

import java.util.Calendar;

public class Point {

    private String id;
    private User user;
    private Calendar datePoint;
    private Validation validation;
    private PointStatus status;

    public Point(String id, User user, Calendar datePoint) {
        this.id = id;
        this.user = user;
        this.datePoint = datePoint;
    }
    public Point(String id, User user, Calendar datePoint,Validation validation,PointStatus status) {
        this(id, user, datePoint);
        this.validation =validation;
        this.status = status;
    }

    @Override
    public String toString() {
        return "Point{" +
                "id='" + id + '\'' +
                ", user=" + user +
                ", datePoint=" + datePoint +
                ", validation=" + validation +
                ", status=" + status +
                '}';
    }

    public User getUser() {
        return user;
    }

    public Calendar getDatePoint() {
        return datePoint;
    }
}
