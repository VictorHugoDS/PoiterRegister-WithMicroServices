import java.util.Calendar;

public class Point {

    private String id;
    private String userName;
    private String userCPF;
    private Calendar datePoint;

    public Point(String id, String userName, String userCPF, Calendar datePoint) {
        this.id = id;
        this.userName = userName;
        this.userCPF = userCPF;
        this.datePoint = datePoint;
    }

    public String getUserCPF() {
        return userCPF;
    }
}
