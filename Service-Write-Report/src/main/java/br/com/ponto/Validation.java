package br.com.ponto;

public enum Validation {

    VALID("V"),INVALID("I"),PENDING("P");

    public String validationValue;
    Validation(String value) {
        validationValue = value;
    }

    public String getValidationValue() {
        return validationValue;
    }

    public static Validation findEnumByValue(String value){
        for(Validation v : values()){
            if( v.validationValue.equals(value)){
                return v;
            }
        }
        return null;
    }


}
