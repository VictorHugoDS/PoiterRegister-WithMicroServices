package br.com.ponto;

import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.Objects;

public class Point {

    private final Validation validation;
    private final PointStatus pointStatus;
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
                ", id='" + id + '\'' +
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

    public Validation getValidation() {
        return validation;
    }

    public interface Getter {
        public String get();
    }

    public String getValidationValue() {
        return validation==null ? null : validation.getValidationValue();
    }

    public String getStatusValue() {
        return pointStatus==null ? null : pointStatus.getPointStatus();
    }


    public Calendar getDatePoint() {
        return datePoint;
    }

    public Getter getFunction(String string) throws NoSuchMethodException {
        if(Objects.equals(string, "valid")){
            return this::getValidationValue;
        } else if (Objects.equals(string, "status")){
            return this::getStatusValue;
        }
       throw new NoSuchMethodException("It was not possible to find the method with the string given");
    }
}
