package com.expression_parser_v2_0.console.core;

public class tokens {
    //constants and terms are runtime types
    static final char
            complex_token = '$',
            function_token = '@',
            operation_token = '_',
            pointer_token = '#',
            variable_token = '?',
            string_token = '"';

    public static final char empty_token = '۪';

    public static boolean containsToken(String expression){
        for(char c : getTokensAsCharArray()){
            if (expression.contains(c + ""))
                return true;
        }
        return false;
    }

    public static char[] getTokensAsCharArray(){
        return new char[]{
            complex_token, function_token, operation_token,
            pointer_token, variable_token, empty_token
        };
    }
}
