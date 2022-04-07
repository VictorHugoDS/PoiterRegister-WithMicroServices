package br.com.ponto;

public enum PointStatus {

    OK("O"),LATE("L"),EARLY("A"),PENDING("P"),INVALID("I");

    public String pointStatus;
    PointStatus(String value) {
        pointStatus = value;
    }

    public String getPointStatus() {
        return pointStatus;
    }

    public static PointStatus findEnumByValue(String value){
        for(PointStatus v : values()){
            if( v.pointStatus.equals(value)){
                return v;
            }
        }
        return null;
    }
}
