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
}
