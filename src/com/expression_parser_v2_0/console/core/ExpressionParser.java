package com.expression_parser_v2_0.console.core;

import static com.expression_parser_v2_0.console.core.Global.*;
import static com.expression_parser_v2_0.console.core.Utility.convertSetToString;
import static com.expression_parser_v2_0.console.core.CONSTANTS.*;
import static com.expression_parser_v2_0.console.core.tokens.*;

//no inheritance.
public final class ExpressionParser {
    /*
    *my keyboard got smacked last night because something happened..., never mind, these keys
    *don't work any longer, and I'm keeping it here just so that I don't have to open
    *the character map every time.

    \ -> reverse slash, for line breaking when you're feeling lazy to just add another println.
    | -> latin iota (is that what it's called?), for char "or" condition.
    || -> double latin iota(still in doubt), for string "or" condition.
    */
    private final Stack<DispatchParcel> pointerOutput = new Stack<>();

    private Stack<functionsInterface> functions = null;
    private Stack<operationsInterface> operations = null;

    //a one way operation
    public void enableGlobal(){
        functions = getFunctions();
        operations = getOperations();
    }

    public void setFeatures(Stack<operationsInterface> operations,
        Stack<functionsInterface> functions){
        if (this.operations == null && this.functions == null){
            this.operations = operations;
            this.functions = functions;
        }else 
            throw new ExpressionException("method is invalid for this insatnce");
    }

    public String Evaluate(String expression){
        if ((this.functions == null || this.functions.isEmpty()) && 
            (this.operations == null || this.operations.isEmpty()))
            return expression;
        if (expression.length() == 0)
            throw new IllegalArgumentException("given input string is empty");
        
        if (tokens.containsToken(expression))
            throw new ExpressionException("the expression contains tokens that are restricted");

        expression = GeneralParser(expression);
        expression = implicitSolver(expression);
        expression = functionHandler(expression);
        expression = complexConverter(expression);
        Stack<String> output = postFixConverter(expression);
        output = postFixEvaluator(output);
        return SortResult(output);
    }

    //general parser to resolve minor errors in a string.
    private String GeneralParser(String expression){
        expression = expression.trim();

        StringBuilder builder = new StringBuilder();
        boolean isInQuote = false;
        for(int i = 0; i < expression.length(); i++){
            char c = expression.charAt(i);
            if (c == '"')
                isInQuote = !isInQuote;

            if ((Character.isWhitespace(c)) && !isInQuote)
                continue;

            builder.append(c);
        }

        expression = builder.toString();

        //I have to find a better approach for god's sake.
        expression = expression.replaceAll("\\)\\(", ")*(");
        expression = expression.replaceAll("}\\{", "}*{");
        expression = expression.replaceAll("\"\"", "\"*\"");
        expression = expression.replaceAll("\\+\\+", "\\+");
        expression = expression.replaceAll("\\+-", "-");
        expression = expression.replaceAll("-\\+", "-");
        expression = expression.replaceAll("--", "\\+");
        expression = expression.replaceAll("i\\(", "i*(");
        expression = expression.replaceAll("i\\{", "i*{");
        expression = expression.replaceAll("i\"", "i*\"");
        expression = expression.replaceAll("\\)\\{", ")*{");
        expression = expression.replaceAll("}\\(", "}*(");
        expression = expression.replaceAll("\\)\"", ")*\"");
        expression = expression.replaceAll("\"\\(", "\"*(");
        expression = expression.replaceAll("}\"", "}*\"");
        expression = expression.replaceAll("\"\\{", "\"*{");


        Stack<String> fn_names = new Stack<>();
        functions.reset();
        while (functions.loop()){
            functionsInterface fnInt = functions.get();
            String[] name = fnInt.getFunctionNames();
            for(String nm : name){
                if (!fn_names.contains(nm)){
                    expression = expression.replace(nm, function_token + nm);
                    fn_names.push(nm);
                }
            }
        }

        //create name array
        builder.setLength(0);
        operations.reset();
        while (operations.loop()){
            operationsInterface opInt = operations.get();
            if (opInt.getOperationNames() != null) {
                for (String name : opInt.getOperationNames()){
                    if (!name.isEmpty()) {
                        builder.append(name);
                        builder.append(".");
                        builder.append(opInt.getOperator());
                        builder.append("?");
                    }
                }
            }
        }
        //sort name array according to their size in descending order,
        //using bubble sort? the name array,
        //cannot be that big to cause any significant performance drop.
        String[] list = builder.toString().split("\\?");
        for (int i = 0; i < list.length; i++){
            for (int j = i + 1; j < list.length; j++){
                String temp_i = list[i];
                String temp_j = list[j];
                if (temp_j.length() > temp_i.length()) {
                    list[i] = list[j];
                    list[j] = temp_i;
                }
            }
        }

        //new replacement block
        String[] blocks = expression.split(string_token + "");
        for(int i = 0; i < blocks.length; i += 2){
            for(String str : list){
                String[] contents = str.split("\\.");
                blocks[i] = blocks[i].replace(contents[0], contents[1]);
            }
        }
        builder.setLength(0);
        
        if (blocks.length == 1)
            //case when the blocks contains no string, then the size is equals 1
            builder.append(blocks[0]);
        else {
            //in any other condition, a generic logic would follow
            for (int i = 0; i < blocks.length; i++){
                builder.append(blocks[i]);
                builder.append(
                    i != (blocks.length - 1) ? 
                        string_token :
                        expression.charAt(expression.length() - 1) == string_token ?
                            string_token :
                            ""
                );
            }
        }

        expression = builder.toString();

        //looping in forward direction to check only the post operators.
        //operator identifiers does not exist yet, until I get to the complex converter.
        builder.setLength(0);
        char op = 0;
        boolean found = false;
        int built_count = 0;
        for (int i = 0; i < expression.length(); i++){
            char c = expression.charAt(i);
            if (!Character.isDigit(i)){
                if (!found) {
                    if (built_count > 0)
                        while(built_count != 0){
                            builder.append(')');
                            built_count--;
                        }
                    operationsInterface operator = getCharAsAnOperator(c);
                    if (operator != null && operator.getType() == TYPE_POST) {
                        op = c;
                        found = true;
                    }
                } else {
                    if (op == c) {
                        builder.append('(');
                        built_count++;
                    }
                }
            }else{
                if (found)
                    found = false;
            }
            builder.append(c);
        }
        while(built_count != 0) {
            builder.append(')');
            built_count--;
        }
        expression = builder.toString();

        builder.setLength(0);
        int stringCounter = 0;
        //only solve for the bracket related implicit multiplication
        for (int i = 0; i < expression.length(); i++){
            char c = expression.charAt(i);

            if (c == string_token){
                stringCounter += stringCounter > 0 ? -1 : 1;
            }

            char p = 0;
            if (i != 0)
                p = expression.charAt(i - 1);
            char a = 0;
            if (i != expression.length() - 1)
                a = expression.charAt(i + 1);

            if (Character.isDigit(c)) {
                if (p == ')' || p == '}' || (p == string_token && stringCounter == 0)) {
                    builder.append('*');
                }
                builder.append(c);
                if ((a == '(' || a == '{') || (a == string_token && stringCounter == 0)
                        || a == function_token) {
                    builder.append('*');
                }
            }else if(c == function_token){
                if (p == ')' || p == '}' || p == '"')
                    builder.append('*');
                builder.append(c);
            }else{
                builder.append(c);
            }
        }
        expression = builder.toString();
        return expression;
    }

