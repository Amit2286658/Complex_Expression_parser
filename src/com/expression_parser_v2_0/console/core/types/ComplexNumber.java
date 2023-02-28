package com.expression_parser_v2_0.console.core.types;

public final class ComplexNumber {
    public double real = 0, iota = 0;

    public ComplexNumber(){
        //empty
    }

    public ComplexNumber(double real, double iota){
        this.real = real;
        this.iota = iota;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ComplexNumber cn && this.real == cn.real && this.iota == cn.iota;
    }
}
