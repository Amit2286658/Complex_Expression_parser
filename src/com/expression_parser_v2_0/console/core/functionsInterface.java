package com.expression_parser_v2_0.console.core;

public interface functionsInterface{
    String[] getFunctionNames();
    int[] getFunctionMap();

    //useful in enums.
    int getId();

    int getResultFlag();
    double getReal();
    double getIota();
    ComplexNumber getComplex();
    Set getSet();
    String getString();
    void function(Argument[] arguments, int id);
}