    //ignore the duplicate expression warnings, it's just the way for the intellij
    //to say that it's better than us humans, no you're not, now shut up.
    //reason being that the string builder is cleared by the method of setLength(0),
    //which apparently is not recognized by the intellij, hence, it's clearly dumber.
    private String complexConverter(String expression){
        StringBuilder builder = new StringBuilder();
        StringBuilder currentStep = new StringBuilder();

        int stringCounter = 0;

        outer_loop :
        for (int i = 0; i < expression.length(); i++){
            String c = String.valueOf(expression.charAt(i));
            if (expression.charAt(i) == string_token){
                builder.append(c);
                if (stringCounter > 0)
                    stringCounter--;
                else
                    stringCounter++;
                continue;
            }
            if (stringCounter > 0){
                builder.append(c);
                continue;
            }
            if (c.matches("[1234567890.iIeE]")){
                char p = 0;
                if (i != 0)
                    p = expression.charAt(i - 1);
                if (c.matches("[.iIeE]")){
                    if ((p + "").matches("[1234567890]")){
                        currentStep.append(c);
                    }else
                        builder.append(c);
                }else{
                    currentStep.append(c);
                }
            }else{
                if (!currentStep.toString().equals("")){
                    //noinspection DuplicateExpressions
                    double value = Double.parseDouble(currentStep.toString().
                            replaceAll("i", ""));
                    if (currentStep.toString().contains("i")){
                        builder.append(convertToComplexString(value, true));
                    }else{
                        builder.append(convertToComplexString(value, false));
                    }
                    currentStep.setLength(0);
                }
                if (c.matches("[+-]")){
                    char a = 0, p = 0;
                    if (i != 0)
                        p = expression.charAt(i - 1);
                    if (i != expression.length() - 1)
                        a = expression.charAt(i + 1);

                    if (i == 0){
                        builder.append(convertComplexToString(new ComplexNumber())).append(c);
                        continue;
                    }
                    if (p == '(' || p == '{'){
                        //a negation or the reverse of it, whatever may it be called.
                        /*if a negative operator is found, that means the intention is negation
                        Otherwise, the intention is to leave the term as it is,
                        I'm going with the option to just let the implementation handle
                        the both cases.*/
                        if (a == '(' || a == '{'){
                            builder.append(convertComplexToString(new ComplexNumber()));
                            builder.append(c);
                        } else {
                            currentStep.append(c);
                        }
                        continue;
                    }

                    operations.reset();
                    while(operations.loop()){
                        operationsInterface opInt = operations.get();
                        if (opInt.getOperator() == p){
                            if (opInt.getType() == TYPE_POST || opInt.getType() == TYPE_BOTH){
                                if (a == '(' || a == '{'){
                                    builder.append(convertComplexToString(
                                            new ComplexNumber())).append(c);
                                } else {
                                    currentStep.append(c);
                                }
                                continue outer_loop;
                            }
                            break;
                        }
                    }
                    builder.append(c);
                    continue;
                }
                builder.append(c);
            }
        }
        if (!currentStep.toString().equals("")){
            //noinspection DuplicateExpressions
            double value = Double.parseDouble(
                    currentStep.toString().replaceAll("i", ""));
            if (currentStep.toString().contains("i")){
                builder.append(convertToComplexString(value, true));
            }else{
                builder.append(convertToComplexString(value, false));
            }
        }
        return builder.toString();
    }

    //does not resolve any error, provide a valid string.
    private Stack<String> postFixConverter(String expression){
        StringBuilder builder = new StringBuilder();

        boolean tokenCounter = false;
        boolean functionCounter = false;
        boolean stringCounter = false;

        Stack<String> output = new Stack<>();
        Stack<String> operators = new Stack<>();

        outer_loop :
        for (int i = 0; i < expression.length(); i++){
            char c = expression.charAt(i);

            if (c == complex_token){
                if (!tokenCounter){
                    tokenCounter = true;
                }else{
                    tokenCounter = false;
                    output.push(builder.toString());
                    builder.setLength(0);
                }
            } else if (tokenCounter){
                builder.append(c);
            } else if (c == function_token){
                if (!functionCounter){
                    functionCounter = true;
                    builder.append(c);
                }
            } else if (functionCounter){
                char a = expression.charAt(i + 1);
                builder.append(c);
                if (a == '(') {
                    functionCounter = false;
                    operators.push(builder.toString());
                    builder.setLength(0);
                }
            } else if (c == string_token){
                builder.append(c);
                if (!stringCounter)
                    stringCounter = true;
                else {
                    stringCounter = false;
                    output.push(builder.toString());
                    builder.setLength(0);
                }
            } else if (stringCounter){
                builder.append(c);
            }
            else {
                if (c == ')'){
                    Popper('(', output, operators);
                }else if (c == '}'){
                    Popper('{', output, operators);
                    output.push(c + "");
                }
                else if (c == '(')
                    operators.push(c + "");
                else if (c == '{') {
                    output.push(c + "");
                    operators.push(c + "");
                }
                else{
                    if (c == ',') {
                        while (operators.hasNext()){
                            char t = operators.peek().charAt(0);
                            //exit at the nearest bracket.
                            if (t == '(' || t == '{'){
                                break;
                            } else {
                                String item = operators.pop();
                                output.push((item.charAt(0) != function_token ?
                                        operation_token : "") + item);
                            }
                        }
                        continue;
                    }

                    operationsInterface current_ops = getCharAsAnOperator(c);
                    if (operators.getPointerLocation() == -1) {
                        operators.push(c + "");
                        continue;
                    }
                    while (operators.hasNext()) {
                        char p = operators.peek().charAt(0);
                        if (p != '(' && p != '{') {
                            boolean isFun = p == function_token;
                            operationsInterface previous_ops = null;
                            if (!isFun)
                                previous_ops = getCharAsAnOperator(p);
                            int current_ops_precedence = current_ops.getPrecedence();
                            if (current_ops_precedence >= PRECEDENCE_FUNCTION)
                                throw new ExpressionException("an operation can't have precedence more than" +
                                        " or equal to ${PRECEDENCE_FUNCTION}");
                            if (current_ops_precedence <= (!isFun ? previous_ops.getPrecedence() :
                                    PRECEDENCE_FUNCTION)){
                                String item = operators.pop();
                                output.push((item.charAt(0) != function_token ?
                                        operation_token : "") + item);
                            }else{
                                operators.push(c + "");
                                continue outer_loop;
                            }
                        }else{
                            operators.push(c + "");
                            continue outer_loop;
                        }
                    }
                    //very important for whatever reason I forgot.
                    operators.push(c + "");
                }
            }
        }
        while(operators.hasNext()) {
            String item = operators.pop();
            output.push((item.charAt(0) != function_token ?
                    operation_token : "") + item);
        }

        output = output.reverse();
        builder.setLength(0);
        output.reset();
        while(output.loop()){
            builder.append(output.get()).append(",");
        }

        System.out.println("post fix string => " + builder);
        return output;
    }

