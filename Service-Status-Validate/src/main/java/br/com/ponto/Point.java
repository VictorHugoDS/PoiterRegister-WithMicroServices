package br.com.ponto;

import java.util.Calendar;

public class Point {

    private final Validation validation;
    private PointStatus pointStatus;
    private final String id;
    private final User user;
    private final Calendar datePoint;

    public Point(String id, User user, Calendar datePoint) {
        this.id = id;
        this.user = user;
        this.datePoint = datePoint;
        validation = null;
        pointStatus = null;
    }

    public Point(String id, User user, Calendar datePoint, Validation validation, PointStatus pointStatus) {
        this.id = id;
        this.user = user;
        this.datePoint = datePoint;
        this.validation = validation;
        this.pointStatus =pointStatus;
    }

    @Override
    public String toString() {
        return "Point{" +
                "validation=" + validation +
                ", pointStatus=" + pointStatus +
                ", id='" + id + '\'' +
                ", user=" + user +
                ", datePoint=" + datePoint +
                '}';
    }

    public User getUser() {
        return user;
    }

    public Validation getValidation() {
        return validation;
    }

    public Calendar getDatePoint() {
        return datePoint;
    }

    public PointStatus getPointStatus() {
        return pointStatus;
    }

    public String getId() {
        return id;
    }

    public void setPointStatus(PointStatus pointStatus) {
        this.pointStatus = pointStatus;
    }
}
