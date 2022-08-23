package com.expression_parser_v2_0.console;

import functions.Sine;
import operations.Addition;
import operations.Division;
import operations.Multiplication;
import operations.Subtraction;

import java.util.Scanner;
import static com.expression_parser_v2_0.console.Main.*;

public class Sample {

    public static void main(String[] args){
        new Addition();
        new Subtraction();
        new Multiplication();
        new Division();
        new Sine();

        Scanner scn = new Scanner(System.in);
        String expression = scn.nextLine();
        double time1 = System.currentTimeMillis();
        String str = Evaluate(expression);
        double time2 = System.currentTimeMillis();
        System.out.println(str);
        System.out.println("time taken : " + (time2 - time1) + " ms");
    }
}