    private Stack<String> postFixEvaluator(Stack<String> stack){
        Stack<String> output = new Stack<>();

        outer_loop :
        while(stack.hasNext()){
            String item = stack.pop();
            if (item.charAt(0) == function_token){
                StringBuilder fn_name_builder = new StringBuilder();
                for(int i = 1; i < item.length(); i++)
                    fn_name_builder.append(item.charAt(i));

                String fn_name = fn_name_builder.toString();

                int count = (int)convertToComplexNumber(output.pop()).real;
                int set_count = 0;
                Stack<String> sub_stack = new Stack<>();
                while(count != 0){
                    String sub_item = output.pop();
                    if (sub_item.equals("{"))
                        set_count--;
                    else if (sub_item.equals("}"))
                        set_count++;

                    sub_stack.push(sub_item);
                    if (set_count == 0)
                        count--;
                }

                functions.reset();
                while(functions.loop()){
                    functionsInterface fnInt = functions.get();
                    String[] name = fnInt.getFunctionNames();
                    for(String nm : name){
                        if (nm.equals(fn_name)) {
                            sub_stack.keepCount();
                            pointerOutput.keepCount();
                            try {
                                DispatchParcel parcel = functionDispatcher(sub_stack, fnInt);
                                pointerOutput.push(parcel);
                                output.push(pointer_token + "");
                                continue outer_loop;
                            } catch (Exception e){
                                sub_stack.resetToCount();
                                bracketReplacement(sub_stack);
                                pointerOutput.resetToCount();
                            }
                        }
                    }
                }
                throw new IllegalStateException("the given function name or" +
                        " argument doesn't quite add up");
            }else if(item.charAt(0) == operation_token){
                operationsInterface opInt = getCharAsAnOperator(item.charAt(1));
                switch (opInt.getType()) {
                    case TYPE_BOTH -> {
                        DispatchParcel parcel_left, parcel_right;
                        String right = output.pop();
                        if (right.equals(pointer_token + "")) {
                            parcel_right = pointerOutput.pop();
                        } else if (right.equals("}")) {
                            parcel_right = new DispatchParcel();
                            parcel_right.type = DISPATCHED_TYPE_SET;
                            parcel_right.set = setGiver(output);
                        } else {
                            parcel_right = convertStringToParcel(right);
                        }
                        String left = output.pop();
                        if (left.equals(pointer_token + "")) {
                            parcel_left = pointerOutput.pop();
                        } else if (left.equals("}")) {
                            parcel_left = new DispatchParcel();
                            parcel_left.type = DISPATCHED_TYPE_SET;
                            parcel_left.set = setGiver(output);
                        } else {
                            parcel_left = convertStringToParcel(left);
                        }
                        pointerOutput.push(tempoDispatcher(parcel_right, parcel_left, opInt));
                        output.push(pointer_token + "");
                    }
                    case TYPE_PRE -> {
                        DispatchParcel parcel_left_1;
                        String left_1 = output.pop();
                        if (left_1.equals(pointer_token + "")) {
                            parcel_left_1 = pointerOutput.pop();
                        } else if (left_1.equals("}")) {
                            parcel_left_1 = new DispatchParcel();
                            parcel_left_1.type = DISPATCHED_TYPE_SET;
                            parcel_left_1.set = setGiver(output);
                        } else {
                            parcel_left_1 = convertStringToParcel(left_1);
                        }
                        pointerOutput.push(tempoDispatcher(null, parcel_left_1, opInt));
                        output.push(pointer_token + "");
                    }
                    case TYPE_POST -> {
                        DispatchParcel parcel_right_1;
                        String right_1 = output.pop();
                        if (right_1.equals(pointer_token + "")) {
                            parcel_right_1 = pointerOutput.pop();
                        } else if (right_1.equals("}")) {
                            parcel_right_1 = new DispatchParcel();
                            parcel_right_1.type = DISPATCHED_TYPE_SET;
                            parcel_right_1.set = setGiver(output);
                        } else {
                            parcel_right_1 = convertStringToParcel(right_1);
                        }
                        pointerOutput.push(tempoDispatcher(parcel_right_1, null, opInt));
                        output.push(pointer_token + "");
                    }
                    case TYPE_CONSTANT -> {
                        pointerOutput.push(tempoDispatcher(null, null, opInt));
                        output.push(pointer_token + "");
                    }
                }
            }else{
                output.push(item);
            }
        }
        return output;
    }

    //this function always assumes that the opening bracket is not to be considered.
    private Set setGiver(Stack<String> stack){
        int set_count = 1;
        Stack<String> st = new Stack<>();
        while(set_count != 0){
            String str1 = stack.pop();
            if (str1.equals("}")) {
                set_count++;
            }
            if (str1.equals("{")) {
                set_count--;
                if (set_count == 0)
                    break;
            }
            st.push(str1);
        }
        st = st.reverse();
        return setHandler('{', '}', st);
    }

    private DispatchParcel convertStringToParcel(String str){
        ComplexNumber cn;
        DispatchParcel parcel = new DispatchParcel();

        if (str.charAt(0) == '\"' && str.charAt(str.length() - 1) == '\"'){
            parcel.type = DISPATCHED_TYPE_STRING;
            parcel.string = str;
            return parcel;
        }
        try{
            cn = convertToComplexNumber(str);
            parcel.type = DISPATCHED_TYPE_COMPLEX;
            parcel.number = cn;
        }catch(NumberFormatException e){
            e.printStackTrace();
        }
        return parcel;
    }

    private DispatchParcel tempoDispatcher(DispatchParcel parcel_right, DispatchParcel parcel_left,
                                                  operationsInterface ops){
        DispatchParcel result = null;
        if (parcel_right != null && parcel_left != null){
            switch (parcel_right.type) {
                case DISPATCHED_TYPE_COMPLEX -> result = switch (parcel_left.type) {
                    case DISPATCHED_TYPE_COMPLEX -> dispatcher(parcel_left.number, parcel_right.number,
                            null, null, null, null, ops);
                    case DISPATCHED_TYPE_STRING -> dispatcher(null, parcel_right.number,
                            null, null, parcel_left.string, null, ops);
                    case DISPATCHED_TYPE_SET -> dispatcher(null, parcel_right.number,
                            parcel_left.set, null, null, null, ops);
                    default -> throw new NullPointerException("the returned result is null, returned an empty " +
                            "object if that is what intended");
                };
                case DISPATCHED_TYPE_STRING -> result = switch (parcel_left.type) {
                    case DISPATCHED_TYPE_COMPLEX -> dispatcher(parcel_left.number, null,
                            null, null, null, parcel_right.string, ops);
                    case DISPATCHED_TYPE_STRING -> dispatcher(null, null,
                            null, null, parcel_left.string, parcel_right.string, ops);
                    case DISPATCHED_TYPE_SET -> dispatcher(null, null,
                            parcel_left.set, null, null, parcel_right.string, ops);
                    default -> throw new NullPointerException("the returned result is null, returned an empty " +
                            "object if that is what intended");
                };
                case DISPATCHED_TYPE_SET -> result = switch (parcel_left.type) {
                    case DISPATCHED_TYPE_COMPLEX -> dispatcher(parcel_left.number, null,
                            null, parcel_right.set, null, null, ops);
                    case DISPATCHED_TYPE_STRING -> dispatcher(null, null,
                            null, parcel_right.set, parcel_left.string, null, ops);
                    case DISPATCHED_TYPE_SET -> dispatcher(null, null,
                            parcel_left.set, parcel_right.set, null, null, ops);
                    default -> throw new NullPointerException("the returned result is null, returned an empty " +
                            "object if that is what intended");
                };
            }
        }else if (parcel_right != null){
            result = switch (parcel_right.type) {
                case DISPATCHED_TYPE_COMPLEX -> dispatcher(null, parcel_right.number, null, null,
                        null, null, ops);
                case DISPATCHED_TYPE_STRING -> dispatcher(null, null, null, null,
                        null, parcel_right.string, ops);
                case DISPATCHED_TYPE_SET -> dispatcher(null, null, null, parcel_right.set,
                        null, null, ops);
                default -> throw new NullPointerException("the returned result is null, returned an empty " +
                        "object if that is what intended");
            };
        }else if (parcel_left != null){
            result = switch (parcel_left.type) {
                case DISPATCHED_TYPE_COMPLEX -> dispatcher(parcel_left.number, null, null, null,
                        null, null, ops);
                case DISPATCHED_TYPE_STRING -> dispatcher(null, null, null, null,
                        parcel_left.string, null, ops);
                case DISPATCHED_TYPE_SET -> dispatcher(null, null, parcel_left.set, null,
                        null, null, ops);
                default -> throw new NullPointerException("the returned result is null, returned an empty " +
                        "object if that is what intended");
            };
        }else {
            result = dispatcher(null, null, null, null,
                    null, null, ops);
        }
        return result;
    }

