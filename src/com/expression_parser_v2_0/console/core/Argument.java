package com.expression_parser_v2_0.console.core;

public final class Argument{
    private double real;
    private double iota;
    private ComplexNumber cn;
    private String str;
    private Set set;
    public int type;

    Argument(double real, double iota, ComplexNumber cn, String str, Set set){
        this.real = real;
        this.iota = iota;
        this.cn = cn;
        if (str != null)
            str = str.replace("\"", "");
        this.str = str;
        this.set = set;
    }

    public double getRealArgument() {
        return real;
    }

    public double getIotaArgument(){
        return iota;
    }

    public ComplexNumber getComplexArgument() {
        return cn;
    }

    public String getStringArgument() {
        return str;
    }

    public Set getSetArgument(){
        return set;
    }
}
