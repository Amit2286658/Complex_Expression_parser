package com.expression_parser_v2_0.console.library.constant_operations;

import com.expression_parser_v2_0.console.library.Operations_Implementation;
import static com.expression_parser_v2_0.console.core.CONSTANTS.*;

import com.expression_parser_v2_0.console.core.NumberName;

public class pi extends Operations_Implementation {
    double result;

    public pi(NumberName numberName){
        super(numberName);
    }

    public pi() {
        super();
    }

    @Override
    public String[] getOperationNames() {
        return new String[]{"pi", "PI"};
    }

    @Override
    public char getOperator() {
        return 'π';
    }

    @Override
    public int getResultFlag() {
        return REAL;
    }

    @Override
    public double getReal() {
        return result;
    }

    @Override
    public int getPrecedence() {
        return PRECEDENCE_MAX;
    }

    @Override
    public int getType() {
        return TYPE_CONSTANT;
    }

    @Override
    public void function() {
        result = Math.PI;
    }
}