    private String functionHandler(String expression){
        expression = functionUpdater(expression);

        Stack<String> fn_names = new Stack<>();
        functions.reset();
        while (functions.loop()){
            functionsInterface fnInt = functions.get();
            String[] name = fnInt.getFunctionNames();
            for(String nm : name){
                if (!fn_names.contains(nm)){
                    expression = expression.replace(nm, function_token + nm);
                    fn_names.push(nm);
                }
            }

        }
        return expression;
    }

    private String functionUpdater(String expression){
        StringBuilder builder = new StringBuilder();

        boolean param_scan = false;
        int bracket_counter = 0;
        int param_count = 0;
        boolean isInFunction = false;

        boolean isInSet = false;
        boolean isInString = false;
        int setCount = 0;

        for (int i = 0; i < expression.length(); i++){
            char c = expression.charAt(i);
            if (c == function_token && bracket_counter == 0){
                isInFunction = true;
                continue;
            }else if (isInFunction){
                if (expression.charAt(i + 1) == '('){
                    isInFunction = false;
                    param_scan = true;
                }
            }
            if (c == '"'){
                isInString = !isInString;
            }else if (c == '{'){
                isInSet = true;
                setCount++;
            }else if (c == '}'){
                isInSet = --setCount != 0;
            }
            builder.append(c);
            if (param_scan){
                if (c == '(')
                    bracket_counter++;
                else if (c == ')') {
                    bracket_counter--;
                    if (bracket_counter == 0){
                        param_scan = false;
                        builder.append(++param_count);
                        param_count = 0;
                    }
                }
                else if (c ==',' && bracket_counter == 1 && !isInSet && !isInString)
                    param_count++;
            }

        }
        expression = builder.toString();

        if (builder.toString().contains(function_token + ""))
            expression = functionUpdater(expression);

        return expression;
    }

    private Set setHandler(char opening_bracket, char closing_bracket, Stack<String> stack){
        if (stack.getLength() == 0){
            return new Set();
        }
        stack = stack.reverse();
        Stack<String> newSet = new Stack<>();
        StringBuilder newString = new StringBuilder();
        Set set = new Set();

        while(stack.hasNext()) {
            String item = stack.pop();
            if (item.charAt(0) == opening_bracket) {
                int count = 1;
                while(count != 0){
                    String newItem = stack.pop();
                    if (newItem.charAt(0) == opening_bracket){
                        newSet.push(newItem);
                        count++;
                    }else if (newItem.charAt(0) == closing_bracket){
                        count--;
                        if (count != 0)
                            newSet.push(newItem);
                        else
                            break;
                    }else
                        newSet.push(newItem);
                }
                set.pushSet(setHandler(opening_bracket, closing_bracket, newSet));
                continue;
            }
            if (item.charAt(0) == string_token){
                if (item.charAt(item.length() - 1) != string_token){
                    throw new IllegalArgumentException("not a proper string");
                }
                newString.setLength(0);
                for(int j = 1; j < item.length() - 1; j++)
                    newString.append(item.charAt(j));
                set.pushString(newString.toString());
                continue;
            }
            if (item.charAt(0) == pointer_token){
                DispatchParcel parcel = pointerOutput.pop();
                switch (parcel.type) {
                    case DISPATCHED_TYPE_SET -> set.pushSet(parcel.set);
                    case DISPATCHED_TYPE_COMPLEX -> {
                        ComplexNumber cn = parcel.number;
                        if (cn.real != 0 && cn.iota != 0)
                            set.pushComplex(cn);
                        else if (cn.real != 0)
                            set.pushReal(cn.real);
                        else if (cn.iota != 0)
                            set.pushIota(cn.iota);
                    }
                    case DISPATCHED_TYPE_STRING -> set.pushString(parcel.string);
                }
                continue;
            }
            try {
                ComplexNumber cn = convertToComplexNumber(item);
                if (cn.real != 0 && cn.iota != 0)
                    set.pushComplex(cn);
                else if (cn.real != 0)
                    set.pushReal(cn.real);
                else if (cn.iota != 0)
                    set.pushIota(cn.iota);
            } catch (Exception e) {
                throw new IllegalArgumentException(e.getMessage());
            }
        }
        return set;
    }

    private String implicitSolver(String expression){
        StringBuilder builder = new StringBuilder();
        boolean isInString = false;

        outer_loop :
        for (int i = 0; i < expression.length(); i++){
            char c = expression.charAt(i);

            if (c == string_token){
                isInString = !isInString;
                builder.append(c);
                continue;
            }

            if (isInString){
                builder.append(c);
                continue;
            }

            char p = 0;
            if (i != 0)
                p = expression.charAt(i - 1);
            char a = 0;
            if (i != expression.length() - 1)
                a = expression.charAt(i + 1);

            operations.reset();
            while (operations.loop()){
                operationsInterface opInt = operations.get();
                if (opInt.getOperator() == c) {
                    builder.append(getImplicitExp(opInt, p, a));
                    continue outer_loop;
                }
            }
            builder.append(c);
        }
        return builder.toString();
    }

    private void Popper(char bracket, Stack<String> output, Stack<String> operators){
        while(operators.hasNext()){
            char p = operators.peek().charAt(0);
            if (p != bracket) {
                String op = operators.pop();
                output.push(op.charAt(0) != function_token ? operation_token + op + "" : op);
            } else {
                operators.pop();
                break;
            }
        }
    }

