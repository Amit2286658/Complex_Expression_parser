package com.expression_parser_v2_0.console.library.constant_operations;

import com.expression_parser_v2_0.console.library.Operations_Implementation;
import static com.expression_parser_v2_0.console.core.CONSTANTS.*;

import com.expression_parser_v2_0.console.core.NumberName;

public class euler_constant extends Operations_Implementation {
    double result;

    public euler_constant(NumberName numberName){
        super(numberName);
    }

    public euler_constant() {
        super();
    }

    @Override
    public String[] getOperationNames() {
        return new String[]{"euler_constant"};
    }

    @Override
    public char getOperator() {
        return 'ê';
    }

    @Override
    public int getResultFlag() {
        return RESULT_REAL;
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
        result = Math.E;
    }
}
