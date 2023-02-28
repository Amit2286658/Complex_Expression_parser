package com.expression_parser_v2_0.console.library.functions;

import com.expression_parser_v2_0.console.core.types.Argument;
import com.expression_parser_v2_0.console.library.Functions_Implementation;
import static com.expression_parser_v2_0.console.core.CONSTANTS.*;

public class LinearEquations extends Functions_Implementation{

    public LinearEquations(){
        super();
    }

    @Override
    public String[] getFunctionNames() {
        return new String[]{"LinearEquations", "LinearEq"};
    }

    @Override
    public int[] getFunctionMap() {
        return new int[]{ARRAY, VARIABLE};
    }

    @Override
    public int getId() {
        return 1;
    }

    @Override
    public void function(Argument[] arguments, int id) {
        for(Argument arg : arguments){
            System.out.println(arg.getVarArgument().getIdentifier());
        }
    }
}
