package com.expression_parser_v2_0.console.library.functions;

import com.expression_parser_v2_0.console.core.NumberName;
import com.expression_parser_v2_0.console.core.types.Argument;
import com.expression_parser_v2_0.console.library.Functions_Implementation;
import static com.expression_parser_v2_0.console.core.CONSTANTS.*;

public class arithmetic_sequence_3 extends Functions_Implementation{
    int result_flag;
    double real_result;

    public arithmetic_sequence_3(NumberName number_name){
        super(number_name);
    }

    @Override
    public String[] getFunctionNames() {
        return new String[]{"AP_arr"};
    }

    @Override
    public int[] getFunctionMap() {
        return new int[]{ARRAY, REAL};
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
        double difference = arguments[1].getRealArgument() - arguments[0].getRealArgument();

        real_result = n/2 * (2 * first_term + (n - 1) * difference);
        result_flag = REAL;
    }
}
