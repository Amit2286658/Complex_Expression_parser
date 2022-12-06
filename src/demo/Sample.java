package demo;

import com.expression_parser_v2_0.console.core.ExpressionParser;
import com.expression_parser_v2_0.console.vendor.Expression;

import java.util.Scanner;

public class Sample {

    public static void main(String[] args){

        Expression expn = new Expression();
        ExpressionParser parser = expn.getExpressionParser();

        Scanner scn = new Scanner(System.in);
        String expression = scn.nextLine();
        scn.close();
        double time1 = System.currentTimeMillis();
        String str = parser.Evaluate(expression);
        double time2 = System.currentTimeMillis();
        System.out.println(str);
        System.out.println("time taken : " + (time2 - time1) + " ms");
    }
}
