package com.expression_parser_v2_0.console.vendor;

import static com.expression_parser_v2_0.console.core.constants.*;

import com.expression_parser_v2_0.console.core.NumberName;
import com.expression_parser_v2_0.console.core.ExpressionParser;
import com.expression_parser_v2_0.console.core.NumberNameDataSets.EnglishNumberName;
import com.expression_parser_v2_0.console.core.NumberNameDataSets.FrenchNumberName;
import com.expression_parser_v2_0.console.core.NumberNameDataSets.IndianNumberName;
import com.expression_parser_v2_0.console.library.functions.*;
import com.expression_parser_v2_0.console.library.operations.*;

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
        featuresEnable();
        parser.enableGlobal();

        numberName.setCurrentNumberNameSystem(englishInterface);
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
                default -> englishInterface;
            }
        );
    }

    private void featuresEnable(){
        new Addition();
        new Subtraction();
        new Multiplication();
        new Division();
        new Sine();
        new Sine_iota();
        new Set_Union();
        new Sqrt();
        new Factorial();
    }
}
