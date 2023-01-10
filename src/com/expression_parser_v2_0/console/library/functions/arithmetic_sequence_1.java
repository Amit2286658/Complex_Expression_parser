package com.expression_parser_v2_0.console.library.functions;

import com.expression_parser_v2_0.console.core.NumberName;
import com.expression_parser_v2_0.console.library.Functions_Implementation;
import static com.expression_parser_v2_0.console.core.CONSTANTS.*;

import com.expression_parser_v2_0.console.core.Argument;

public class arithmetic_sequence_1 extends Functions_Implementation{

    int result_flag;
    double real_result;

    public arithmetic_sequence_1(NumberName number_name){
        super(number_name);
    }

    @Override
    public String[] getFunctionNames() {
        return new String[]{"AP"};
    }

    @Override
    public int[] getFunctionMap() {
        return new int[]{ARGUMENT_REAL, ARGUMENT_REAL, ARGUMENT_REAL};
    }

    @Override
    public double getReal() {
        return real_result;
    }

    @Override
    public int getId() {
        return 1;
    }

    @Override
    public int getResultFlag() {
        return result_flag;
    }

    @Override
    public void function(Argument[] arguments, int id) {
        double first_term = arguments[0].getRealArgument();
        double last_term = arguments[1].getRealArgument();
        double difference = arguments[2].getRealArgument();

        float n = (float) (((last_term - first_term)/difference) + 1);

        real_result = n/2 * (last_term + first_term);
        result_flag = RESULT_REAL;
    }
}
