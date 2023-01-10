package com.expression_parser_v2_0.console.library.functions;

import com.expression_parser_v2_0.console.core.NumberName;
import com.expression_parser_v2_0.console.core.Set;
import com.expression_parser_v2_0.console.library.Functions_Implementation;

import static com.expression_parser_v2_0.console.core.CONSTANTS.*;

import com.expression_parser_v2_0.console.core.Argument;

public class fun_1 extends Functions_Implementation{
    int resultFlag;
    Set st;

    public fun_1(NumberName numberName) {
        super(numberName);
    }

    @Override
    public String[] getFunctionNames() {
        return new String[]{"fun"};
    }

    @Override
    public int[] getFunctionMap() {
        return new int[]{ARGUMENT_SET};
    }

    @Override
    public int getId() {
        return 1;
    }

    @Override
    public int getResultFlag() {
        return resultFlag;
    }

    @Override
    public Set getSet() {
        return st;
    }

    @Override
    public void function(Argument[] arguments, int id) {
        Set st = arguments[0].getSetArgument();
        resultFlag = RESULT_SET;
        this.st = st;
    }
}
