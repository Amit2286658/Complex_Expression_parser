package com.expression_parser_v2_0.console.core.types;

import static com.expression_parser_v2_0.console.core.CONSTANTS.*;
//class representing variables,
//to be used for the algebra system.
public class Variable {
    private char identifier;
    public boolean isPositive;
    public int 
        coefficient_status = -1,
        exponent_status = -1,
        denominator_status = -1;
    private Object 
        coefficient = 1,
        exponent = 1,
        denominator = 1;

        //what if the coefficient is a number and is positive, but the provided sign is negative
        //well in this case, during the evaluation as the interface function will be called
        //which i guess would receive a variable and a double value, then as the variable is 
        //being created, those possibilities would be well processed. so the above mentions
        //condition might never happen.
    public Variable(char identifier, boolean isPositive){
        this.identifier = identifier;
        this.isPositive = isPositive;
    }

    public void setCoefficient(Object obj){
        if (obj == null)
            throw new NullPointerException("object is null");
            coefficient = obj;
        if (obj instanceof ComplexNumber cn){
            if (cn.iota == 0 && cn.real != 0)
                coefficient_status = IOTA;
            else if (cn.real == 0)
                coefficient_status = REAL;
            else
                coefficient_status = COMPLEX;
        }
        else if (obj instanceof String)
            coefficient_status = STRING;
        else if (obj instanceof Set)
            coefficient_status = SET;
        else if (obj instanceof Variable)
            coefficient_status = VARIABLE;
        else if (obj instanceof Constant)
            coefficient_status = CONSTANT;
        else if (obj instanceof Terms)
            coefficient_status = TERM;
        else
            throw new IllegalStateException("assignment beyond known types");
    }

    public void setExponent(Object obj){
        if (obj == null)
            throw new NullPointerException("object is null");
            exponent = obj;
        if (obj instanceof ComplexNumber cn){
            if (cn.iota == 0 && cn.real != 0)
                exponent_status = IOTA;
            else if (cn.real == 0)
                exponent_status = REAL;
            else
                exponent_status = COMPLEX;
        }
        else if (obj instanceof String)
            exponent_status = STRING;
        else if (obj instanceof Set)
            exponent_status = SET;
        else if (obj instanceof Variable)
            exponent_status = VARIABLE;
        else if (obj instanceof Constant)
            exponent_status = CONSTANT;
        else if (obj instanceof Terms)
            exponent_status = TERM;
        else
            throw new IllegalStateException("assignment beyond known types");
    }

    public void setDenominator(Object obj){
        if (obj == null)
            throw new NullPointerException("object is null");
            denominator = obj;
        if (obj instanceof ComplexNumber cn){
            if (cn.iota == 0 && cn.real != 0)
                denominator_status = IOTA;
            else if (cn.real == 0)
                denominator_status = REAL;
            else
                denominator_status = COMPLEX;
        }
        else if (obj instanceof String)
            denominator_status = STRING;
        else if (obj instanceof Set)
            denominator_status = SET;
        else if (obj instanceof Variable)
            denominator_status = VARIABLE;
        else if (obj instanceof Constant)
            denominator_status = CONSTANT;
        else if (obj instanceof Terms)
            denominator_status = TERM;
        else
            throw new IllegalStateException("assignment beyond known types");
    }

    public Object getCoefficient() {
        return coefficient;
    }

    public Object getExponent() {
        return exponent;
    }

    public Object getDenominator() {
        return denominator;
    }

    public char getIdentifier() {
        return identifier;
    }

    public boolean isPositive() {
        return isPositive;
    }
}
