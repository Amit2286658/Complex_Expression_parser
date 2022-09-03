package com.expression_parser_v2_0.console.library.functions;

import com.expression_parser_v2_0.console.core.Argument;
import com.expression_parser_v2_0.console.core.ComplexNumber;
import com.expression_parser_v2_0.console.library.Functions_Implementation;
import com.expression_parser_v2_0.console.core.Main;

import static com.expression_parser_v2_0.console.core.constants.*;

public class Sine_iota extends Functions_Implementation {

    int resultFlag;
    double real;
    double iota;
    ComplexNumber cn;

    public Sine_iota() {
        super();
    }

    @Override
    public String[] getFunctionNames() {
        return new String[]{"Sin", "sin", "SIN"};
    }

    @Override
    public int[] getFunctionMap() {
        return new int[]{ARGUMENT_IOTA};
    }

    @Override
    public int getId() {
        return 2;
    }

    @Override
    public int getResultFlag() {
        return resultFlag;
    }

    @Override
    public double getReal() {
        return real;
    }

    @Override
    public double getIota() {
        return iota;
    }

    @Override
    public ComplexNumber getComplex() {
        return cn;
    }

    @Override
    public void function(Argument[] arguments, int id) {
        real = Math.sin(Main.getAngleMode() == ANGLE_MODE_RADIAN ? arguments[0].getIotaArgument() :
                Math.toRadians(arguments[0].getIotaArgument()));
        resultFlag = RESULT_REAL;
    }
}
