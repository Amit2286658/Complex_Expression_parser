package com.expression_parser_v2_0.console.library;

public class Commons {

    public static int factorial(int number){
        int result = 1;
        boolean positive = (number > 0);
        while(number != 0){
            result *= number;
            number = positive ? number-1 : number+1;
        }
        return result;
    }

}
