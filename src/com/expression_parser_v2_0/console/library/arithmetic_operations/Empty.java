package com.expression_parser_v2_0.console.library.arithmetic_operations;

import com.expression_parser_v2_0.console.library.Operations_Implementation;
import static com.expression_parser_v2_0.console.core.CONSTANTS.*;

import com.expression_parser_v2_0.console.core.NumberName;
import com.expression_parser_v2_0.console.core.tokens;

//null operation, not for use within the expression itself
//it will be called by the sortResult function to sort out the single operands
//the leftovers from the actual evaluation of the expression
public class Empty extends Operations_Implementation {

    int result_flag;
    double real_result;
    String string_result;

    public Empty(NumberName numberName) {
        super(numberName);
    }

    //empty character as an operator
    @Override
    public char getOperator() {
        return tokens.empty_token;
    }

    @Override
    public int getResultFlag() {
        return result_flag;
    }

    @Override
    public double getReal() {
        return real_result;
    }

    @Override
    public String getString() {
        return string_result;
    }

    //the only function which is suitable to be called. no need to override others
    @Override
    public void function(String str) {
        try {
            double result = myNumberName.convertNameToNumber(str);
            result_flag = RESULT_REAL;
            real_result = result;
        }catch (Exception e){
            result_flag = RESULT_STRING;
            string_result = str;
        }
    }
}
