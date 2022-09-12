package com.expression_parser_v2_0.console.vendor;

import com.expression_parser_v2_0.console.core.Parser;

public class Expression {
    private final Parser main = new Parser();
    public String Evaluate(String expression){
        return main.Evaluate(expression);
    }
}
