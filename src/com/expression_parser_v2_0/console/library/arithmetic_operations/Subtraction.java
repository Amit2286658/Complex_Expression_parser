package com.expression_parser_v2_0.console.library.arithmetic_operations;

import com.expression_parser_v2_0.console.core.NumberName;
import com.expression_parser_v2_0.console.core.types.ComplexNumber;
import com.expression_parser_v2_0.console.library.Operations_Implementation;

import static com.expression_parser_v2_0.console.core.CONSTANTS.*;

public class Subtraction extends Operations_Implementation {

    int resultFlag;
    ComplexNumber complexResult;
    double realResult;
    double iotaResult;

    public Subtraction(NumberName numberName) {
        super(numberName);
    }

    @Override
    public String[] getOperationNames() {
        return new String[]{"Minus", "Subtract"};
    }

    @Override
    public char getOperator() {
        return '-';
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
    public ComplexNumber getComplex() {
        return complexResult;
    }

    @Override
    public void function(double d1, double d2, int iotaStatus) {
        switch(iotaStatus){
            case IOTA_BOTH :
                iotaResult = d1 - d2;
                resultFlag = IOTA;
                break;
            case IOTA_NONE :
                realResult = d1 - d2;
                resultFlag = REAL;
                break;
            case IOTA_FIRST :
                complexResult = new ComplexNumber();
                complexResult.real = d2 * -1;
                complexResult.iota = d1;
                resultFlag = COMPLEX;
                break;
            case IOTA_SECOND :
                complexResult = new ComplexNumber();
                complexResult.real = d1;
                complexResult.iota = d2 * -1;
                resultFlag = COMPLEX;
                break;
        }
    }

    @Override
    public void function(ComplexNumber c1, double d2, int iotaStatus) {
        switch(iotaStatus) {
            case IOTA_TRUE :
                c1.iota -= d2;
                complexResult = c1;
                resultFlag = COMPLEX;
                break;
            case IOTA_FALSE :
                c1.real -= d2;
                complexResult = c1;
                resultFlag = COMPLEX;
                break;
        }
    }

    @Override
    public void function(double d1, ComplexNumber c2, int iotaStatus) {
        switch(iotaStatus) {
            case IOTA_TRUE :
                c2.iota = d1 - c2.iota;
                complexResult = c2;
                resultFlag = COMPLEX;
                break;
            case IOTA_FALSE :
                c2.real = d1 - c2.real;
                complexResult = c2;
                resultFlag = COMPLEX;
                break;
        }
    }

    @Override
    public void function(ComplexNumber c1, ComplexNumber c2) {
        c2.real = c1.real - c2.real;
        c2.iota = c1.iota - c2.iota;
        complexResult = c2;
        resultFlag = COMPLEX;
    }
}
