package com.expression_parser_v2_0.console.library.operations;

import com.expression_parser_v2_0.console.library.Operations_Implementation;

import static com.expression_parser_v2_0.console.core.constants.*;
import static com.expression_parser_v2_0.console.library.Commons.factorial;

public class Factorial extends Operations_Implementation {
    int resultFlag;
    double realResult;

    public Factorial() {
        super();
    }

    @Override
    public char getOperator() {
        return '!';
    }

    @Override
    public int getPrecedence() {
        return PRECEDENCE_MAX;
    }

    @Override
    public int getType() {
        return TYPE_PRE;
    }

    @Override
    public int getResultFlag() {
        return resultFlag;
    }

    @Override
    public double getReal() {
        return realResult;
    }

    @Override
    public void function(double d, int iotaStatus) {
        switch(iotaStatus){
            case IOTA_FALSE -> {
                realResult = factorial((int)d);
                resultFlag = RESULT_REAL;
            }
        }
    }
}
