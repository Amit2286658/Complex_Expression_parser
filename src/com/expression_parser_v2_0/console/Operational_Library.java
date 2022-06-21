package com.expression_parser_v2_0.console;

import static com.expression_parser_v2_0.console.Main.*;

public enum Operational_Library implements operationsInterface {

    MULTIPLY('*', PRECEDENCE_MEDIUM, "x, multiply"),
    ADD('+', PRECEDENCE_LEAST, "plus, add"),
    SUBTRACT('-', PRECEDENCE_LEAST, "minus, subtract"),
    FACTORIAL('!', PRECEDENCE_MEDIUM + 1, "factorial", TYPE_PRE),
    ROOT('√', PRECEDENCE_MEDIUM + 1, "root", TYPE_POST),
    NATURAL_LOG_BASE(';', PRECEDENCE_MAX, "natural_log_base, ln", TYPE_CONSTANT);


    private char operator;
    private int precedence;
    private String operationName;
    private int resultFlag;
    private double doubleResult;
    private ComplexNumber complexResult;
    private int type;

    Operational_Library(char operator, int precedence, String operationName, int type){
        this.operator = operator;
        this.precedence = precedence;
        this.operationName = operationName;
        this.type = type;
        this.complexResult = new ComplexNumber();

        registerOperation(this);
    }

    Operational_Library(char operator, int precedence, String operationName){
        this(operator, precedence, operationName, TYPE_BOTH);
    }

    @Override
    public String[] getOperationNames() {
        operationName = operationName.replaceAll("\\s+", "");
        return operationName.split(",");
    }

    @Override
    public char getOperator() {
        return operator;
    }

    @Override
    public int getPrecedence() {
        return precedence;
    }

    @Override
    public int getType() {
        return type;
    }

    @Override
    public int getResultFlag() {
        return resultFlag;
    }

    @Override
    public double getDoubleResult() {
        return doubleResult;
    }

    @Override
    public ComplexNumber getComplexResult() {
        return complexResult;
    }

    @Override
    public void function() {
        switch(operator){
            case ';' :
                doubleResult = 100;
                resultFlag = RESULT_REAL;
        }
    }

    @Override
    public void function(double d, int iotaStatus) {
        switch(operator){
            case '√' :
                if (iotaStatus == IOTA_FALSE){
                    resultFlag = RESULT_REAL;
                    doubleResult = Math.sqrt(d);
                }
                break;
            case '!' :
                double result = 1;
                for(int i = (int)d; i > 0; i--){
                    result *= i;
                }
                resultFlag = RESULT_REAL;
                doubleResult = result;
                break;
        }
    }

    @Override
    public void function(ComplexNumber cn) {

    }

    @Override
    public void function(double d1, double d2, int iotaStatus) {
        switch(operator){
            case '+' :
                switch(iotaStatus){
                    case IOTA_NONE :
                        resultFlag = RESULT_REAL;
                        doubleResult = d1 + d2;
                        break;
                    case IOTA_BOTH :
                        resultFlag = RESULT_IOTA;
                        doubleResult = d1 + d2;
                        break;
                    case IOTA_FIRST :
                        complexResult.real = d2;
                        complexResult.iota = d1;
                        resultFlag = RESULT_COMPLEX;
                        break;
                    case IOTA_SECOND :
                        complexResult.real = d1;
                        complexResult.iota = d2;
                        resultFlag = RESULT_COMPLEX;
                        break;
                }
                break;
            case '-' :
                switch(iotaStatus){
                    case IOTA_NONE :
                        resultFlag = RESULT_REAL;
                        doubleResult = d1 - d2;
                        break;
                    case IOTA_BOTH :
                        resultFlag = RESULT_IOTA;
                        doubleResult = d1 - d2;
                        break;
                    case IOTA_FIRST :
                        complexResult.real = -d2;
                        complexResult.iota = d1;
                        resultFlag = RESULT_COMPLEX;
                        break;
                    case IOTA_SECOND :
                        complexResult.real = d1;
                        complexResult.iota = -d2;
                        resultFlag = RESULT_COMPLEX;
                        break;
                }
                break;
            case '*' :
                switch(iotaStatus){
                    case IOTA_NONE :
                        doubleResult = d1 * d2;
                        resultFlag = RESULT_REAL;
                        break;
                    case IOTA_BOTH :
                        doubleResult = d1 * d2 * -1;
                        resultFlag = RESULT_REAL;
                        break;
                    case IOTA_FIRST :
                    case IOTA_SECOND :
                        doubleResult = d1 * d2;
                        resultFlag = RESULT_IOTA;
                        break;
                }
                break;
            default : break;
        }
    }

