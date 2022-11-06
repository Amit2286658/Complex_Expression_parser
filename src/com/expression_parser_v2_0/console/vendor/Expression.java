package com.expression_parser_v2_0.console.vendor;

import com.expression_parser_v2_0.console.core.NumberName;
import com.expression_parser_v2_0.console.core.Parser;

public class Expression {
    private final Parser parser = new Parser();
    private final NumberName numberName = new NumberName();
    

    public Parser getParser(){
        return parser;
    }

    public NumberName getNumberNameParser(){
        return numberName;
    }
}
