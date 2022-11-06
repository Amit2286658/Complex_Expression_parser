package com.expression_parser_v2_0.console.core;

public interface NumberNameDataInterface {
    int stepChange(int step);
    String getZero();
    String getPointName();
    int getInitialGroupDifference();

    String postProcessing(String name);

    //step is normalized to 0
    String getName(int step, int position, int current_number,
        int[] previous_numbers, int[] next_numbers, Stack<String> name);

    String getSimpleName(int current_number);

    int getAdditiveNumber(String current_term);
    long getMultiplicativeNumber(String current_term);
}