    private String getImplicitExp(operationsInterface opInt, char left, char right){
        //no need to check for either a dot '.', or exponent ';', in each case,
        //they must end with a number, or else they'll be invalid.
        //only left operand can have an iota 'i'.
        boolean l = (left + "").matches("[1234567890)i}\"]");
        boolean r = (right + "").matches("[1234567890(@{\"]");
        

        if (l && r){
            switch (opInt.getType()){
                case TYPE_BOTH :
                    return opInt.getOperator() + "";
                case TYPE_PRE :
                    return opInt.getOperator() + "*";
                case TYPE_POST :
                    return "*" + opInt.getOperator();
                case TYPE_CONSTANT :
                    return "*" + opInt.getOperator() + "*";
            }
        }else if (l){
            //the right one is guaranteed to be an operator or (a closing bracket or empty).
            operationsInterface right_ops = getCharAsAnOperator(right);
            int precedence_current = opInt.getPrecedence();
            int precedence_right = right_ops != null ? right_ops.getPrecedence() : -1;
            switch (opInt.getType()) {
                case TYPE_BOTH -> {
                    if (right_ops == null)
                        throw new ExpressionException("the operator type is both, however the right operand is" +
                                " a closing bracket");
                    switch (right_ops.getType()) {
                        case TYPE_BOTH:
                            if (right_ops.getOperator() == '+' || right_ops.getOperator() == '-')
                                return opInt.getOperator() + "";
                        case TYPE_PRE:
                            throw new ExpressionException("the right operand is of type pre or" +
                                    " both while the left operator" +
                                    " is of type both, they both need an operand in between to work.");
                        case TYPE_POST:
                        case TYPE_CONSTANT:
                            if (precedence_current >= precedence_right)
                                throw new ExpressionException("the right operator is of type post or constant" +
                                        " while the left operator " +
                                        "is of type both, but the left operator's precedence " +
                                        "is greater than or equal to " +
                                        "so the left operator will require an operand to work.");
                            else
                                return opInt.getOperator() + "";
                        default:
                            throw new ExpressionException("unknown type");
                    }
                }
                case TYPE_PRE -> {
                    if (right_ops == null)
                        return opInt.getOperator() + "";
                    switch (right_ops.getType()) {
                        case TYPE_BOTH:
                        case TYPE_PRE:
                            if (precedence_right > precedence_current)
                                throw new ExpressionException("the right operator is of type both or pre" +
                                        " and has a greater precedence than the left operator.");
                            else
                                return opInt.getOperator() + "";
                        case TYPE_POST:
                        case TYPE_CONSTANT:
                            return opInt.getOperator() + "*";
                        default:
                            throw new ExpressionException("unknown type");
                    }
                }
                case TYPE_POST -> {
                    if (right_ops == null)
                        throw new ExpressionException("the type is post, however the right operand is a " +
                                "closing bracket.");
                    switch (right_ops.getType()) {
                        case TYPE_BOTH:
                            if (right_ops.getOperator() == '+' || right_ops.getOperator() == '-')
                                return opInt.getOperator() + "";
                        case TYPE_PRE:
                            throw new ExpressionException("the right operator is of type both and the left operator" +
                                    " is of type post, an operand in between is required regardless of the precedence");
                        case TYPE_POST:
                            if (precedence_current > precedence_right)
                                throw new ExpressionException("the left and right operator, both is of type post, " +
                                        "but the left operator has a greater precedence.");
                            else
                                return "*" + opInt.getOperator();
                        case TYPE_CONSTANT:
                            if (precedence_current >= precedence_right)
                                throw new ExpressionException("the left and right operator, both is of type " +
                                        "constant, but the left operator has a greater precedence.");
                            else
                                return "*" + opInt.getOperator();
                        default:
                            throw new ExpressionException("unknown type");
                    }
                }
                case TYPE_CONSTANT -> {
                    if (right_ops == null)
                        return "*" + opInt.getOperator() + "";
                    switch (right_ops.getType()) {
                        case TYPE_BOTH:
                        case TYPE_PRE:
                            if (precedence_right > precedence_current)
                                throw new ExpressionException("the right operator is either of type pre or both " +
                                        "and has a greater precedence than the left one.");
                            else
                                return "*" + opInt.getOperator();
                        case TYPE_POST:
                        case TYPE_CONSTANT:
                            return "*" + opInt.getOperator() + "*";
                        default:
                            throw new ExpressionException("unknown type");
                    }
                }
            }
        }else if (r) {
            //the left char is guaranteed to be an operator or (an opening bracket or empty).
            operationsInterface left_ops = getCharAsAnOperator(left);
            int precedence_current = opInt.getPrecedence();
            int precedence_left = left_ops != null ? left_ops.getPrecedence() : -1;
            switch (opInt.getType()) {
                case TYPE_BOTH -> {
                    if (opInt.getOperator() == '+' || opInt.getOperator() == '-')
                        return opInt.getOperator() + "";
                    if (left_ops == null)
                        if (opInt.getOperator() != '+' || opInt.getOperator() != '-')
                            throw new ExpressionException("the operator is of type both but the left operand" +
                                    " is an opening bracket");
                        else
                            return opInt.getOperator() + "";
                    switch (left_ops.getType()) {
                        case TYPE_PRE:
                        case TYPE_CONSTANT:
                            if (precedence_current <= precedence_left)
                                return opInt.getOperator() + "";
                        default:
                            return opInt.getOperator() + "";
                    }
                }
                case TYPE_PRE -> {
                    if (left_ops == null)
                        throw new ExpressionException("the operator is of type pre but the left operand" +
                                " is an opening bracket");
                    switch (left_ops.getType()) {
                        case TYPE_PRE:
                            if (precedence_current <= precedence_left)
                                return opInt.getOperator() + "*";
                        case TYPE_CONSTANT:
                            if (precedence_current <= precedence_left)
                                return opInt.getOperator() + "";
                        default:
                            return opInt.getOperator() + "*";
                    }
                }
                case TYPE_POST -> {
                    if (left_ops == null)
                        return "" + opInt.getOperator();
                    switch (left_ops.getType()) {
                        case TYPE_BOTH:
                            if (precedence_left < precedence_current)
                                return opInt.getOperator() + "";
                        case TYPE_POST:
                            if (precedence_left <= precedence_current)
                                return opInt.getOperator() + "";
                        case TYPE_CONSTANT:
                            return opInt.getOperator() + "";
                        default:
                            return opInt.getOperator() + "";
                    }
                }
                case TYPE_CONSTANT -> {
                    if (left_ops == null)
                        return "" + opInt.getOperator() + "*";
                    switch (left_ops.getType()) {
                        case TYPE_BOTH:
                        case TYPE_POST:
                            if (precedence_left < precedence_current)
                                return opInt.getOperator() + "*";
                        case TYPE_PRE:
                        case TYPE_CONSTANT:
                            return opInt.getOperator() + "*";
                        default:
                            return opInt.getOperator() + "*";
                    }
                }
            }
        }else {
            //both left and right char are guaranteed to be an operator or a set of an opening and a closing bracket.
            operationsInterface left_operator, right_operator;
            left_operator = getCharAsAnOperator(left);
            right_operator = getCharAsAnOperator(right);

            int precedence_current = opInt.getPrecedence();
            int precedence_left = left_operator != null ? left_operator.getPrecedence() : -1;
            int precedence_right = right_operator != null ? right_operator.getPrecedence() : -1;

            if (left_operator == null && right_operator == null){
                return opInt.getOperator() + "";
            }else if (left_operator == null){
                switch(opInt.getType()){
                    case TYPE_BOTH :
                    case TYPE_PRE :
                        throw new ExpressionException("the operator has type pre or both but no left operand found");
                    case TYPE_POST :
                        switch(right_operator.getType()){
                            case TYPE_BOTH :
                                if (right_operator.getOperator() == '+' || right_operator.getOperator() == '-')
                                    return opInt.getOperator() + "";
                            case TYPE_PRE :
                                throw new ExpressionException("the right operator has type both or pre, while" +
                                        " the left operator type post");
                            case TYPE_POST :
                                if (precedence_current > precedence_right)
                                    throw new ExpressionException("the right operator has type post while " +
                                            "the left operator has type post but has a greater precedence");
                                else
                                    return opInt.getOperator() + "";
                            case TYPE_CONSTANT :
                                if (precedence_current >= precedence_right)
                                    throw new ExpressionException("the right operator has type constant however has" +
                                            " less than or equal to the precedence of the" +
                                            " left operator which has type post");
                                else
                                    return opInt.getOperator() + "";
                        }
                    case TYPE_CONSTANT :
                        switch(right_operator.getType()){
                            case TYPE_BOTH :
                            case TYPE_PRE :
                                if (precedence_right > precedence_current)
                                    throw new ExpressionException("the right operator has type pre or both has " +
                                            "greater precedence than the left operator which has type constant");
                                else
                                    return opInt.getOperator() + "";
                            case TYPE_POST :
                            case TYPE_CONSTANT :
                                return opInt.getOperator() + "*";
                        }
                }
            }else if (right_operator == null){
                switch(opInt.getType()){
                    case TYPE_BOTH :
                        if (left_operator.getOperator() == '+' || left_operator.getOperator() == '-')
                            return opInt.getOperator() + "";
                    case TYPE_POST :
                        throw new ExpressionException("the operator has type post or both but no right operand found");
                    case TYPE_PRE :
                        switch(left_operator.getType()){
                            case TYPE_PRE :
                                if (precedence_current <= precedence_left)
                                    return opInt.getOperator() + "";
                            case TYPE_CONSTANT :
                                if (precedence_current <= precedence_left)
                                    return opInt.getOperator() + "";
                            default :
                                return opInt.getOperator() + "";
                        }

                    case TYPE_CONSTANT :
                        switch(left_operator.getType()){
                            case TYPE_BOTH :
                            case TYPE_POST :
                                if (precedence_left < precedence_current)
                                    return opInt.getOperator() + "";
                            case TYPE_PRE :
                            case TYPE_CONSTANT :
                                return opInt.getOperator() + "";
                            default :
                                return opInt.getOperator() + "";
                        }
                }
            }else {
                //both side is an operation, and I have no idea how will I deal with this.
                StringBuilder builder = new StringBuilder();
                switch (opInt.getType()) {
                    case TYPE_BOTH -> {
                        builder.append(opInt.getOperator());
                        switch (right_operator.getType()) {
                            case TYPE_BOTH:
                                if (right_operator.getOperator() == '+' || right_operator.getOperator() == '-')
                                    return opInt.getOperator() + "";
                            case TYPE_PRE:
                                throw new ExpressionException("the right operand is of type pre or both while" +
                                        " the left operator" +
                                        " is of type both, they both need an operand in between to work.");
                            case TYPE_POST:
                            case TYPE_CONSTANT:
                                if (precedence_current >= precedence_right)
                                    throw new ExpressionException("the right operator is of type post or constant" +
                                            " while the left operator " +
                                            "is of type both, but the left operator's precedence " +
                                            "is greater than or equal to " +
                                            "so the left operator will require an operand to work.");
                                break;
                        }
                    }
                    case TYPE_PRE -> {
                        builder.append(opInt.getOperator());
                        switch (right_operator.getType()) {
                            case TYPE_BOTH:
                            case TYPE_PRE:
                                if (precedence_right > precedence_current)
                                    throw new ExpressionException("the right operator is of type both or pre" +
                                            " and has a greater precedence than the left operator.");
                                break;
                            case TYPE_POST:
                            case TYPE_CONSTANT:
                                builder.append("*");
                                break;
                        }
                    }
                    case TYPE_POST -> {
                        builder.append(opInt.getOperator());
                        switch (right_operator.getType()) {
                            case TYPE_BOTH:
                                if (right_operator.getOperator() == '+' || right_operator.getOperator() == '-')
                                    return opInt.getOperator() + "";
                            case TYPE_PRE:
                                throw new ExpressionException("the right operator is of" +
                                        " type both and the left operator" +
                                        " is of type post, an operand in between is" +
                                        " required regardless of the precedence");
                            case TYPE_POST:
                                if (precedence_current > precedence_right)
                                    throw new ExpressionException("the left and right operator, " +
                                            "both is of type post, " +
                                            "but the left operator has a greater precedence.");
                                break;
                            case TYPE_CONSTANT:
                                if (precedence_current >= precedence_right)
                                    throw new ExpressionException("the left and right operator, both is of type " +
                                            "constant, but the left operator has a greater precedence.");
                                break;
                        }
                    }
                    case TYPE_CONSTANT -> {
                        builder.append(opInt.getOperator());
                        switch (right_operator.getType()) {
                            case TYPE_BOTH:
                            case TYPE_PRE:
                                if (precedence_right > precedence_current)
                                    throw new ExpressionException("the right operator is either of type pre or both " +
                                            "and has a greater precedence than the left one.");
                                break;
                            case TYPE_POST:
                            case TYPE_CONSTANT:
                                builder.append("*");
                                break;
                        }
                    }
                }
                return builder.toString();
            }
        }
        return opInt.getOperator() + "";
    }

