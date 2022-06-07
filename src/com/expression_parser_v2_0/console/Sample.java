package com.expression_parser_v2_0.console;

import java.util.Scanner;
import static com.expression_parser_v2_0.console.Main.Evaluate;

public class Sample {

    public static void main(String[] args){
        Scanner scn = new Scanner(System.in);
        String expression = scn.nextLine();
        registerLibraries();
        double time1 = System.nanoTime();
        String str = Evaluate(expression);
        double time2 = System.nanoTime();
        System.out.println(str);
        System.out.println("time taken : " + (time2 - time1)/1000000 + " ms");
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void registerLibraries(){
        Operational_Library.values();
        Functional_Library.values();
    }
}
