package com.expression_parser_v2_0.console.core;

import static com.expression_parser_v2_0.console.core.CONSTANTS.*;

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
            if (real % 1 == 0)
                builder.append((real + "").split("\\.")[0]).append(',');
            else
                builder.append(real).append(',');
        }

        while(set.hasNext(ELEMENT_IOTA)) {
            double iota = set.pullIota();
            if (iota % 1 == 0)
                builder.append((iota + "").split("\\.")[0]).append("i");
            else
                builder.append(iota).append("i");
            builder.append(',');
        }

        while(set.hasNext(ELEMENT_COMPLEX)) {
            ComplexNumber cn = set.pullComplex();
            if (cn.real % 1 == 0)
                builder.append((cn.real + "").split("\\.")[0]);
            else
                builder.append(cn.real);
            if (cn.iota > 0)
                builder.append("+");
            if (cn.iota % 1 == 0)
                builder.append((cn.iota + "").split("\\.")[0]).append("i");
            else
                builder.append(cn.iota).append("i");
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
}