    private DispatchParcel dispatcher(ComplexNumber complex_left, ComplexNumber complex_right, Set set_left,
                                             Set set_right, String str_left, String str_right,
                                             operationsInterface whichOperation){
        DispatchParcel parcel = new DispatchParcel();
        ComplexNumber number = new ComplexNumber(0, 0);

        boolean handle_complex = false;

        if (str_left != null)
            str_left = str_left.replace("\"", "");

        if (str_right != null)
            str_right = str_right.replace("\"", "");

        //only two of these parameters ever going to be non-null.
        //do not handle complex numbers in this block, I've already written the code below
        //and have no intention of writing it again.
        if (complex_left != null){
            if (set_right != null) {
                double real = complex_left.real;
                double iota = complex_left.iota;
                if (real != 0 && iota != 0)
                    whichOperation.function(complex_left, set_right);
                else if (real != 0)
                    whichOperation.function(real, set_right, IOTA_FALSE);
                else if (iota != 0)
                    whichOperation.function(iota, set_right, IOTA_TRUE);
                else
                    whichOperation.function(complex_left, set_right);
            }
            else if (str_right != null) {
                double real = complex_left.real;
                double iota = complex_left.iota;
                if (real != 0 && iota != 0)
                    whichOperation.function(complex_left, str_right);
                else if (real != 0)
                    whichOperation.function(real, str_right, IOTA_FALSE);
                else if (iota != 0)
                    whichOperation.function(iota, str_right, IOTA_TRUE);
                else
                    whichOperation.function(complex_left, str_right);
            }
            else
                //whether c2 is also not null or is null doesn't matter in this block, the codes below
                //can handle all cases regarding complex numbers;
                handle_complex = true;
        }else if (complex_right != null){
            if (set_left != null) {
                double real = complex_right.real;
                double iota = complex_right.iota;
                if (real != 0 && iota != 0)
                    whichOperation.function(set_left, complex_right);
                else if (real != 0)
                    whichOperation.function(set_left, real, IOTA_FALSE);
                else if (iota != 0)
                    whichOperation.function(set_left, iota, IOTA_TRUE);
                else
                    whichOperation.function(set_left, complex_right);
            }
            else if (str_left != null) {
                double real = complex_right.real;
                double iota = complex_right.iota;
                if (real != 0 && iota != 0)
                    whichOperation.function(str_left, complex_right);
                else if (real != 0)
                    whichOperation.function(str_left, real, IOTA_FALSE);
                else if (iota != 0)
                    whichOperation.function(str_left, iota, IOTA_TRUE);
                else
                    whichOperation.function(str_left, complex_right);
            }
            else
                handle_complex = true;
        }else if (set_left != null){
            if (set_right != null)
                whichOperation.function(set_left, set_right);
            else if (str_right != null)
                whichOperation.function(set_left, str_right);
            else
                whichOperation.function(set_left);
        }else if (set_right != null){
            if (str_left != null)
                whichOperation.function(str_left, set_right);
            else
                whichOperation.function(set_right);
        }else if (str_left != null){
            if (str_right != null)
                whichOperation.function(str_left, str_right);
            else
                whichOperation.function(str_left);
        }else if (str_right != null){
            whichOperation.function(str_right);
        }else {
            whichOperation.function();
        }

        //is checking for the types even necessary? like if the type is post,
        //the c1 naturally is going to be null, or if the type is pre, the c2 will be null.
        //but it's a lot of refactoring, so I'll leave it as it is.
        if (handle_complex) {
            double
                    left_real = complex_left != null ? complex_left.real : 0,
                    left_iota = complex_left != null ? complex_left.iota : 0,
                    right_real = complex_right != null ? complex_right.real : 0,
                    right_iota = complex_right != null ? complex_right.iota : 0;
            switch (whichOperation.getType()) {
                case TYPE_BOTH:
                    //under normal circumstances
                    if (left_real != 0 && left_iota == 0 && right_real != 0 && right_iota == 0)
                        whichOperation.function(left_real, right_real, IOTA_NONE);
                    else if (left_real == 0 && left_iota != 0 && right_real == 0 && right_iota != 0)
                        whichOperation.function(left_iota, right_iota, IOTA_BOTH);
                    else if (left_real != 0 && left_iota == 0 && right_real == 0 && right_iota != 0)
                        whichOperation.function(left_real, right_iota, IOTA_SECOND);
                    else if (left_real == 0 && left_iota != 0 && right_real != 0 && right_iota == 0)
                        whichOperation.function(left_iota, right_real, IOTA_FIRST);
                    else if (left_real != 0 && left_iota != 0 && right_real != 0 && right_iota == 0)
                        whichOperation.function(complex_left, right_real, IOTA_FALSE);
                    else if (left_real != 0 && left_iota != 0 && right_real == 0 && right_iota != 0)
                        whichOperation.function(complex_left, right_iota, IOTA_TRUE);
                    else if (left_real != 0 && left_iota == 0 && right_real != 0)
                        whichOperation.function(left_real, complex_right, IOTA_FALSE);
                    else if (left_real == 0 && left_iota != 0 && right_real != 0)
                        whichOperation.function(left_iota, complex_right, IOTA_TRUE);
                    else if (left_real != 0 && left_iota != 0 && right_real != 0)
                        whichOperation.function(complex_left, complex_right);
                    else if (left_real == 0 && left_iota == 0 && right_real == 0 && right_iota == 0)
                        whichOperation.function(left_real, right_real, IOTA_NONE);
                    else if (left_real != 0 && left_iota == 0)
                        whichOperation.function(left_real, right_real, IOTA_NONE);
                    else if (left_real == 0 && left_iota == 0 && right_real != 0 && right_iota == 0)
                        whichOperation.function(left_real, right_real, IOTA_NONE);
                    else if (left_real == 0 && left_iota != 0)
                        whichOperation.function(left_iota, right_real, IOTA_FIRST);
                    else if (left_real == 0 && right_real == 0)
                        whichOperation.function(left_real, right_iota, IOTA_SECOND);
                    else if (left_real == 0)
                        whichOperation.function(complex_left, complex_right);
                    else
                        whichOperation.function(complex_left, complex_right);
                    break;
                case TYPE_PRE:
                    if (left_real != 0 && left_iota != 0)
                        whichOperation.function(complex_left);
                    else if (left_real != 0)
                        whichOperation.function(left_real, IOTA_FALSE);
                    else if (left_iota != 0)
                        whichOperation.function(left_iota, IOTA_TRUE);
                    else
                        whichOperation.function(0, IOTA_FALSE);
                    break;
                case TYPE_POST:
                    if (right_real != 0 && right_iota != 0)
                        whichOperation.function(complex_right);
                    else if (right_real != 0)
                        whichOperation.function(right_real, IOTA_FALSE);
                    else if (right_iota != 0)
                        whichOperation.function(right_iota, IOTA_TRUE);
                    else
                        whichOperation.function(0, IOTA_FALSE);
                    break;
                case TYPE_CONSTANT:
                    whichOperation.function();
                    break;
            }
        }

        int result_flag = whichOperation.getResultFlag();

        switch (result_flag){
            case RESULT_REAL :
                number.real = whichOperation.getReal();
                parcel.type = DISPATCHED_TYPE_COMPLEX;
                parcel.number = number;
                break;
            case RESULT_IOTA :
                number.iota = whichOperation.getIota();
                parcel.type = DISPATCHED_TYPE_COMPLEX;
                parcel.number = number;
                break;
            case RESULT_COMPLEX :
                number = whichOperation.getComplex();
                parcel.type = DISPATCHED_TYPE_COMPLEX;
                parcel.number = number;
                break;
            case RESULT_SET :
                parcel.type = DISPATCHED_TYPE_SET;
                parcel.set = whichOperation.getSet();
                break;
            case RESULT_STRING :
                parcel.type = DISPATCHED_TYPE_STRING;
                parcel.string = whichOperation.getString();
            default: break;
        }

        return parcel;
    }

