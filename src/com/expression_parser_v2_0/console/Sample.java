package com.expression_parser_v2_0.console;

import java.util.Scanner;
import static com.expression_parser_v2_0.console.Main.Evaluate;

public class Sample {

    public static void main(String[] args){
        Scanner scn = new Scanner(System.in);
        String expression = scn.nextLine();
        registerLibraries();
        String str = Evaluate(expression);
        System.out.println(str);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void registerLibraries(){
        Operational_Library.values();
        Functional_Library.values();
    }
}
