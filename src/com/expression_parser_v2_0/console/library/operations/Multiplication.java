package com.expression_parser_v2_0.console.library.operations;

import com.expression_parser_v2_0.console.core.ComplexNumber;
import com.expression_parser_v2_0.console.library.Operations_Implementation;

import static com.expression_parser_v2_0.console.core.constants.*;

public class Multiplication extends Operations_Implementation {

    int resultFlag;
    ComplexNumber complexResult;
    double realResult;
    double iotaResult;

    public Multiplication() {
        super();
    }

    @Override
    public String[] getOperationNames() {
        return new String[]{"times", "Times", "TIMES", "x", "X"};
    }

    @Override
    public char getOperator() {
        return '*';
    }

    @Override
    public int getPrecedence() {
        return PRECEDENCE_MEDIUM;
    }

    @Override
    public int getType() {
        return TYPE_BOTH;
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
    public void function(double d1, double d2, int iotaStatus) {
        switch (iotaStatus) {
            case IOTA_BOTH -> {
                realResult = d1 * d2 * -1;
                resultFlag = RESULT_REAL;
            }
            case IOTA_NONE -> {
                realResult = d1 * d2;
                resultFlag = RESULT_REAL;
            }
            case IOTA_FIRST, IOTA_SECOND -> {
                iotaResult = d1 * d2;
                resultFlag = RESULT_IOTA;
            }
        }
    }

    @Override
    public void function(ComplexNumber c1, double d2, int iotaStatus) {
        switch (iotaStatus) {
            case IOTA_FALSE -> {
                c1.real *= d2;
                c1.iota *= d2;
                complexResult = c1;
                resultFlag = RESULT_COMPLEX;
            }
            case IOTA_TRUE -> {
                c1.iota *= d2;
                c1.real *= d2 * -1;
                complexResult = c1;
                resultFlag = RESULT_COMPLEX;
            }
        }
    }

    @Override
    public void function(double d1, ComplexNumber c2, int iotaStatus) {
        switch (iotaStatus) {
            case IOTA_FALSE -> {
                c2.real *= d1;
                c2.iota *= d1;
                complexResult = c2;
                resultFlag = RESULT_COMPLEX;
            }
            case IOTA_TRUE -> {
                c2.iota *= d1;
                c2.real *= d1 * -1;
                complexResult = c2;
                resultFlag = RESULT_COMPLEX;
            }
        }
    }

    @Override
    public void function(ComplexNumber c1, ComplexNumber c2) {
        double c1r = c1.real, c1i = c1.iota, c2r = c2.real, c2i = c2.iota;
        double d1 = c1r * c2r, d2 = c1r * c2i, d3 = c1i * c2r, d4 = c1i * c2i;
        c2.real = d1 + (d4 * -1);
        c2.iota = d2 + d3;
        complexResult = c2;
        resultFlag = RESULT_COMPLEX;
    }
}