    //the caller should take care if the sub_expression is empty or not, the dispatcher
    //will not resolve any problems.
    private DispatchParcel functionDispatcher(Stack<String> stack, functionsInterface fs){
        bracketReplacement(stack);

        int[] map = fs.getFunctionMap();
        Stack<Argument> arguments;

        DispatchParcel parcel = new DispatchParcel();

        boolean isArray = false;

        if (map[0] == ARGUMENT_ARRAY){
            if (map.length != 2)
                throw new ExpressionException("the functions that expects an array can only accept one type" +
                        " of argument, in this case, the map declaration can only have two types," +
                        " the first declaring it an array and the second being the type," +
                        " whose array is being expected");
            isArray = true;
        }

        arguments = new Stack<>(isArray ? stack.getLength() : map.length);

        if (!isArray) {
            int counter = 0;
            while (stack.hasNext()) {
                int id = map[counter];
                String item = stack.pop();
                switch (id) {
                    case ARGUMENT_REAL :
                        if (item.equals(pointer_token + "")){
                            ComplexNumber cn = pointerOutput.peek().number;
                            if (cn == null)
                                throw new ExpressionException("arguments don't match");
                            if (cn.iota == 0){
                                arguments.push(new Argument(cn.real, 0,
                                        null, null, null));
                                pointerOutput.pop();
                                break;
                            }
                        } else {
                            ComplexNumber cn = convertToComplexNumber(item);
                            if(cn.iota == 0){
                                arguments.push(new Argument(cn.real, 0,
                                        null, null, null));
                                break;
                            }
                        }
                        throw new IllegalArgumentException("given argument does not match with the map");
                    case ARGUMENT_IOTA :
                        if (item.equals(pointer_token + "")){
                            ComplexNumber cn = pointerOutput.peek().number;
                            if (cn == null)
                                throw new ExpressionException("arguments don't match");
                            if (cn.real == 0){
                                arguments.push(new Argument(0, cn.iota,
                                        null, null, null));
                                pointerOutput.pop();
                                break;
                            }
                        } else {
                            ComplexNumber cn = convertToComplexNumber(item);
                            if(cn.real == 0){
                                arguments.push(new Argument(0, cn.iota,
                                        null, null, null));
                                break;
                            }
                        }
                        throw new IllegalArgumentException("given argument does not match with the map");
                    case ARGUMENT_COMPLEX :
                        if (item.equals(pointer_token + "")){
                            ComplexNumber cn = pointerOutput.peek().number;
                            if (cn == null)
                                throw new ExpressionException("arguments don't match");
                            arguments.push(new Argument(0, 0,
                                    cn, null, null));
                            pointerOutput.pop();
                        } else {
                            ComplexNumber cn = convertToComplexNumber(item);
                            arguments.push(new Argument(0, 0,
                                    cn, null, null));
                        }
                        break;
                    case ARGUMENT_STRING :
                        if (item.equals(pointer_token + "")){
                            String str = pointerOutput.peek().string;
                            if (str == null)
                                throw new ExpressionException("arguments don't match");
                            arguments.push(new Argument(0, 0,
                                    null, str, null));
                            pointerOutput.pop();
                            break;
                        } else if (item.charAt(0) == string_token) {
                            arguments.push(new Argument(0, 0,
                                    null, item.replace(string_token + "", ""), null));
                            break;
                        }
                        throw new IllegalArgumentException("not a string");
                    case ARGUMENT_SET :
                        if (item.equals(pointer_token + "")){
                            Set set = pointerOutput.peek().set;
                            if (set == null)
                                throw new ExpressionException("arguments don't match");
                            arguments.push(new Argument(0, 0,
                                    null, null, set));
                            pointerOutput.pop();
                        } else {
                            Set set = setGiver(stack);
                            arguments.push(new Argument(0, 0,
                                    null, null, set));
                        }
                        break;
                    case ARGUMENT_ARRAY :
                        throw new ExpressionException("the array declaration must be the first element");
                    default:
                        break;
                }
                counter++;
            }
        }else {
            int type = map[1];
            while (stack.hasNext()){
                String item = stack.pop();
                switch(type){
                    case ARGUMENT_REAL :
                        if (item.equals(pointer_token + "")){
                            ComplexNumber cn = pointerOutput.peek().number;
                            if (cn == null)
                                throw new ExpressionException("arguments don't match");
                            if (cn.iota == 0){
                                arguments.push(new Argument(cn.real, 0,
                                        null, null, null));
                                pointerOutput.pop();
                                break;
                            }
                        } else {
                            ComplexNumber cn = convertToComplexNumber(item);
                            if(cn.iota == 0){
                                arguments.push(new Argument(cn.real, 0,
                                        null, null, null));
                                break;
                            }
                        }
                        throw new IllegalArgumentException("given argument does not match with the map");
                    case ARGUMENT_IOTA :
                        if (item.equals(pointer_token + "")){
                            ComplexNumber cn = pointerOutput.peek().number;
                            if (cn == null)
                                throw new ExpressionException("arguments don't match");
                            if (cn.real == 0){
                                arguments.push(new Argument(0, cn.iota,
                                        null, null, null));
                                pointerOutput.pop();
                                break;
                            }
                        } else {
                            ComplexNumber cn = convertToComplexNumber(item);
                            if(cn.real == 0){
                                arguments.push(new Argument(0, cn.iota,
                                        null, null, null));
                                break;
                            }
                        }
                        throw new IllegalArgumentException("given argument does not match with the map");
                    case ARGUMENT_COMPLEX :
                        if (item.equals(pointer_token + "")){
                            ComplexNumber cn = pointerOutput.peek().number;
                            if (cn == null)
                                throw new ExpressionException("arguments don't match");
                            arguments.push(new Argument(0, 0,
                                    cn, null, null));
                            pointerOutput.pop();
                        } else {
                            ComplexNumber cn = convertToComplexNumber(item);
                            arguments.push(new Argument(0, 0,
                                    cn, null, null));
                        }
                        break;
                    case ARGUMENT_STRING :
                        if (item.equals(pointer_token + "")){
                            String str = pointerOutput.peek().string;
                            if (str == null)
                                throw new ExpressionException("arguments don't match");
                            arguments.push(new Argument(0, 0,
                                    null, str, null));
                            pointerOutput.pop();
                            break;
                        } else if (item.charAt(0) == string_token) {
                            arguments.push(new Argument(0, 0,
                                    null, item.replace(string_token + "", ""), null));
                            break;
                        }
                        throw new IllegalArgumentException("not a string");
                    case ARGUMENT_SET :
                        if (item.equals(pointer_token + "")){
                            Set set = pointerOutput.peek().set;
                            if (set == null)
                                throw new ExpressionException("arguments don't match");
                            arguments.push(new Argument(0, 0,
                                    null, null, set));
                            pointerOutput.pop();
                        } else {
                            Set set = setGiver(stack);
                            arguments.push(new Argument(0, 0,
                                    null, null, set));
                        }
                        break;
                    case ARGUMENT_ARRAY :
                        throw new ExpressionException("the array declaration must be the first" +
                                " and only element of its kind");
                    default:
                        break;
                }
            }
        }

        Argument[] args = new Argument[arguments.getLength()];
        int count = 0;
        arguments = arguments.reverse();
        while(arguments.hasNext()){
            args[count] = arguments.pop();
            count++;
        }
        fs.function(args, fs.getId());

        int resultFlag = fs.getResultFlag();
        ComplexNumber cn;
        switch (resultFlag) {
            case RESULT_REAL -> {
                cn = convertToComplexNumber(fs.getReal(), false);
                parcel.type = DISPATCHED_TYPE_COMPLEX;
                parcel.number = cn;
                return parcel;
            }
            case RESULT_IOTA -> {
                cn = convertToComplexNumber(fs.getIota(), true);
                parcel.type = DISPATCHED_TYPE_COMPLEX;
                parcel.number = cn;
                return parcel;
            }
            case RESULT_COMPLEX -> {
                cn = fs.getComplex();
                parcel.type = DISPATCHED_TYPE_COMPLEX;
                parcel.number = cn;
                return parcel;
            }
            case RESULT_SET -> {
                parcel.type = DISPATCHED_TYPE_SET;
                parcel.set = fs.getSet();
                return parcel;
            }
            case RESULT_STRING -> {
                parcel.type = DISPATCHED_TYPE_STRING;
                parcel.string = fs.getString();
                return parcel;
            }
            default -> throw new IllegalArgumentException("unknown type");
        }
    }

