package com.expression_parser_v2_0.console.library.functions;

import com.expression_parser_v2_0.console.core.Argument;
import com.expression_parser_v2_0.console.core.Set;
import com.expression_parser_v2_0.console.library.Functions_Implementation;

import static com.expression_parser_v2_0.console.core.constants.*;

public class Set_Union extends Functions_Implementation {

    private Set set;
    private int resultFlag;


    public Set_Union() {
        super();
    }

    @Override
    public String[] getFunctionNames() {
        return new String[]{"Union"};
    }

    @Override
    public int[] getFunctionMap() {
        return new int[]{ARGUMENT_ARRAY, ARGUMENT_SET};
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
        return set;
    }

    @Override
    public void function(Argument[] arguments, int id) {
        int count = 0;
        set = new Set();
        Set firstSet = arguments[count].getSetArgument();
        while(firstSet.hasNext(ELEMENT_REAL))
            this.set.pushReal(firstSet.pullReal());

        count++;
        while(count < arguments.length) {
            union(arguments[count].getSetArgument());
            count++;
        }
        resultFlag = RESULT_SET;
    }

    private void union(Set set){
        while(set.hasNext(ELEMENT_REAL)){
            double real = set.pullReal();
            boolean check = this.set.containsReal(real);
            if (!check)
                this.set.pushReal(real);
        }
    }
}
