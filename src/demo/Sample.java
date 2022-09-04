package demo;

import com.expression_parser_v2_0.console.library.functions.Set_Union;
import com.expression_parser_v2_0.console.library.functions.Sine;
import com.expression_parser_v2_0.console.library.functions.Sine_iota;
import com.expression_parser_v2_0.console.library.operations.Addition;
import com.expression_parser_v2_0.console.library.operations.Division;
import com.expression_parser_v2_0.console.library.operations.Multiplication;
import com.expression_parser_v2_0.console.library.operations.Subtraction;

import java.util.Scanner;
import static com.expression_parser_v2_0.console.core.Main.*;

public class Sample {

    public static void main(String[] args){
        new Addition();
        new Subtraction();
        new Multiplication();
        new Division();
        new Sine();
        new Sine_iota();
        new Set_Union();

        Scanner scn = new Scanner(System.in);
        String expression = scn.nextLine();
        double time1 = System.currentTimeMillis();
        String str = Evaluate(expression);
        double time2 = System.currentTimeMillis();
        System.out.println(str);
        System.out.println("time taken : " + (time2 - time1) + " ms");
    }
}
