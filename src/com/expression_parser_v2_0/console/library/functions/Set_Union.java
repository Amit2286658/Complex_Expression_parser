package com.expression_parser_v2_0.console.library.functions;

import com.expression_parser_v2_0.console.core.NumberName;
import com.expression_parser_v2_0.console.core.types.Argument;
import com.expression_parser_v2_0.console.core.types.Set;
import com.expression_parser_v2_0.console.library.Functions_Implementation;

import static com.expression_parser_v2_0.console.core.CONSTANTS.*;

public class Set_Union extends Functions_Implementation {

    private Set set;
    private int resultFlag;

    public Set_Union(NumberName numberName) {
        super(numberName);
    }

    @Override
    public String[] getFunctionNames() {
        return new String[]{"Union"};
    }

    @Override
    public int[] getFunctionMap() {
        return new int[]{ARRAY, SET};
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
        while(firstSet.hasNext(REAL))
            this.set.pushReal(firstSet.pullReal());

        count++;
        while(count < arguments.length) {
            union(arguments[count].getSetArgument());
            count++;
        }
        resultFlag = SET;
    }

    private void union(Set set){
        while(set.hasNext(REAL)){
            double real = set.pullReal();
            boolean check = this.set.containsReal(real);
            if (!check)
                this.set.pushReal(real);
        }
    }
}