    @Override
    public void function(ComplexNumber c1, double d2, int iotaStatus) {
        switch(operator){
            case '+' :
                switch(iotaStatus){
                    case IOTA_TRUE :
                        complexResult.iota = c1.iota + d2;
                        complexResult.real = c1.real;
                        resultFlag = RESULT_COMPLEX;
                        break;
                    case IOTA_FALSE :
                        complexResult.real = c1.real + d2;
                        complexResult.iota = c1.iota;
                        resultFlag = RESULT_COMPLEX;
                        break;
                }
                break;
            case '-' :
                switch(iotaStatus){
                    case IOTA_TRUE :
                        complexResult.iota = c1.iota - d2;
                        complexResult.real = c1.real;
                        resultFlag = RESULT_COMPLEX;
                        break;
                    case IOTA_FALSE :
                        complexResult.real = c1.real - d2;
                        complexResult.iota = c1.iota;
                        resultFlag = RESULT_COMPLEX;
                        break;
                }
                break;
            case '*' :
                switch (iotaStatus){
                    case IOTA_FALSE :
                        complexResult.real = c1.real * d2;
                        complexResult.iota = c1.iota * d2;
                        resultFlag = RESULT_COMPLEX;
                        break;
                    case IOTA_TRUE :
                        complexResult.iota = c1.real * d2;
                        complexResult.real = c1.iota * d2 * -1;
                        resultFlag = RESULT_COMPLEX;
                        break;
                }
                break;
        }
    }

    @Override
    public void function(double d1, ComplexNumber c2, int iotaStatus) {
        switch(operator){
            case '+' :
                switch(iotaStatus){
                    case IOTA_TRUE :
                        complexResult.iota = c2.iota + d1;
                        complexResult.real = c2.real;
                        resultFlag = RESULT_COMPLEX;
                        break;
                    case IOTA_FALSE :
                        complexResult.real = c2.real + d1;
                        complexResult.iota = c2.iota;
                        resultFlag = RESULT_COMPLEX;
                        break;
                }
                break;
            case '-' :
                switch(iotaStatus){
                    case IOTA_TRUE :
                        complexResult.iota = d1 - c2.iota;
                        complexResult.real = c2.real;
                        resultFlag = RESULT_COMPLEX;
                        break;
                    case IOTA_FALSE :
                        complexResult.real = d1 - c2.real;
                        complexResult.iota = c2.iota * -1;
                        resultFlag = RESULT_COMPLEX;
                        break;
                }
                break;
            case '*' :
                switch (iotaStatus){
                    case IOTA_FALSE :
                        complexResult.real = c2.real * d1;
                        complexResult.iota = c2.iota * d1;
                        resultFlag = RESULT_COMPLEX;
                        break;
                    case IOTA_TRUE :
                        complexResult.iota = c2.real * d1;
                        complexResult.real = c2.iota * d1 * -1;
                        resultFlag = RESULT_COMPLEX;
                        break;
                }
                break;
        }
    }

    @Override
    public void function(ComplexNumber c1, ComplexNumber c2) {
        switch(operator){
            case '+':
                complexResult.real = c1.real + c2.real;
                complexResult.iota = c1.iota + c2.iota;
                resultFlag = RESULT_COMPLEX;
                break;
            case '-' :
                complexResult.real = c1.real - c2.real;
                complexResult.iota = c1.iota - c2.iota;
                resultFlag = RESULT_COMPLEX;
                break;
            case '*' :
                double c1r = c1.real, c1i = c1.iota, c2r = c2.real, c2i = c2.iota;
                double d1 = c1r * c2r, d2 = c1r * c2i, d3 = c1i * c2r, d4 = c1i * c2i;
                complexResult.real = d1 + (d4 * -1);
                complexResult.iota = d2 + d3;
                resultFlag = RESULT_COMPLEX;
                break;
        }
    }
}
