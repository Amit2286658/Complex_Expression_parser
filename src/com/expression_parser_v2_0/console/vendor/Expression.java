package com.expression_parser_v2_0.console.vendor;

import static com.expression_parser_v2_0.console.core.CONSTANTS.*;

import com.expression_parser_v2_0.console.core.NumberName;
import com.expression_parser_v2_0.console.core.number_name_data_sets.*;
import com.expression_parser_v2_0.console.core.ExpressionParser;
import com.expression_parser_v2_0.console.library.arithmetic_operations.*;
import com.expression_parser_v2_0.console.library.boolean_operations.*;
import com.expression_parser_v2_0.console.library.constant_operations.*;
import com.expression_parser_v2_0.console.library.functions.*;

//this class will also be used for caching and other purposes
public class Expression {
    //class initials
    private final ExpressionParser parser = new ExpressionParser();
    private final NumberName numberName = new NumberName();

    //number name data sets
    IndianNumberName indianInterface = new IndianNumberName();
    FrenchNumberName frenchInterface = new FrenchNumberName();
    EnglishNumberName englishInterface = new EnglishNumberName();

    public Expression(){
        numberName.setCurrentNumberNameSystem(englishInterface);

        featuresEnable();
        parser.enableGlobal();
    }
    public ExpressionParser getExpressionParser(){
        return parser;
    }

    public NumberName getNumberNameParser(){
        return numberName;
    }

    public void setNumberNameSystem(int type){
        numberName.setCurrentNumberNameSystem(
            switch(type){
                case INDIAN_NUMBER_SYSTEM -> indianInterface;
                case FRENCH_NUMBER_SYSTEM -> frenchInterface;
                case ENGLISH_NUMBER_SYSTEM -> englishInterface;
                default -> throw new IllegalArgumentException(
                    "the given number name set does not exist yet");
            }
        );
    }

    private void featuresEnable(){
        new Addition(numberName);
        new Subtraction(numberName);
        new Multiplication(numberName);
        new Division(numberName);
        new Sine(numberName);
        new Sine_iota(numberName);
        new Set_Union(numberName);
        new Sqrt(numberName);
        new Factorial(numberName);
        new pi();
        new euler_constant();
        new equal_to();
        new Empty(numberName);
    }
}
