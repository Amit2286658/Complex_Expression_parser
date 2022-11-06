package demo;

import static com.expression_parser_v2_0.console.core.constants.*;

import com.expression_parser_v2_0.console.core.NumberName;
import com.expression_parser_v2_0.console.core.Parser;
import com.expression_parser_v2_0.console.library.functions.Set_Union;
import com.expression_parser_v2_0.console.library.functions.Sine;
import com.expression_parser_v2_0.console.library.functions.Sine_iota;
import com.expression_parser_v2_0.console.library.operations.*;
import com.expression_parser_v2_0.console.vendor.Expression;

import java.util.Scanner;

public class Sample {

    public static void main(String[] args){
        new Addition();
        new Subtraction();
        new Multiplication();
        new Division();
        new Sine();
        new Sine_iota();
        new Set_Union();
        new Sqrt();
        new Factorial();

        Expression expn = new Expression();
        Parser parser = expn.getParser();
        NumberName numberName = expn.getNumberNameParser();
        
        numberName.setNumberSystem(FRENCH_NUMBER_SYSTEM);
        //numberName.setStandardPointNotation(false);
        //String name = numberName.convertToNumberName("77");
        double number = numberName.convertNameToNumber("deux milliards soixante-dix-neuf");
        //System.out.println(name);
        System.out.println(number);

        // Scanner scn = new Scanner(System.in);
        // String expression = scn.nextLine();
        // double time1 = System.currentTimeMillis();
        // Parser exp = new Parser();
        // String str = exp.Evaluate(expression);
        // double time2 = System.currentTimeMillis();
        // System.out.println(str);
        // System.out.println("time taken : " + (time2 - time1) + " ms");
    }
}
