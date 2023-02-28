package com.expression_parser_v2_0.console.core.types;

import static com.expression_parser_v2_0.console.core.CONSTANTS.*;
import com.expression_parser_v2_0.console.core.Stack;

//class representing terms.
//a term is an object containing one or more variables or constants
//in a term, the ordering would not matter as every other term is an independent unit in itself
//for example: (2 + 3x + 4y) or (4y + 3x + 2), does it make any difference? nope, not at all.
//the sign is part of the variable or the constant itself.
//for example the variable carries their own sign flag,
//the constants on the other hand has a double value which carries a sign flag by default 
//as it can either be a positive or a negative.
//no popping the items off the stack please.
//fine just pop it off, who cares.

//experimental
public class Terms {
    Stack<Variable> variables = new Stack<>();
    Stack<Constant> constants = new Stack<>();
    Stack<ComplexNumber> numbers = new Stack<>();

    public boolean hasNext(int type){
        return switch(type){
            case VARIABLE -> variables.hasNext();
            case CONSTANT -> constants.hasNext();
            default -> numbers.hasNext();
        };
    }

    public void pushVariable(Variable variable){
        variables.push(variable);
    }

    public void pushCOnstant(Constant constant){
        constants.push(constant);
    }

    public void pushNumber(ComplexNumber number){
        numbers.push(number);
    }

    public Variable getVariabe(){
        return variables.pop();
    }

    public Constant getConstant(){
        return constants.pop();
    }

    public ComplexNumber getNumber(){
        return numbers.pop();
    }
}
