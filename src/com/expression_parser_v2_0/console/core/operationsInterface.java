package com.expression_parser_v2_0.console.core;

import com.expression_parser_v2_0.console.core.types.*;

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
    Variable getVariable();
    Constant getConstant();
    Terms getTerms();

    void function();
    void function(double d, int iotaStatus);
    void function(double d1, double d2, int iotaStatus);

    void function(ComplexNumber cn);
    void function(ComplexNumber c1, double d2, int iotaStatus);
    void function(double d1, ComplexNumber c2, int iotaStatus);
    void function(ComplexNumber c1, ComplexNumber c2);

    void function(Set s);
    void function(Set s1, double d2, int iotaStatus);
    void function(double d1, Set s2, int iotaStatus);
    void function(Set s1, Set s2);

    void function(String str);
    void function(String str1, double d2, int iotaStatus);
    void function(double d1, String str2, int iotaStatus);
    void function(String str1, String str2);

    /////////////////////////////////////////////////////////////////////////////////////////////
    void function(Variable var);
    void function(Variable var1, double d2, int iotaStatus);
    void function(double d1, Variable var2, int iotaStatus);
    void function(Variable var1, Variable var2);

    void function(Constant con);
    void function(Constant con1, double d2, int iotaStatus);
    void function(double d1, Constant con2, int iotaStatus);
    void function(Constant con1, Constant con2);

    void function(Terms t);
    void function(Terms t1, double d2, int iotaStatus);
    void function(double d1, Terms t2, int iotaStatus);
    void function(Terms t1, Terms t2);
    /////////////////////////////////////////////////////////////////////////////////////////////

    void function(ComplexNumber c1, Set s2);
    void function(Set s1, ComplexNumber c2);
    void function(ComplexNumber c1, String str2);
    void function(String str1, ComplexNumber c2);
    /////////////////////////////////////////////////////////////////////////////////////////////
    void function(ComplexNumber c1, Variable var2);
    void function(Variable var1, ComplexNumber c2);
    void function(ComplexNumber c1, Constant con2);
    void function(Constant con1, ComplexNumber c2);
    void function(ComplexNumber c1, Terms t2);
    void function(Terms t1, ComplexNumber c2);
    void function(Set s1, Variable var2);
    void function(Variable var1, Set s2);
    void function(Set s1, Constant con2);
    void function(Constant con1, Set s2);
    void function(Set s1, Terms t2);
    void function(Terms t1, Set s2);
    void function(String str1, Variable var2);
    void function(Variable var1, String str2);
    void function(String str1, Constant con2);
    void function(Constant con1, String str2);
    void function(String str1, Terms t2);
    void function(Terms t1, String str2);
    void function(Variable var1, Constant con2);
    void function(Constant con1, Variable var2);
    void function(Variable var1, Terms t2);
    void function(Terms t1, Variable var2);
    void function(Constant con1, Terms t2);
    void function(Terms t1, Constant con2);
    /////////////////////////////////////////////////////////////////////////////////////////////
    void function(Set s1, String str2);
    void function(String str1, Set s2);
}
