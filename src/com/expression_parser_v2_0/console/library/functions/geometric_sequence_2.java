package com.expression_parser_v2_0.console.library.functions;

import com.expression_parser_v2_0.console.core.NumberName;
import com.expression_parser_v2_0.console.core.types.Argument;
import com.expression_parser_v2_0.console.library.Functions_Implementation;
import static com.expression_parser_v2_0.console.core.CONSTANTS.*;

public class geometric_sequence_2 extends Functions_Implementation{
    int result_flag;
    double real_result;

    public geometric_sequence_2(NumberName number_name){
        super(number_name);
    }

    @Override
    public String[] getFunctionNames() {
        return new String[]{"GP_nFirst"};
    }

    @Override
    public int[] getFunctionMap() {
        return new int[]{REAL, REAL, REAL};
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
        double n = arguments[1].getRealArgument();
        double ratio = arguments[2].getRealArgument();

        // if (n < 1){
        //     real_result = first_term * (Math.pow(ratio, n) - 1) / (ratio - 1);
        // }else if ( n > 1) {
            real_result = first_term * (1 - Math.pow(ratio, n)) / (1 - ratio);
        // }else {
        //     real_result = first_term * n;
        // }
        result_flag = REAL;
    }
}
