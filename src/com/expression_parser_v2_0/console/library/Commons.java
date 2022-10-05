package com.expression_parser_v2_0.console.library;

public class Commons {

    public static int factorial(int number){
        int result = 1;
        while(number != 0){
            result *= number;
            number--;
        }
        return result;
    }

}
