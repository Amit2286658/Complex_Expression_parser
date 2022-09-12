package com.expression_parser_v2_0.console.library;

import com.expression_parser_v2_0.console.core.*;

public abstract class Functions_Implementation implements functionsInterface {

    public Functions_Implementation(){
        this(false);
    }

    public Functions_Implementation(boolean override){
        Global.registerFunction(this, override);
    }

    @Override
    public String[] getFunctionNames() {
        throw new UnsupportedOperationException("function not implemented yet");
    }

    @Override
    public int[] getFunctionMap() {
        throw new UnsupportedOperationException("function not implemented yet");
    }

    @Override
    public int getId() {
        throw new UnsupportedOperationException("function not implemented yet");
    }

    @Override
    public int getResultFlag() {
        throw new UnsupportedOperationException("function not implemented yet");
    }

    @Override
    public double getReal() {
        throw new UnsupportedOperationException("function not implemented yet");
    }

    @Override
    public double getIota() {
        throw new UnsupportedOperationException("function not implemented yet");
    }

    @Override
    public ComplexNumber getComplex() {
        throw new UnsupportedOperationException("function not implemented yet");
    }

    @Override
    public Set getSet() {
        throw new UnsupportedOperationException("function not implemented yet");
    }

    @Override
    public String getString() {
        throw new UnsupportedOperationException("function not implemented yet");
    }

    @Override
    public void function(Argument[] arguments, int id) {
        throw new UnsupportedOperationException("function not implemented yet");
    }
}
