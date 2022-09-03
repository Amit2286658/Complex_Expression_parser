package com.expression_parser_v2_0.console.core;

public interface operationsInterface {
    String[] getOperationNames();
    char getOperator();
    int getPrecedence();
    int getType();
    int getResultFlag();
    double getReal();
    double getIota();
    ComplexNumber getComplex();
    Set getSet();
    String getString();
    void function();
    void function(double d, int iotaStatus);
    void function(double d1, double d2, int iotaStatus);

    void function(ComplexNumber cn);
    void function(ComplexNumber c1, double d2, int iotaStatus);
    void function(double d1, ComplexNumber c2, int iotaStatus);
    void function(ComplexNumber c1, ComplexNumber c2);

    void function(Set s);
    void function(Set s1, double d1, int iotaStatus);
    void function(double d1, Set s2, int iotaStatus);
    void function(Set s1, Set s2);

    void function(String str);
    void function(String str1, double d2, int iotaStatus);
    void function(double d1, String str2, int iotaStatus);
    void function(String str1, String str2);

    void function(ComplexNumber c1, Set s2);
    void function(Set s1, ComplexNumber c2);
    void function(ComplexNumber c1, String str2);
    void function(String str2, ComplexNumber c2);
    void function(Set s1, String str2);
    void function(String str1, Set s1);
}
