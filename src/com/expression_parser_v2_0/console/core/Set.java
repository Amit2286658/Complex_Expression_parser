package com.expression_parser_v2_0.console.core;

import java.util.ArrayList;

import static com.expression_parser_v2_0.console.core.constants.*;

public final class Set{
    //type enforcement, but honestly, I have no energy left to implement it right now,
    //or 10 years in the future.
    int type = -1;
    private final Stack<Double> reals;
    private final Stack<Double> iotas;
    private final Stack<ComplexNumber> complexes;
    private final Stack<String> strings;
    private final Stack<Set> sets;
    @SuppressWarnings("SpellCheckingInspection")
    //without this little guy here, one could remain stucked forever in the depths of the nested sets.
    //update : or maybe not, I mean there's always this little thing called recursion.
    //int nestedSets = 0;

    public Set(){
        reals = new Stack<>();
        iotas = new Stack<>();
        complexes = new Stack<>();
        strings = new Stack<>();
        sets = new Stack<>();
    }

    //these push com.expression_parser_v2_0.console.library.functions are only available to this whole Parser class, the client class does not
    //need to have access to these com.expression_parser_v2_0.console.library.functions as it'd be useless for them anyway.
    public void pushReal(double d){
        reals.push(d);
    }

    public void pushIota(double d){
        iotas.push(d);
    }

    public void pushComplex(ComplexNumber cn){
        complexes.push(cn);
    }

    public void pushString(String string){
        string = string.replace("\"", "");
        strings.push(string);
    }

    public void pushSet(Set s){
        sets.push(s);
    }

    public double pullReal(){
        return reals.pop();
    }

    public double pullIota(){
        return iotas.pop();
    }

    public ComplexNumber pullComplex(){
        return complexes.pop();
    }

    public String pullString(){
        return strings.pop();
    }

    public Set pullSet(){
        return sets.pop();
    }

    public boolean containsReal(double real){
        return reals.contains(real);
    }

    public boolean containsIota(double iota){
        return iotas.contains(iota);
    }

    public boolean containsComplexNumber(ComplexNumber cn){
        return complexes.contains(cn);
    }

    public boolean containsString(String str){
        return strings.contains(str);
    }

    //the data in the set do not need to be popped off the stack.
    //always call the reset function first
    public boolean hasNext(int type){
        return switch (type) {
            case ELEMENT_REAL -> reals.hasNext();
            case ELEMENT_IOTA -> iotas.hasNext();
            case ELEMENT_COMPLEX -> complexes.hasNext();
            case ELEMENT_SET -> sets.hasNext();
            case ELEMENT_STRING -> strings.hasNext();
            default -> throw new IllegalArgumentException("unknown type");
        };
    }

    public ArrayList<Double> getRealsAsList(){
        return reals.getAsList();
    }

    public ArrayList<Double> getIotasAsList(){
        return iotas.getAsList();
    }

    public ArrayList<ComplexNumber> getComplexesAsList(){
        return complexes.getAsList();
    }

    public ArrayList<String> getStringsAsList(){
        return strings.getAsList();
    }

    public ArrayList<Set> getSetsAsList(){
        return sets.getAsList();
    }
}