package com.expression_parser_v2_0.console.core;

public class tokens {
    //constants and terms are runtime types
    //variable token is to be used with the variables (experimental)
    static final char
            complex_token = '$',
            function_token = '@',
            operation_token = '_',
            pointer_token = '#',
            variable_token = '?',
            string_token = '"',
            multiplication_token = '*';

    public static final char empty_token = '۪';

    public static boolean containsToken(String expression){
        char[] tokens_array = new char[]{
                complex_token, function_token,
                pointer_token, variable_token, empty_token
        };

        for(char c : tokens_array){
            if (expression.contains(c + ""))
                return true;
        }
        return false;
    }
}
