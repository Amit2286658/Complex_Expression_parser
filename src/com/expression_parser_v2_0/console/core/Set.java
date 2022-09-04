package com.expression_parser_v2_0.console.core;

import static com.expression_parser_v2_0.console.core.constants.*;

public final class Set{
    //type enforcement, but honestly, I have no energy left to implement it right now,
    //or 10 years in the future.
    int type = -1;
    private Stack<Double> reals;
    private Stack<Double> iotas;
    private Stack<ComplexNumber> complexes;
    private Stack<String> strings;
    private Stack<Set> sets;
    @SuppressWarnings("SpellCheckingInspection")
    //without this little guy here, one could remain stucked forever in the depths of the nested sets.
    //update : or maybe not, I mean there's always this little thing called recursion.
    //int nestedSets = 0;

    public Set(){
        reals = new Stack<>();
        reals.iterateRestrict();
        iotas = new Stack<>();
        iotas.iterateRestrict();
        complexes = new Stack<>();
        complexes.iterateRestrict();
        strings = new Stack<>();
        strings.iterateRestrict();
        sets = new Stack<>();
        sets.iterateRestrict();
    }

    //these push com.expression_parser_v2_0.console.library.functions are only available to this whole Main class, the client class does not
    //need to have access to these com.expression_parser_v2_0.console.library.functions as it'd be useless for them anyways.
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

    public double getReal(){
        return reals.get();
    }

    public double pullIota(){
        return iotas.pop();
    }

    public double getIota(){
        return iotas.get();
    }

    public ComplexNumber pullComplex(){
        return complexes.pop();
    }

    public ComplexNumber getComplex(){
        return complexes.get();
    }

    public String pullString(){
        return strings.pop();
    }

    public String getString(){
        return strings.get();
    }

    public Set pullSet(){
        return sets.pop();
    }

    public Set getSet(){
        return sets.get();
    }

    public boolean hasNext(int type){
        switch(type){
            case ELEMENT_REAL :
                return reals.hasNext();
            case ELEMENT_IOTA :
                return iotas.hasNext();
            case ELEMENT_COMPLEX :
                return complexes.hasNext();
            case ELEMENT_SET :
                return sets.hasNext();
            case ELEMENT_STRING :
                return strings.hasNext();
            default :
                throw new IllegalArgumentException("unknown type");
        }
    }

    public void resetLoop(int type){
        switch(type){
            case ELEMENT_REAL :
                reals.reset();
                break;
            case ELEMENT_IOTA :
                iotas.reset();
                break;
            case ELEMENT_COMPLEX :
                complexes.reset();
                break;
            case ELEMENT_SET :
                sets.reset();
                break;
            case ELEMENT_STRING :
                strings.reset();
                break;
            default :
                throw new IllegalArgumentException("unknown type");
        }
    }

    public boolean loop(int type){
        switch(type){
            case ELEMENT_REAL :
                return reals.loop();
            case ELEMENT_IOTA :
                return iotas.loop();
            case ELEMENT_COMPLEX :
                return complexes.loop();
            case ELEMENT_SET :
                return sets.loop();
            case ELEMENT_STRING :
                return strings.loop();
            default :
                throw new IllegalArgumentException("unknown type");
        }
    }
}