package com.expression_parser_v2_0.console.library.arithmetic_operations;

import com.expression_parser_v2_0.console.core.ComplexNumber;
import com.expression_parser_v2_0.console.core.ExpressionException;
import com.expression_parser_v2_0.console.core.NumberName;
import com.expression_parser_v2_0.console.core.Set;
import com.expression_parser_v2_0.console.library.Operations_Implementation;

import static com.expression_parser_v2_0.console.core.CONSTANTS.*;

public class Addition extends Operations_Implementation {

    int resultFlag;
    ComplexNumber complexResult;
    double realResult;
    double iotaResult;
    String resultString;
    Set resultSet;

    public Addition(NumberName numberName) {
        super(numberName);
    }

    @Override
    public String[] getOperationNames() {
        return new String[]{"Add", "add", "ADD", "Plus"};
    }

    @Override
    public char getOperator() {
        return '+';
    }

    @Override
    public int getPrecedence() {
        return PRECEDENCE_LEAST;
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
    public String getString() {
        return resultString;
    }

    @Override
    public ComplexNumber getComplex() {
        return complexResult;
    }

    @Override
    public Set getSet() {
        return resultSet;
    }

    @Override
    public void function(double d1, double d2, int iotaStatus) {
        switch (iotaStatus) {
            case IOTA_BOTH -> {
                iotaResult = d1 + d2;
                resultFlag = RESULT_IOTA;
            }
            case IOTA_NONE -> {
                realResult = d1 + d2;
                resultFlag = RESULT_REAL;
            }
            case IOTA_FIRST -> {
                complexResult = new ComplexNumber();
                complexResult.real = d2;
                complexResult.iota = d1;
                resultFlag = RESULT_COMPLEX;
            }
            case IOTA_SECOND -> {
                complexResult = new ComplexNumber();
                complexResult.real = d1;
                complexResult.iota = d2;
                resultFlag = RESULT_COMPLEX;
            }
        }
    }

    @Override
    public void function(ComplexNumber c1, double d2, int iotaStatus) {
        switch (iotaStatus) {
            case IOTA_TRUE -> {
                c1.iota += d2;
                complexResult = c1;
                resultFlag = RESULT_COMPLEX;
            }
            case IOTA_FALSE -> {
                c1.real += d2;
                complexResult = c1;
                resultFlag = RESULT_COMPLEX;
            }
        }
    }

    @Override
    public void function(double d1, ComplexNumber c2, int iotaStatus) {
        switch (iotaStatus) {
            case IOTA_TRUE -> {
                c2.iota += d1;
                complexResult = c2;
                resultFlag = RESULT_COMPLEX;
            }
            case IOTA_FALSE -> {
                c2.real += d1;
                complexResult = c2;
                resultFlag = RESULT_COMPLEX;
            }
        }
    }

    @Override
    public void function(ComplexNumber c1, ComplexNumber c2) {
        c2.real += c1.real;
        c2.iota += c1.iota;
        complexResult = c2;
        resultFlag = RESULT_COMPLEX;
    }

    @Override
    public void function(String str1, String str2) {
        try {
            double d1 = myNumberName.convertNameToNumber(str1);
            double d2 = myNumberName.convertNameToNumber(str2);
            double d3 = d1 + d2;
            resultFlag = RESULT_REAL;
            realResult = d3;
            return;
        }catch (Exception e){
            //empty
        }
        resultString = str1 + str2;
        resultFlag = RESULT_STRING;
    }

    @Override
    public void function(Set s1, double d2, int iotaStatus) {
        if (iotaStatus == IOTA_TRUE){
            s1.pushIota(d2);
        } else {
            s1.pushReal(d2);
        }
        resultSet = s1;
        resultFlag = RESULT_SET;
    }

    @Override
    public void function(double d1, Set s2, int iotaStatus) {
        if (iotaStatus == IOTA_TRUE){
            s2.pushIota(d1);
        } else {
            s2.pushReal(d1);
        }
        resultSet = s2;
        resultFlag = RESULT_SET;
    }

    @Override
    public void function(Set s1, Set s2) {
        s1.pushSet(s2);
        resultSet = s1;
        resultFlag = RESULT_SET;
    }

    @Override
    public void function(String str1, double d2, int iotaStatus) {
        str1 += d2 + (iotaStatus == IOTA_TRUE ? "i" : "");
        resultString = str1;
        resultFlag = RESULT_STRING;
    }

    @Override
    public void function(double d1, String str2, int iotaStatus) {
        resultString = d1 + (iotaStatus == IOTA_TRUE ? "i" : "") + str2;
        resultFlag = RESULT_STRING;
    }

    @Override
    public void function(ComplexNumber c1, Set s2) {
        s2.pushComplex(c1);
        resultSet = s2;
        resultFlag = RESULT_SET;
    }

    @Override
    public void function(Set s1, ComplexNumber c2) {
        s1.pushComplex(c2);
        resultSet = s1;
        resultFlag = RESULT_SET;
    }

    @Override
    public void function(ComplexNumber c1, String str2) {
        throw new ExpressionException("no meaningful operands");
    }

    @Override
    public void function(String str2, ComplexNumber c2) {
        throw new ExpressionException("no meaningful operands");
    }

    @Override
    public void function(Set s1, String str2) {
        s1.pushString(str2);
        resultSet = s1;
        resultFlag = RESULT_SET;
    }

    @Override
    public void function(String str1, Set s2) {
        s2.pushString(str1);
        resultSet = s2;
        resultFlag = RESULT_SET;
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
    public void function(String str) {
        throw new ExpressionException("type requires two operands, however only one was found");
    }
}
