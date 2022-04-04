package br.com.ponto;

public enum PointStatus {

    OK("O"),LATE("L"),EARLY("A"),PENDING("P");

    public String pointStatus;
    PointStatus(String value) {
        pointStatus = value;
    }

    public String getPointStatus() {
        return pointStatus;
    }
}
