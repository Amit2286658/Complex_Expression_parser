package com.expression_parser_v2_0.console.library.boolean_operations;

import com.expression_parser_v2_0.console.library.Operations_Implementation;

import static com.expression_parser_v2_0.console.core.CONSTANTS.*;

import com.expression_parser_v2_0.console.core.NumberName;

public class equal_to extends Operations_Implementation {
    double result;
    
    public equal_to(NumberName numberName){
        super(numberName);
    }

    public equal_to() {
        super();
    }

    @Override
    public String[] getOperationNames() {
        return new String[]{"is_equal_to"};
    }

    @Override
    public char getOperator() {
        return '=';
    }

    @Override
    public int getPrecedence() {
        return PRECEDENCE_MAX - 1;
    }

    @Override
    public double getReal() {
        return result;
    }

    @Override
    public int getResultFlag() {
        return REAL;
    }

    @Override
    public int getType() {
        return TYPE_BOTH;
    }

    @Override
    public void function(double d1, double d2, int iotaStatus) {
        switch (iotaStatus){
            case IOTA_BOTH -> {
                result = d1 == d2 ? 1 : 0;
            }
            case IOTA_NONE -> {
                result = d1 == d2 ? 1 : 0;
            }
            default -> 
                result = 0;
        }
    }
}
