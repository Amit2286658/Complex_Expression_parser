package com.expression_parser_v2_0.console.library;

import com.expression_parser_v2_0.console.core.*;

public abstract class Operations_Implementation implements operationsInterface {

    public NumberName myNumberName;

    public Operations_Implementation() {
        this(false, null);
    }

    public Operations_Implementation(boolean override){
        this(override, null);
    }

    public Operations_Implementation(NumberName numberName){
        this(false, numberName);
    }

    public Operations_Implementation(boolean override, NumberName numberName){
        Global.registerOperation(this, override);
        myNumberName = numberName;
    }

    @Override
    public String[] getOperationNames() {
        return null;
    }

    @Override
    public char getOperator() {
        throw new UnsupportedOperationException("operation not implemented yet");
    }

    @Override
    public int getPrecedence() {
        throw new UnsupportedOperationException("operation not implemented yet");
    }

    @Override
    public int getType() {
        throw new UnsupportedOperationException("operation not implemented yet");
    }

    @Override
    public int getResultFlag() {
        throw new UnsupportedOperationException("operation not implemented yet");
    }

    @Override
    public double getReal() {
        throw new UnsupportedOperationException("operation not implemented yet");
    }

    @Override
    public double getIota() {
        throw new UnsupportedOperationException("operation not implemented yet");
    }

    @Override
    public ComplexNumber getComplex() {
        throw new UnsupportedOperationException("operation not implemented yet");
    }

    @Override
    public Set getSet() {
        throw new UnsupportedOperationException("operation not implemented yet");
    }

    @Override
    public String getString() {
        throw new UnsupportedOperationException("operation not implemented yet");
    }

    @Override
    public void function() {
        throw new UnsupportedOperationException("operation not implemented yet");
    }

    @Override
    public void function(double d, int iotaStatus) {
        throw new UnsupportedOperationException("operation not implemented yet");
    }

    @Override
    public void function(double d1, double d2, int iotaStatus) {
        throw new UnsupportedOperationException("operation not implemented yet");
    }

    @Override
    public void function(ComplexNumber cn) {
        throw new UnsupportedOperationException("operation not implemented yet");
    }

    @Override
    public void function(ComplexNumber c1, double d2, int iotaStatus) {
        throw new UnsupportedOperationException("operation not implemented yet");
    }

    @Override
    public void function(double d1, ComplexNumber c2, int iotaStatus) {
        throw new UnsupportedOperationException("operation not implemented yet");
    }

    @Override
    public void function(ComplexNumber c1, ComplexNumber c2) {
        throw new UnsupportedOperationException("operation not implemented yet");
    }

    @Override
    public void function(Set s) {
        throw new UnsupportedOperationException("operation not implemented yet");
    }

    @Override
    public void function(Set s1, double d1, int iotaStatus) {
        throw new UnsupportedOperationException("operation not implemented yet");
    }

    @Override
    public void function(double d1, Set s2, int iotaStatus) {
        throw new UnsupportedOperationException("operation not implemented yet");
    }

    @Override
    public void function(Set s1, Set s2) {
        throw new UnsupportedOperationException("operation not implemented yet");
    }

    @Override
    public void function(String str) {
        throw new UnsupportedOperationException("operation not implemented yet");
    }

    @Override
    public void function(String str1, double d2, int iotaStatus) {
        throw new UnsupportedOperationException("operation not implemented yet");
    }

    @Override
    public void function(double d1, String str2, int iotaStatus) {
        throw new UnsupportedOperationException("operation not implemented yet");
    }

    @Override
    public void function(String str1, String str2) {
        throw new UnsupportedOperationException("operation not implemented yet");
    }

    @Override
    public void function(ComplexNumber c1, Set s2) {
        throw new UnsupportedOperationException("operation not implemented yet");
    }

    @Override
    public void function(Set s1, ComplexNumber c2) {
        throw new UnsupportedOperationException("operation not implemented yet");
    }

    @Override
    public void function(ComplexNumber c1, String str2) {
        throw new UnsupportedOperationException("operation not implemented yet");
    }

    @Override
    public void function(String str2, ComplexNumber c2) {
        throw new UnsupportedOperationException("operation not implemented yet");
    }

    @Override
    public void function(Set s1, String str2) {
        throw new UnsupportedOperationException("operation not implemented yet");
    }

    @Override
    public void function(String str1, Set s1) {
        throw new UnsupportedOperationException("operation not implemented yet");
    }
}
