package com.expression_parser_v2_0.console.core;

import static com.expression_parser_v2_0.console.core.CONSTANTS.*;
import static com.expression_parser_v2_0.console.core.tokens.complex_token;

public class Utility {
    //when true, the entered angle will be converted into the radians since
    //that's the default accepting parameter
    //for the java math com.expression_parser_v2_0.console.library.functions, when false, no conversion will happen.
    private static boolean use_degree = true;

    public static int getAngleMode() {
        return use_degree ? ANGLE_MODE_DEGREE : ANGLE_MODE_RADIAN;
    }

    public static void setAngleMode(int angleMode) {
        if (angleMode == ANGLE_MODE_DEGREE)
            use_degree = true;
        else if (angleMode == ANGLE_MODE_RADIAN)
            use_degree = false;
        else
            throw new IllegalArgumentException("unknown angle mode");
    }

    //repeat after me, System.out.println System.out.println System.out.println System.out.println
    public static void print(String str){
        System.out.println(str);
    }

    public static void displaySet(Set set){
        System.out.println(convertSetToString(set));
    }

    protected static String convertSetToString(Set set){
        return convertSetToString(set, true);
    }

    private static String convertSetToString(Set set, boolean firstIteration){
        StringBuilder builder = new StringBuilder();

        if (firstIteration)
            builder.append('{');

        while(set.hasNext(ELEMENT_REAL)) {
            double real = set.pullReal();
            if (real % 1 == 0){
                String[] parts = (real + "").split("\\.");
                    boolean empty_decimals = true;
                    for(char c : parts[1].toCharArray()){
                        if (c != '0'){
                            empty_decimals = false;
                            break;
                        }
                    }
                    if (empty_decimals)
                        builder.append(parts[0]).append(',');
                    else
                        builder.append(real).append(',');
            }else
                builder.append(real).append(',');
        }

        while(set.hasNext(ELEMENT_IOTA)) {
            double iota = set.pullIota();
            if (iota % 1 == 0){
                String[] parts = (iota + "").split("\\.");
                    boolean empty_decimals = true;
                    for(char c : parts[1].toCharArray()){
                        if (c != '0'){
                            empty_decimals = false;
                            break;
                        }
                    }
                    if (empty_decimals)
                        builder.append(parts[0]).append('i');
                    else
                        builder.append(iota).append('i');
            }else
                builder.append(iota).append("i");
            builder.append(',');
        }

        while(set.hasNext(ELEMENT_COMPLEX)) {
            ComplexNumber cn = set.pullComplex();
            builder.append(convertComplexToString(cn, false));
            builder.append(',');
        }

        while(set.hasNext(ELEMENT_STRING))
            builder.append('"').append(set.pullString()).append('"').append(',');

        while(set.hasNext(ELEMENT_SET))
            builder.append('{').append(convertSetToString(set.pullSet(), false))
                    .append('}').append(',');

        int last_pos = builder.length() - 1;
        if (last_pos != -1 && builder.charAt(last_pos) == ',')
            builder.deleteCharAt(last_pos);

        if (firstIteration)
            builder.append('}');
        return builder.toString();
    }

    public static String convertComplexToString(ComplexNumber cn, boolean c_t){
        return (c_t ? complex_token : "") + "" + cn.real +
                (cn.iota >= 0 ? "+" : "") + cn.iota + "i" + (c_t ? complex_token : "");
    }

    public static ComplexNumber convertToComplexNumber(String complexString){
        ComplexNumber number = new ComplexNumber();
        StringBuilder currentStep = new StringBuilder();

        for (int i = 0; i < complexString.length(); i++){
            char c = complexString.charAt(i);
            if (c == '+' || c == '-') {
                if (i != 0) {
                    number.real = Double.parseDouble(currentStep.toString());
                    currentStep.setLength(0);
                }
            }
            currentStep.append(c);
        }
        number.iota = Double.parseDouble(currentStep.toString().replaceAll("i", ""));

        return number;
    }

    public static ComplexNumber convertToComplexNumber(double value, boolean iota){
        return new ComplexNumber(!iota ? value : 0, iota ? value : 0);
    }

    public static String convertToComplexString(double value, boolean iota){
        return complex_token + (!iota ? value + "+" + 0 + "i" : 0 + (value < 0 ? "" : "+")
                + value + "i") + complex_token;
    }

    public static String convertComplexToString(ComplexNumber cn){
        return convertComplexToString(cn, true);
    }
}