    private operationsInterface getCharAsAnOperator(char c){
        operationsInterface opInt = null;
        operations.reset();
        while (operations.loop()){
            operationsInterface ops = operations.get();
            if (ops.getOperator() == c) {
                opInt = ops;
                break;
            }
        }
        return opInt;
    }

    private String SortResult(Stack<String> result){
        ComplexNumber cn = null;
        boolean isComplex = false;

        String item = result.pop();
        if (item.equals(pointer_token + "")){
            DispatchParcel parcel = pointerOutput.pop();
            switch(parcel.type){
                case DISPATCHED_TYPE_COMPLEX :
                    isComplex = true;
                    cn = parcel.number;
                    break;
                case DISPATCHED_TYPE_STRING :
                    return parcel.string;
                case DISPATCHED_TYPE_SET :
                    return convertSetToString(parcel.set);
            }
        } else if (item.charAt(0) == string_token){
            item = item.replace(string_token + "", "");
            operations.reset();
            while(operations.loop()){
                operationsInterface opInt = operations.get();
                if (opInt.getOperator() == empty_token){
                    opInt.function(item);
                    int result_flag = opInt.getResultFlag();
                    switch(result_flag){
                        case RESULT_REAL -> {
                            return opInt.getReal() + "";
                        }
                        case RESULT_STRING -> {
                            return opInt.getString();
                        }
                        default -> {
                            return item;
                        }
                    }
                }
            }
            return item;
        } else if (item.equals("}")){
            Set set = setGiver(result);
            return convertSetToString(set);
        } else {
            cn = convertToComplexNumber(item);
            isComplex = true;
        }

        if (isComplex) {
            if (cn.real != 0 && cn.iota == 0) {
                if (cn.real % 1 == 0)
                    return String.valueOf((int) cn.real);
                else
                    return String.valueOf(cn.real);
            } else if (cn.real == 0 && cn.iota != 0) {
                if (cn.iota % 1 == 0)
                    return (int) cn.iota + "i";
                else
                    return cn.iota + "i";
            } else if (cn.real == 0) {
                return "0";
            } else {
                String builder = "";
                if (cn.real % 1 == 0)
                    builder += (cn.real + "").split("\\.")[0];
                else
                    builder += cn.real;
                if (cn.iota > 0)
                    builder += "+";
                if (cn.iota % 1 == 0)
                    builder += (cn.iota + "").split("\\.")[0] + "i";
                else
                    builder += cn.iota + "i";
                return builder;
            }
        }
        return "no result and only god knows why";
    }

    private void bracketReplacement(Stack<String> stack){
        stack.reset();
        while(stack.loop()){
            String str = stack.get();
            if (str.equals("{"))
                stack.replace("}");
            else if (str.equals("}"))
                stack.replace("{");
        }
    }

    private ComplexNumber convertToComplexNumber(String complexString){
        ComplexNumber number = new ComplexNumber();
        StringBuilder currentStep = new StringBuilder();

        for (int i = 0; i < complexString.length(); i++){
            char c = complexString.charAt(i);
            if (c == '+' || c == '-') {
                if (i != 0) {
                    number.real = Double.parseDouble(currentStep.toString());
                    currentStep.setLength(0);
                }
            }
            currentStep.append(c);
        }
        number.iota = Double.parseDouble(currentStep.toString().replaceAll("i", ""));

        return number;
    }

    private static ComplexNumber convertToComplexNumber(double value, boolean iota){
        return new ComplexNumber(!iota ? value : 0, iota ? value : 0);
    }

    private static String convertToComplexString(double value, boolean iota){
        return complex_token + (!iota ? value + "+" + 0 + "i" : 0 + (value < 0 ? "" : "+")
                + value + "i") + complex_token;
    }

    private static String convertComplexToString(ComplexNumber cn){
        return convertComplexToString(cn, true);
    }

    private static String convertComplexToString(ComplexNumber cn, @SuppressWarnings("SameParameterValue") boolean c_t){
        return (c_t ? complex_token : "") + "" + cn.real +
                (cn.iota >= 0 ? "+" : "") + cn.iota + "i" + (c_t ? complex_token : "");
    }

    private static final class DispatchParcel {
        private int type;
        private ComplexNumber number;
        private String string;
        private Set set;
    }
}
