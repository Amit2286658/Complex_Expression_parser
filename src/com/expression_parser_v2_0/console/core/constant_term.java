package com.expression_parser_v2_0.console.core;

//experimental
public class constant_term {
    private double value;
    private Variable exponent;
    private Variable denominator;
    //why not the others, well they'd be solved anyways.
    constant_term(double value, Variable exponent, Variable denominator){
        this.value = value;
        this.exponent = exponent;
        this.denominator = denominator;
    }

    public void setValue(double value){
        this.value = value;
    }

    public void setExponent(Variable exp){
        this.exponent = exp;
    }

    public void setDenominator(Variable denominator){
        this.denominator = denominator;
    }
}
