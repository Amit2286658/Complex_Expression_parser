package com.expression_parser_v2_0.console.core.types;

public final class Argument{
    Object obj;
    public int type;

    //it may seem obvious that a function is never going to receive constants and terms, i mean clearly
    //they cannot be typed in, they are created at runtine, but what if the function excpets a constant
    //and for that it uses two static types which together form a constant like 2 exponent y.

    public Argument(Object obj, int type) {
        this.obj = obj;
        this.type = type;
    }

    public double getRealArgument() {
        return (Double)obj;
    }

    public double getIotaArgument(){
        return (Double)obj;
    }

    public ComplexNumber getComplexArgument() {
        return (ComplexNumber)obj;
    }

    public String getStringArgument() {
        return ((String)obj).replace("\"", "");
    }

    public Set getSetArgument(){
        return (Set)obj;
    }

    public Variable getVarArgument(){
        return (Variable)obj;
    }

    public Constant getConArgument(){
        return (Constant)obj;
    }

    public Terms getTermArgument(){
        return (Terms)obj;
    }
}
