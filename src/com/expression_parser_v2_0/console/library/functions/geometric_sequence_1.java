package com.expression_parser_v2_0.console.library.functions;

import com.expression_parser_v2_0.console.core.NumberName;
import com.expression_parser_v2_0.console.library.Functions_Implementation;
import static com.expression_parser_v2_0.console.core.CONSTANTS.*;

import com.expression_parser_v2_0.console.core.Argument;

public class geometric_sequence_1 extends Functions_Implementation{
    int result_flag;
    double real_result;

    public geometric_sequence_1(NumberName number_name){
        super(number_name);
    }

    @Override
    public String[] getFunctionNames() {
        return new String[]{"GP_arr"};
    }

    @Override
    public int[] getFunctionMap() {
        return new int[]{ARGUMENT_ARRAY, ARGUMENT_REAL};
    }

    @Override
    public double getReal() {
        return real_result;
    }

    @Override
    public int getId() {
        return 2;
    }

    @Override
    public int getResultFlag() {
        return result_flag;
    }

    @Override
    public void function(Argument[] arguments, int id) {
        double first_term = arguments[0].getRealArgument();
        double n = arguments.length;
        double difference = arguments[1].getRealArgument() / arguments[0].getRealArgument();

        real_result = first_term * (1 - Math.pow(difference, n)) / (1 - difference);
        result_flag = RESULT_REAL;
    }
}
