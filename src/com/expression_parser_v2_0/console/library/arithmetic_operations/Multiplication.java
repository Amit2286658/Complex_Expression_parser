package com.expression_parser_v2_0.console.library.arithmetic_operations;

import com.expression_parser_v2_0.console.core.ExpressionException;
import com.expression_parser_v2_0.console.core.NumberName;
import com.expression_parser_v2_0.console.core.types.Set;
import com.expression_parser_v2_0.console.core.types.ComplexNumber;
import com.expression_parser_v2_0.console.library.Operations_Implementation;

import static com.expression_parser_v2_0.console.core.CONSTANTS.*;

public class Multiplication extends Operations_Implementation {

    int resultFlag;
    ComplexNumber complexResult;
    double realResult;
    double iotaResult;

    public Multiplication(NumberName numberName) {
        super(numberName);
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
                resultFlag = REAL;
            }
            case IOTA_NONE -> {
                realResult = d1 * d2;
                resultFlag = REAL;
            }
            case IOTA_FIRST, IOTA_SECOND -> {
                iotaResult = d1 * d2;
                resultFlag = IOTA;
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
                resultFlag = COMPLEX;
            }
            case IOTA_TRUE -> {
                c1.iota *= d2;
                c1.real *= d2 * -1;
                complexResult = c1;
                resultFlag = COMPLEX;
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
                resultFlag = COMPLEX;
            }
            case IOTA_TRUE -> {
                c2.iota *= d1;
                c2.real *= d1 * -1;
                complexResult = c2;
                resultFlag = COMPLEX;
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
        resultFlag = COMPLEX;
    }

    @Override
    public void function() {
        throw new ExpressionException("type requires two operands, however none were found");
    }

    @Override
    public void function(double d, int iotaStatus) {
        throw new ExpressionException("type requires two operands, however only one was found");
    }

    @Override
    public void function(ComplexNumber cn) {
        throw new ExpressionException("type requires two operands, however only one was found");
    }

    @Override
    public void function(Set s) {
        throw new ExpressionException("type requires two operands, however only one was found");
    }

    @Override
    public void function(Set s1, double d1, int iotaStatus) {
        super.function(s1, d1, iotaStatus);
    }

    @Override
    public void function(double d1, Set s2, int iotaStatus) {
        super.function(d1, s2, iotaStatus);
    }

    @Override
    public void function(Set s1, Set s2) {
        super.function(s1, s2);
    }

    @Override
    public void function(String str) {
        throw new ExpressionException("type requires two operands, however only one was found");
    }

    @Override
    public void function(String str1, double d2, int iotaStatus) {
        super.function(str1, d2, iotaStatus);
    }

    @Override
    public void function(double d1, String str2, int iotaStatus) {
        super.function(d1, str2, iotaStatus);
    }

    @Override
    public void function(String str1, String str2) {
        super.function(str1, str2);
    }

    @Override
    public void function(ComplexNumber c1, Set s2) {
        super.function(c1, s2);
    }

    @Override
    public void function(Set s1, ComplexNumber c2) {
        super.function(s1, c2);
    }

    @Override
    public void function(ComplexNumber c1, String str2) {
        super.function(c1, str2);
    }

    @Override
    public void function(String str2, ComplexNumber c2) {
        super.function(str2, c2);
    }

    @Override
    public void function(Set s1, String str2) {
        super.function(s1, str2);
    }

    @Override
    public void function(String str1, Set s1) {
        super.function(str1, s1);
    }
}
