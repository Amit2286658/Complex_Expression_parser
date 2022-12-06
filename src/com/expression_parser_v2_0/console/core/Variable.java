package com.expression_parser_v2_0.console.core;

//class representing variables,
//experimental
//to be used for the algebra system.
public class Variable {
    //character identifier
    private char identifier = 0;
    //numerical coefficient
    private double coefficient = 1;
    //a variable likewise can also be a coefficient, like "xy" where x is the coefficient of y
    //in a variable like 2xy, 2x is the coefficient of y, and 2 is the coefficient of x.
    private Variable coefficient_var = null;
    //numerical denominator
    private double denominator = 1;
    //a variable can be the denominator the same way a variable can be the coefficient,
    //same logic applies here
    private Variable denominator_var = null;
    //true would represent plus, false likewise minus
    //in case the variable has an another variable as a coefficient
    //the sign must be same as that coeffcient variable's sign
    private boolean sign = true;
    //numerical exponent
    private double exponent = 1;
    //a variable can be the exponent the same way a variable can be the coefficient and a denominator,
    //same logic applies here
    private Variable exponent_var = null;

    public Variable(char identfier, boolean sign, double coefficient, Variable coefficient_var,
        double denominator, Variable denominator_var, double exponent, Variable exponent_var){
            this.exponent = exponent;
            this.exponent_var = exponent_var;
            this.denominator = denominator;
            this.denominator_var = denominator_var;
            this.coefficient = coefficient;
            this.coefficient_var = coefficient_var;
            this.sign = sign;
            this.identifier = identfier;
    }

    public Variable(char identifier, boolean sign, double coefficient, double denominator,
        double exponent){
            this(identifier, sign, coefficient, null, denominator,
                null, exponent, null);
    }

    public Variable(char identfier){
        this(identfier, true, 1, null,
            1, null, 1, null);
    }

    public double getCoefficientIfNotVar(){
        return coefficient;
    }

    public Variable getCoefficientIfVar(){
        return coefficient_var;
    }

    public double getDenominatorIfNotVar(){
        return denominator;
    }

    public Variable getDenominatorIfVar(){
        return denominator_var;
    }

    public double getExponentIfNotVar(){
        return exponent;
    }

    public Variable getExponentIfVar(){
        return exponent_var;
    }

    public boolean getSign(){
        return sign;
    }

    public char getIdentifier(){
        return identifier;
    }

    public void setCoefficientIfVar(Variable coefficient_var){
        this.coefficient_var = coefficient_var;
    }

    public void setCoefficientIfNotVar(double coefficient){
        this.coefficient = coefficient;
    }
    
    public void setDenominatorIfVar(Variable denominator_var){
        this.denominator_var = denominator_var;
    }

    public void setDenominatorIfNotVar(double denominator){
        this.denominator = denominator;
    }

    public void setExponentIfVar(Variable exponent_var){
        this.exponent_var = exponent_var;
    }

    public void setexpoenentIfNotVar(double exponent){
        this.exponent = exponent;
    }

    public void setSign(boolean sign){
        this.sign = sign;
    }

}
