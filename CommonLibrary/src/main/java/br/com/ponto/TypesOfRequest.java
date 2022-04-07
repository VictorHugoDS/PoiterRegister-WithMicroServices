package br.com.ponto;

public enum TypesOfRequest {
    INSERT(1),SELECT(2),UPDATE(3),SELECT_ALL(4);

    public int validationValue;
    TypesOfRequest(int value) {
        validationValue = value;
    }

    public int getValidationValue() {
        return validationValue;
    }

    public static TypesOfRequest findEnumByValue(int value){
        for(TypesOfRequest v : values()){
            if( v.getValidationValue() == value){
                return v;
            }
        }
        return null;
    }
}
