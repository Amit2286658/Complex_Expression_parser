package com.expression_parser_v2_0.console.library.arithmetic_operations;

import com.expression_parser_v2_0.console.core.ComplexNumber;
import com.expression_parser_v2_0.console.core.NumberName;
import com.expression_parser_v2_0.console.library.Operations_Implementation;

import static com.expression_parser_v2_0.console.core.CONSTANTS.*;

public class Sqrt extends Operations_Implementation {

    int resultFlag;
    double realResult;
    double iotaResult;

    ComplexNumber complexResult;

    public Sqrt(NumberName numberName) {
        super(numberName);
    }

    @Override
    public String[] getOperationNames() {
        return new String[]{"Sqrt", "Square_root"};
    }

    @Override
    public char getOperator() {
        return '√';
    }

    @Override
    public int getPrecedence() {
        return PRECEDENCE_MAX;
    }

    @Override
    public int getType() {
        return TYPE_POST;
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
    public double getIota() {
        return iotaResult;
    }

    @Override
    public ComplexNumber getComplex() {
        return complexResult;
    }

    @Override
    public void function(double d, int iotaStatus) {
        switch (iotaStatus) {
            case IOTA_FALSE -> {
                if (d < 0){
                    iotaResult = Math.sqrt(d * -1);
                    resultFlag = RESULT_IOTA;
                } else {
                    realResult = Math.sqrt(d);
                    resultFlag = RESULT_REAL;
                }
            }
            case IOTA_TRUE -> {
                complexResult = new ComplexNumber();
                double sqrt = Math.sqrt(d / 2 * (d < 0 ? -1 : 1));
                complexResult.real = sqrt;
                complexResult.iota = sqrt * (d < 0 ? -1 : 1);
                resultFlag = RESULT_COMPLEX;
            }
        }
    }
}
