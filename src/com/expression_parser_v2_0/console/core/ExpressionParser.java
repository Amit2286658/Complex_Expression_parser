package com.expression_parser_v2_0.console.core;

import static com.expression_parser_v2_0.console.core.Global.*;
import static com.expression_parser_v2_0.console.core.Utility.*;
import static com.expression_parser_v2_0.console.core.CONSTANTS.*;
import static com.expression_parser_v2_0.console.core.tokens.*;

import com.expression_parser_v2_0.console.core.types.*;

//no inheritance. seriously why would anyone need to extend it, i see absolutely no reason.
public final class ExpressionParser {
    private final Stack<DispatchParcel> pointerOutput = new Stack<>();

    private Stack<functionsInterface> functions = null;
    private Stack<operationsInterface> operations = null;

    // a one way operation
    public void enableGlobal() {
        functions = getFunctions();
        operations = getOperations();
    }

    public void setFeatures(Stack<operationsInterface> operations,
            Stack<functionsInterface> functions) {
        if (this.operations == null && this.functions == null) {
            this.operations = operations;
            this.functions = functions;
        } else
            throw new ExpressionException("method is invalid for this instance");
    }

    public String Evaluate(String expression) {
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

    // general parser to resolve minor errors in a string.
    private String GeneralParser(String expression) {
        expression = expression.trim();

        StringBuilder builder = new StringBuilder();
        boolean isInQuote = false;
        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);
            if (c == '"')
                isInQuote = !isInQuote;

            if ((Character.isWhitespace(c)) && !isInQuote)
                continue;

            builder.append(c);
        }

        expression = builder.toString();

        /*
         * now there is a bug, take this for example AP_nFirst
         * here the AP_nFirst is a function, the AP is function too
         * it causes double function token to be inserted, it obviously is a problem
         * so now, there are two approaches I can take
         * first -> run a char by char loop and do every match manually
         * this approach is very tiresome, may lead to performance drop, I don't know
         * in this approach I'll just check if a function token already exists, if it
         * does then
         * I'll skip, for this I'll also have to sort the functions names by their size,
         * in descending order
         * 
         * the second approach -> in this approach, I'll leave it the way it is,
         * and at the end of the operation, I can simply check if there are consecutive
         * function token, it would indicate the problem and I then can get rid of the
         * excessive
         * tokens leaving only one behind. and the good thing is
         * I don't even need to sort the function names for this approach to work.
         * so, let's give it a try.
         * it worked, 2nd approach worked like a charm.
         */
        String[] strs = expression.split(string_token + "");
        // since the string has been split here, let's also do some minor
        // transformations
        // transfroation block
        {
            for (int i = 0; i < strs.length; i += 2) {
                strs[i] = strs[i].replaceAll("\\++", "\\+");
                strs[i] = strs[i].replaceAll("\\+-", "\\-");
                strs[i] = strs[i].replaceAll("\\-+", "\\-");
                strs[i] = strs[i].replaceAll("\\--", "\\+");
            }
        }
        Stack<String> fn_names = new Stack<>();
        functions.resetLoopAtPointer();
        while (functions.loop()) {
            functionsInterface fnInt = functions.get();
            String[] name = fnInt.getFunctionNames();
            for (String nm : name) {
                if (!fn_names.contains(nm)) {
                    for (int k = 0; k < strs.length; k += 2) {
                        strs[k] = strs[k].replace(nm, function_token + nm);
                    }
                    fn_names.push(nm);
                }
            }
        }
        if (strs.length == 1)
            expression = strs[0];
        else {
            builder.setLength(0);
            for (int i = 0; i < strs.length; i++) {
                builder.append(strs[i]);
                builder.append(
                        i != (strs.length - 1) ? string_token
                                : expression.charAt(expression.length() - 1) == 
                                    string_token ? string_token : "");
            }
            expression = builder.toString();
        }

        // approach 2, check for the consecutive function tokens
        builder.setLength(0);
        boolean fun_token_found = false;
        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);
            if (c == function_token && fun_token_found)
                // consecutive found, skip this step
                continue;
            else if (c == function_token) {
                builder.append(c);
                fun_token_found = true;
                continue;
            } else {
                if (fun_token_found)
                    fun_token_found = false;
                builder.append(c);
            }
        }
        expression = builder.toString();

        // create name array
        builder.setLength(0);
        operations.resetLoopAtPointer();
        while (operations.loop()) {
            operationsInterface opInt = operations.get();
            if (opInt.getOperationNames() != null) {
                for (String name : opInt.getOperationNames()) {
                    if (!name.isEmpty()) {
                        builder.append(name);
                        builder.append(".");
                        builder.append(opInt.getOperator());
                        builder.append("?");
                    }
                }
            }
        }
        // sort name array according to their size in descending order,
        // using bubble sort? the name array,
        // cannot be that big to cause any significant performance drop.
        String[] list = builder.toString().split("\\?");
        for (int i = 0; i < list.length; i++) {
            for (int j = i + 1; j < list.length; j++) {
                String temp_i = list[i];
                String temp_j = list[j];
                if (temp_j.length() > temp_i.length()) {
                    list[i] = list[j];
                    list[j] = temp_i;
                }
            }
        }

        // new replacement block
        String[] blocks = expression.split(string_token + "");
        for (int i = 0; i < blocks.length; i += 2) {
            for (String str : list) {
                String[] contents = str.split("\\.");
                blocks[i] = blocks[i].replace(contents[0], contents[1]);
            }
        }
        builder.setLength(0);

        if (blocks.length == 1)
            // case when the block contain no string, then the size is equals 1
            builder.append(blocks[0]);
        else {
            // in any other condition, a generic logic would follow
            for (int i = 0; i < blocks.length; i++) {
                builder.append(blocks[i]);
                builder.append(
                        i != (blocks.length - 1) ? string_token
                                : expression.charAt(expression.length() - 1) == 
                                    string_token ? string_token : "");
            }
        }

        expression = builder.toString();

        // bracket insertion for the function negation, this is the best place to do it
        // because the function token already exists by now and there are no
        // unnecessary clutter, doing this after the function
        // is converted into postfix would be impossible
        builder.setLength(0);
        boolean function_negation = false;
        int bracket_counter = 0;
        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);
            if ((c + "").matches("[-+]") && !function_negation) {
                // if i == 0, which means the first char is either a plus or a minus
                // this case is handled by complexConverter
                // I can ignore this one particular case
                if (i == 0) {
                    builder.append(c);
                    continue;
                } else {
                    // p will always be greater than 0 here
                    char p = expression.charAt(i - 1);
                    char a = i != expression.length() - 1 ? expression.charAt(i + 1) : 0;
                    if (a == function_token) {
                        // also look behind, for example if the expression is something like
                        // 1 - sin(90) or 2 times (-sin(90)), there's no need for any bracket
                        // but if the expression is like Sqrt - sin(90), it needs bracket insertion
                        operationsInterface ops = getCharAsAnOperator(p);
                        if (ops != null &&
                                (ops.getType() == TYPE_BOTH || ops.getType() == TYPE_POST)) {
                            builder.append('(');
                            builder.append('0');
                            builder.append(c);
                            function_negation = true;
                            continue;
                        }
                    }
                    builder.append(c);
                }
            } else if (function_negation) {
                builder.append(c);
                if (c == '(' || c == '{')
                    bracket_counter++;
                else if (c == ')' || c == '}') {
                    bracket_counter--;
                    if (bracket_counter == 0) {
                        function_negation = false;
                        builder.append(')');
                        continue;
                    }
                }
            } else
                builder.append(c);
        }
        expression = builder.toString();

        // another good place to add the multiplication operator between the
        // variables and the functions and the numbers
        // a new issue, what if the alphabet is actually an operator for a function?
        // in which case, skip the iteration, do some checks.
        builder.setLength(0);
        boolean isInFunction = false, isInString = false;
        int functionCounter = 0;
        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);

            if (c == string_token) {
                isInString = !isInString;
                builder.append(c);
                continue;
            }
            if (isInString) {
                builder.append(c);
                continue;
            }
            if (c == function_token) {
                isInFunction = true;
            }
            if (c == '(' && isInFunction) {
                functionCounter++;
            } else if (c == ')' && isInFunction) {
                functionCounter--;
                if (functionCounter == 0) {
                    isInFunction = false;
                }
            }
            // I'm not an expert in regexes, if it works, it works, I don't care.
            // if something breaks, just smash in some more random regexes, god I hate this.
            if ((c + "").matches("[a-zA-Z&&[^eEiI]]") &&
                    (!isInFunction || functionCounter > 0) && getCharAsAnOperator(c) == null) {
                // here take this for example abc, where a is an operator but b and c
                // are regular variables so what happens is when a is skipped entirely
                // but in the case of b, it checks if the previous var is an operator is an
                // operator or not,
                // if it is an operator then don't do it, otherwise do it.
                // and i don't need to check if the next char is also an operator
                // because, the next char is only checked if it's a number or not
                char p = (i != 0) ? expression.charAt(i - 1) : 0;
                char a = (i != expression.length() - 1) ? expression.charAt(i + 1) : 0;
                if ((p + "").matches("[i)}0-9a-zA-Z&&[^eE]]") &&
                        getCharAsAnOperator(p) == null)
                    builder.append(multiplication_token);
                builder.append(c);
                if ((a + "").matches("[0-9@({]"))
                    builder.append(multiplication_token);
            } else if ((c + "").matches("[0-9]")) {
                char p = (i != 0) ? expression.charAt(i - 1) : 0;
                if ((p + "").matches("[iI]"))
                    builder.append(multiplication_token);
                builder.append(c);
            } else
                builder.append(c);
        }
        expression = builder.toString();

        // looping in forward direction to check only the post operators.
        // operator identifiers does not exist yet, until I get to the complex
        // converter.
        builder.setLength(0);
        char op = 0;
        boolean found = false;
        int built_count = 0;
        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);
            if (!Character.isDigit(i)) {
                if (!found) {
                    if (built_count > 0)
                        while (built_count != 0) {
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
            } else {
                if (found)
                    found = false;
            }
            builder.append(c);
        }
        while (built_count != 0) {
            builder.append(')');
            built_count--;
        }
        expression = builder.toString();

        builder.setLength(0);
        int stringCounter = 0;
        // only solve for the bracket related implicit multiplication
        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);

            if (c == string_token) {
                stringCounter += stringCounter > 0 ? -1 : 1;
            }

            char p = (i != 0) ? expression.charAt(i - 1) : 0;
            char a = (i != expression.length() - 1) ? expression.charAt(i + 1) : 0;

            if (Character.isDigit(c)) {
                if (p == ')' || p == '}' || (p == string_token && stringCounter == 0)) {
                    builder.append(multiplication_token);
                }
                builder.append(c);
                if ((a == '(' || a == '{') || (a == string_token && stringCounter == 0)
                        || a == function_token) {
                    builder.append(multiplication_token);
                }
            } else if (c == function_token) {
                if (p == ')' || p == '}' || p == '"')
                    builder.append(multiplication_token);
                builder.append(c);
            } else {
                builder.append(c);
            }
        }
        expression = builder.toString();
        return expression;
    }

    // ignore the duplicate expression warnings, it's just the way for the intellij
    // to say that it's better than us humans, no you're not, now shut up.
    // reason being that the string builder is cleared by the method of
    // setLength(0),
    // which apparently is not recognized by the intellij, hence, it's clearly
    // dumber.
    private String complexConverter(String expression) {
        StringBuilder builder = new StringBuilder();
        StringBuilder currentStep = new StringBuilder();

        int stringCounter = 0;

        boolean negation_bracket_insert = false;
        int negation_bracket_insert_counter = 0;

        boolean isInFunction = false;
        int functionCounter = 0;

        outer_loop: 
        for (int i = 0; i < expression.length(); i++) {
            String c = expression.charAt(i) + "";
            if (expression.charAt(i) == string_token) {
                builder.append(c);
                if (stringCounter > 0)
                    stringCounter--;
                else
                    stringCounter++;
                continue;
            }
            if (stringCounter > 0) {
                builder.append(c);
                continue;
            }
            // just a seperate block, for readibility
            {
                if (c.equals(function_token + "")) {
                    isInFunction = true;
                }
                if (c.equals("(") && isInFunction) {
                    functionCounter++;
                } else if (c.equals(")") && isInFunction) {
                    functionCounter--;
                    if (functionCounter == 0) {
                        isInFunction = false;
                    }
                }
            }
            if (negation_bracket_insert) {
                if (c.matches("[({]"))
                    negation_bracket_insert_counter++;
                else if (c.matches("[)}]")) {
                    negation_bracket_insert_counter--;
                    if (negation_bracket_insert_counter == 0) {
                        if (!currentStep.toString().equals("")) {
                            // noinspection DuplicateExpressions
                            double value = Double.parseDouble(
                                currentStep.toString().replaceAll("i", ""));
                            if (currentStep.toString().contains("i")) {
                                builder.append(convertToComplexString(value, true));
                            } else {
                                builder.append(convertToComplexString(value, false));
                            }
                            currentStep.setLength(0);
                        }
                        builder.append(c);
                        builder.append(")");
                        negation_bracket_insert = false;
                        continue outer_loop;
                    }
                }
            }
            // this section assumes that there are no confusions
            // meaning, for example 2y+5 which would mean that the currentStep would not be
            // empty,
            // but this section assumes that the confusion has already been resolved
            // somewhere up above, most likely in the general parser.
            // meaning, 2y+5 would be converted into 2*y+5 which then would work perfectly
            // fine.
            if (c.matches("(?i)[a-z&&[^ei]]") && currentStep.isEmpty()
                    && (!isInFunction || functionCounter > 0) &&
                    getCharAsAnOperator(c.charAt(0)) == null) {
                builder.append(variable_token);
                builder.append(c);
                continue;
            }
            if (c.matches("[0-9.iIeE]")) {
                char p = (i != 0) ? expression.charAt(i - 1) : 0;
                if (c.matches("[.iIeE]")) {
                    if ((p + "").matches("[0-9]")) {
                        currentStep.append(c);
                    } else
                        builder.append(c);
                } else {
                    currentStep.append(c);
                }
            } else {
                if (!currentStep.toString().equals("")) {
                    // noinspection DuplicateExpressions
                    double value = Double.parseDouble(
                        currentStep.toString().replaceAll("i", ""));
                    if (currentStep.toString().contains("i")) {
                        builder.append(convertToComplexString(value, true));
                    } else {
                        builder.append(convertToComplexString(value, false));
                    }
                    currentStep.setLength(0);
                }
                if (c.matches("[+-]")) {
                    char a = (i != expression.length() - 1) ? expression.charAt(i + 1) : 0,
                            p = (i != 0) ? expression.charAt(i - 1) : 0;

                    if (i == 0 || p == '(' || p == '{' || p == ',') {
                        builder.append(convertComplexToString(new ComplexNumber())).append(c);
                        continue;
                    }

                    operationsInterface ops = getCharAsAnOperator(p);
                    if (ops != null && (ops.getType() == TYPE_POST || ops.getType() == TYPE_BOTH)) {
                        if (a == '(' || a == '{') {
                            builder.append('(');
                            builder.append(convertComplexToString(
                                    new ComplexNumber()));
                            builder.append(c);
                            negation_bracket_insert = true;
                        } else {
                            currentStep.append(c);
                        }
                        continue outer_loop;
                    }
                    builder.append(c);
                    continue;
                }
                builder.append(c);
            }
        }
        if (!currentStep.toString().equals("")) {
            // noinspection DuplicateExpressions
            double value = Double.parseDouble(
                    currentStep.toString().replaceAll("i", ""));
            if (currentStep.toString().contains("i")) {
                builder.append(convertToComplexString(value, true));
            } else {
                builder.append(convertToComplexString(value, false));
            }
        }
        expression = builder.toString();
        System.out.println("converted string => " + expression);
        return expression;
    }

    // does not resolve any error, provide a valid string.
    private Stack<String> postFixConverter(String expression) {
        StringBuilder builder = new StringBuilder();

        boolean tokenCounter = false;
        boolean functionCounter = false;
        boolean stringCounter = false;
        boolean foundVariable = false;

        Stack<String> output = new Stack<>();
        Stack<String> operators = new Stack<>();
        

        outer_loop: 
        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);
            if (c == complex_token) {
                if (!tokenCounter) {
                    tokenCounter = true;
                } else {
                    tokenCounter = false;
                    output.push(builder.toString());
                    builder.setLength(0);
                }
            } else if (tokenCounter) {
                builder.append(c);
            } else if (c == function_token) {
                if (!functionCounter) {
                    functionCounter = true;
                    builder.append(c);
                }
            } else if (functionCounter) {
                char a = expression.charAt(i + 1);
                builder.append(c);
                if (a == '(') {
                    functionCounter = false;
                    operators.push(builder.toString());
                    builder.setLength(0);
                }
            } else if (c == string_token) {
                builder.append(c);
                if (!stringCounter)
                    stringCounter = true;
                else {
                    stringCounter = false;
                    output.push(builder.toString());
                    builder.setLength(0);
                }
            } else if (stringCounter) {
                builder.append(c);
            } else if (c == variable_token){
                foundVariable = true;
                continue;
            } else if (foundVariable){
                char a = (i != expression.length() - 1 ? expression.charAt(i + 1) : 0);
                output.push(variable_token + "" + c + (a == 'i' ? a : ""));
                foundVariable = false;
                if (a == 'i')
                    i++;
                continue;
            } else {
                if (c == ')') {
                    Popper('(', output, operators);
                } else if (c == '}') {
                    Popper('{', output, operators);
                    output.push(c + "");
                } else if (c == '(')
                    operators.push(c + "");
                else if (c == '{') {
                    output.push(c + "");
                    operators.push(c + "");
                } else {
                    if (c == ',') {
                        while (operators.hasNext()) {
                            char t = operators.peek().charAt(0);
                            // exit at the nearest bracket.
                            if (t == '(' || t == '{') {
                                break;
                            } else {
                                String item = operators.pop();
                                output.push((item.charAt(0) != 
                                    function_token ? operation_token : "") + item);
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
                            if (current_ops_precedence <= (!isFun ? previous_ops.getPrecedence()
                                    : PRECEDENCE_FUNCTION)) {
                                String item = operators.pop();
                                output.push((item.charAt(0) != 
                                    function_token ? operation_token : "") + item);
                            } else {
                                operators.push(c + "");
                                continue outer_loop;
                            }
                        } else {
                            operators.push(c + "");
                            continue outer_loop;
                        }
                    }
                    // very important for whatever reason I forgot.
                    operators.push(c + "");
                }
            }
        }
        while (operators.hasNext()) {
            String item = operators.pop();
            output.push((item.charAt(0) != function_token ? operation_token : "") + item);
        }

        output = output.reverse();
        builder.setLength(0);
        output.resetLoopAtPointer();
        while (output.loop()) {
            builder.append(output.get()).append(",");
        }
        System.out.println("post fix string => " + builder);
        return output;
    }

    private Stack<String> postFixEvaluator(Stack<String> stack) {
        Stack<String> output = new Stack<>();

        outer_loop: 
        while (stack.hasNext()) {
            String item = stack.pop();
            if (item.charAt(0) == function_token) {
                StringBuilder fn_name_builder = new StringBuilder();
                for (int i = 1; i < item.length(); i++)
                    fn_name_builder.append(item.charAt(i));

                String fn_name = fn_name_builder.toString();

                int count = (int) convertToComplexNumber(output.pop()).real;
                int set_count = 0;
                Stack<String> sub_stack = new Stack<>();
                while (count != 0) {
                    String sub_item = output.pop();
                    if (sub_item.equals("{"))
                        set_count--;
                    else if (sub_item.equals("}"))
                        set_count++;

                    sub_stack.push(sub_item);
                    if (set_count == 0)
                        count--;
                }

                functions.resetLoopAtPointer();
                while (functions.loop()) {
                    functionsInterface fnInt = functions.get();
                    String[] name = fnInt.getFunctionNames();
                    for (String nm : name) {
                        if (nm.equals(fn_name)) {
                            sub_stack.checkpoint();
                            pointerOutput.checkpoint();
                            try {
                                DispatchParcel parcel = functionDispatcher(sub_stack, fnInt);
                                pointerOutput.push(parcel);
                                output.push(pointer_token + "");
                                continue outer_loop;
                            } catch (Exception e) {
                                sub_stack.resetToCheckpoint();
                                bracketReplacement(sub_stack);
                                pointerOutput.resetToCheckpoint();
                            }
                        }
                    }
                }
                throw new IllegalStateException("the given function name or" +
                        " argument doesn't quite add up");
            } else if (item.charAt(0) == operation_token) {
                operationsInterface opInt = getCharAsAnOperator(item.charAt(1));
                switch (opInt.getType()) {
                    case TYPE_BOTH -> {
                        DispatchParcel parcel_left, parcel_right;
                        String right = output.pop();
                        if (right.equals(pointer_token + "")) {
                            parcel_right = pointerOutput.pop();
                        } else if (right.equals("}")) {
                            parcel_right = new DispatchParcel();
                            parcel_right.type = SET;
                            parcel_right.set = setGiver(output);
                        } else {
                            parcel_right = convertStringToParcel(right);
                        }
                        String left = output.pop();
                        if (left.equals(pointer_token + "")) {
                            parcel_left = pointerOutput.pop();
                        } else if (left.equals("}")) {
                            parcel_left = new DispatchParcel();
                            parcel_left.type = SET;
                            parcel_left.set = setGiver(output);
                        } else {
                            parcel_left = convertStringToParcel(left);
                        }
                        pointerOutput.push(dispatcher(parcel_left, parcel_right, opInt));
                        output.push(pointer_token + "");
                    }
                    case TYPE_PRE -> {
                        DispatchParcel parcel_left_1;
                        String left_1 = output.pop();
                        if (left_1.equals(pointer_token + "")) {
                            parcel_left_1 = pointerOutput.pop();
                        } else if (left_1.equals("}")) {
                            parcel_left_1 = new DispatchParcel();
                            parcel_left_1.type = SET;
                            parcel_left_1.set = setGiver(output);
                        } else {
                            parcel_left_1 = convertStringToParcel(left_1);
                        }
                        pointerOutput.push(dispatcher(parcel_left_1, null, opInt));
                        output.push(pointer_token + "");
                    }
                    case TYPE_POST -> {
                        DispatchParcel parcel_right_1;
                        String right_1 = output.pop();
                        if (right_1.equals(pointer_token + "")) {
                            parcel_right_1 = pointerOutput.pop();
                        } else if (right_1.equals("}")) {
                            parcel_right_1 = new DispatchParcel();
                            parcel_right_1.type = SET;
                            parcel_right_1.set = setGiver(output);
                        } else {
                            parcel_right_1 = convertStringToParcel(right_1);
                        }
                        pointerOutput.push(dispatcher(null, parcel_right_1, opInt));
                        output.push(pointer_token + "");
                    }
                    case TYPE_CONSTANT -> {
                        pointerOutput.push(dispatcher(null, null, opInt));
                        output.push(pointer_token + "");
                    }
                }
            } else {
                output.push(item);
            }
        }
        return output;
    }

    // this function always assumes that the opening bracket is not to be
    // considered.
    private Set setGiver(Stack<String> stack) {
        int set_count = 1;
        Stack<String> st = new Stack<>();
        while (set_count != 0) {
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

    private DispatchParcel convertStringToParcel(String str) {
        ComplexNumber cn;
        DispatchParcel parcel = new DispatchParcel();

        if (str.charAt(0) == '\"' && str.charAt(str.length() - 1) == '\"') {
            parcel.type = STRING;
            parcel.string = str;
            return parcel;
        } else if (str.charAt(0) == variable_token){
            parcel.type = VARIABLE;
            Variable var = new Variable(str.charAt(1), true);
            parcel.var = var;
            return parcel;
        }
        try {
            cn = convertToComplexNumber(str);
            parcel.type = COMPLEX;
            parcel.number = cn;
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return parcel;
    }

    private String functionHandler(String expression) {
        expression = functionUpdater(expression);
        // so the way it works is that the general parser adds the token for the updater
        // so that updater will know which are the functions
        // the updater is a recursive function and needs the token to be destroyed
        // this is because the updater only goes through the top layer and then recurse
        // into nested layers
        // the nested layers are the functions that are within brackets
        // after the updater returns, the expression no longer has any function token
        // that's why it needs to be added again which is done down here.
        StringBuilder builder = new StringBuilder();
        String[] strs = expression.split(string_token + "");
        Stack<String> fn_names = new Stack<>();
        functions.resetLoopAtPointer();
        while (functions.loop()) {
            functionsInterface fnInt = functions.get();
            String[] name = fnInt.getFunctionNames();
            for (String nm : name) {
                if (!fn_names.contains(nm)) {
                    for (int k = 0; k < strs.length; k += 2) {
                        strs[k] = strs[k].replace(nm, function_token + nm);
                    }
                    fn_names.push(nm);
                }
            }
        }
        if (strs.length == 1)
            expression = strs[0];
        else {
            builder.setLength(0);
            for (int i = 0; i < strs.length; i++) {
                builder.append(strs[i]);
                builder.append(
                        i != (strs.length - 1) ? string_token
                                : expression.charAt(expression.length() - 1) == 
                                    string_token ? string_token : "");
            }
            expression = builder.toString();
        }

        builder.setLength(0);
        boolean fun_token_found = false;
        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);
            if (c == function_token && fun_token_found)
                // consecutive found, skip this step
                continue;
            else if (c == function_token) {
                builder.append(c);
                fun_token_found = true;
                continue;
            } else {
                if (fun_token_found)
                    fun_token_found = false;
                builder.append(c);
            }
        }
        expression = builder.toString();
        return expression;
    }

    private String functionUpdater(String expression) {
        StringBuilder builder = new StringBuilder();

        boolean param_scan = false;
        int bracket_counter = 0;
        int param_count = 0;
        boolean isInFunction = false;

        boolean isInSet = false;
        boolean isInString = false;
        int setCount = 0;

        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);
            if (c == '"') {
                isInString = !isInString;
                builder.append(c);
                continue;
            }
            if (isInString) {
                builder.append(c);
                continue;
            }
            if (c == function_token && bracket_counter == 0) {
                isInFunction = true;
                if (isInSet)
                    isInSet = false;
                continue;
            } else if (isInFunction) {
                if (expression.charAt(i + 1) == '(') {
                    isInFunction = false;
                    param_scan = true;
                }
            }
            if (c == '{') {
                isInSet = true;
                setCount++;
            } else if (c == '}') {
                setCount--;
                if (setCount == 0)
                    isInSet = false;
            }
            builder.append(c);
            if (param_scan) {
                if (c == '(')
                    bracket_counter++;
                else if (c == ')') {
                    bracket_counter--;
                    if (bracket_counter == 0) {
                        param_scan = false;
                        builder.append(++param_count);
                        param_count = 0;
                    }
                } else if (c == ',' && bracket_counter == 1 && !isInSet)
                    param_count++;
            }

        }
        expression = builder.toString();

        if (builder.toString().contains(function_token + ""))
            expression = functionUpdater(expression);

        return expression;
    }

    private Set setHandler(char opening_bracket, char closing_bracket, Stack<String> stack) {
        if (stack.getLength() == 0) {
            return new Set();
        }
        stack = stack.reverse();
        Stack<String> newSet = new Stack<>();
        StringBuilder newString = new StringBuilder();
        Set set = new Set();

        while (stack.hasNext()) {
            String item = stack.pop();
            if (item.charAt(0) == opening_bracket) {
                int count = 1;
                while (count != 0) {
                    String newItem = stack.pop();
                    if (newItem.charAt(0) == opening_bracket) {
                        newSet.push(newItem);
                        count++;
                    } else if (newItem.charAt(0) == closing_bracket) {
                        count--;
                        if (count != 0)
                            newSet.push(newItem);
                        else
                            break;
                    } else
                        newSet.push(newItem);
                }
                set.pushSet(setHandler(opening_bracket, closing_bracket, newSet));
                continue;
            }
            if (item.charAt(0) == string_token) {
                if (item.charAt(item.length() - 1) != string_token) {
                    throw new IllegalArgumentException("not a proper string");
                }
                newString.setLength(0);
                for (int j = 1; j < item.length() - 1; j++)
                    newString.append(item.charAt(j));
                set.pushString(newString.toString());
                continue;
            }
            //only a var can exist in a raw form in a string
            if (item.charAt(0) == variable_token){
                Variable var = new Variable(item.charAt(1), true);
                set.pushVar(var);
                continue;
            }
            if (item.charAt(0) == pointer_token) {
                DispatchParcel parcel = pointerOutput.pop();
                switch (parcel.type) {
                    case SET -> set.pushSet(parcel.set);
                    case COMPLEX -> {
                        ComplexNumber cn = parcel.number;
                        if (cn.real != 0 && cn.iota != 0)
                            set.pushComplex(cn);
                        else if (cn.real != 0)
                            set.pushReal(cn.real);
                        else if (cn.iota != 0)
                            set.pushIota(cn.iota);
                        else
                            set.pushComplex(cn);
                    }
                    case STRING -> set.pushString(parcel.string);
                    case VARIABLE -> set.pushVar(parcel.var);
                    case CONSTANT -> set.pushCon(parcel.con);
                    case TERM -> set.pushTerm(parcel.term);
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
                else
                    set.pushComplex(cn);
            } catch (Exception e) {
                throw new IllegalArgumentException(e.getMessage());
            }
        }
        return set;
    }

    private String implicitSolver(String expression) {
        StringBuilder builder = new StringBuilder();
        boolean isInString = false;

        outer_loop: 
        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);
            // cases to handle
            /*
                i" -> i*"
                )" -> )*"
                }" -> }*"
                "" -> "*"
                "( -> "*(
                "{ -> "*{
                )( -> )*(
                }{ -> }*{
                i( -> i*(
                i{ -> i*{
                ){ -> )*{
                }( -> }*(
                
             */

            // the occuring of two double quotes next to each other can be handled
            // in either of the two if cases, but I'm just going to handle it in the later
            // if case
            // it just looks more pleasent to me, 3 cases handled up and 3 cases handled
            // down below.

            if (c == string_token) {
                if (!isInString && (i != 0)) {
                    char p = expression.charAt(i - 1);
                    if ((p + "").matches("[i)}]")) {
                        builder.append(multiplication_token);
                    }
                }
                isInString = !isInString;

                builder.append(c);
                if (!isInString && (i != expression.length() - 1)) {
                    char a = expression.charAt(i + 1);
                    if ((a + "").matches("[\"({]"))
                        builder.append(multiplication_token);
                }
                continue;
            }

            if (isInString) {
                builder.append(c);
                continue;
            }

            char p = (i != 0) ? expression.charAt(i - 1) : 0;
            char a = (i != expression.length() - 1) ? expression.charAt(i + 1) : 0;

            if ((c + "").matches("[)i}]") && 
                    (a + "").matches("[({]")){
                builder.append(c);
                builder.append(multiplication_token);
                continue;
            }

            operationsInterface ops = getCharAsAnOperator(c);
            if (ops != null) {
                builder.append(getImplicitExp(ops, p, a));
                continue outer_loop;
            }
            // operations.reset();
            // while (operations.loop()){
            // operationsInterface opInt = operations.get();
            // if (opInt.getOperator() == c) {
            // builder.append(getImplicitExp(opInt, p, a));
            // continue outer_loop;
            // }
            // }
            builder.append(c);
        }
        return builder.toString();
    }

    private void Popper(char bracket, Stack<String> output, Stack<String> operators) {
        while (operators.hasNext()) {
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

    //todo : string messages are outdated, need to update it.
    //just forget about it, i'm not touching this bamboozling pile of mess
    //this whole function is just a huge headache and i hope i don't have to touch it again.
    //i'll rather get rid of this functionality altogether than having to deal 
    //with this pile of shit i wrote
    private String getImplicitExp(operationsInterface opInt, char left, char right) {
        // no need to check for either a dot '.', or exponent ';', in each case,
        // they must end with a number, or else they'll be invalid.
        // only left operand can have an iota 'i'.
        boolean l = (left + "").matches("[0-9)i}\"\\]]");
        boolean r = (right + "").matches("[0-9(@{\"\\[]");
        // because why not, i like things scoped.
        if (!l && (left + "").matches("(?i)[a-z]") &&
                getCharAsAnOperator(left) == null) {
            l = true;
        }
        if (!r && (right + "").matches("(?i)[a-z]") &&
                getCharAsAnOperator(right) == null) {
            r = true;
        }

        if (l && r) {
            switch (opInt.getType()) {
                case TYPE_BOTH:
                    return opInt.getOperator() + "";
                case TYPE_PRE:
                    return opInt.getOperator() + "*";
                case TYPE_POST:
                    return "*" + opInt.getOperator();
                case TYPE_CONSTANT:
                    return "*" + opInt.getOperator() + "*";
            }
        } else if (l) {
            // the right one is guaranteed to be an operator or (a closing bracket or
            // empty).
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
        } else if (r) {
            // the left char is guaranteed to be an operator or (an opening bracket or
            // empty).
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
        } else {
            // both left and right char are guaranteed to be an operator or a set of an
            // opening and a closing bracket.
            operationsInterface left_operator, right_operator;
            left_operator = getCharAsAnOperator(left);
            right_operator = getCharAsAnOperator(right);

            int precedence_current = opInt.getPrecedence();
            int precedence_left = left_operator != null ? left_operator.getPrecedence() : -1;
            int precedence_right = right_operator != null ? right_operator.getPrecedence() : -1;

            if (left_operator == null && right_operator == null) {
                return opInt.getOperator() + "";
            } else if (left_operator == null) {
                switch (opInt.getType()) {
                    case TYPE_BOTH:
                    case TYPE_PRE:
                        // there isn't much to do with either a plus or a minus operator
                        // in case a situation arises in the future, update this function as needed.
                        if (opInt.getOperator() == '+' || opInt.getOperator() == '-')
                            return opInt.getOperator() + "";
                        throw new ExpressionException("the operator has type pre or both but no left operand found");
                    case TYPE_POST:
                        switch (right_operator.getType()) {
                            case TYPE_BOTH:
                                if (right_operator.getOperator() == '+' || right_operator.getOperator() == '-')
                                    return opInt.getOperator() + "";
                            case TYPE_PRE:
                                throw new ExpressionException("the right operator has type both or pre, while" +
                                        " the left operator type post");
                            case TYPE_POST:
                                if (precedence_current > precedence_right)
                                    throw new ExpressionException("the right operator has type post while " +
                                            "the left operator has type post but has a greater precedence");
                                else
                                    return opInt.getOperator() + "";
                            case TYPE_CONSTANT:
                                if (precedence_current >= precedence_right)
                                    throw new ExpressionException("the right operator has type constant however has" +
                                            " less than or equal to the precedence of the" +
                                            " left operator which has type post");
                                else
                                    return opInt.getOperator() + "";
                        }
                    case TYPE_CONSTANT:
                        switch (right_operator.getType()) {
                            case TYPE_BOTH:
                            case TYPE_PRE:
                                if (precedence_right > precedence_current)
                                    throw new ExpressionException("the right operator has type pre or both has " +
                                            "greater precedence than the left operator which has type constant");
                                else
                                    return opInt.getOperator() + "";
                            case TYPE_POST:
                            case TYPE_CONSTANT:
                                return opInt.getOperator() + "*";
                        }
                }
            } else if (right_operator == null) {
                switch (opInt.getType()) {
                    case TYPE_BOTH:
                        if (left_operator.getOperator() == '+' || left_operator.getOperator() == '-')
                            return opInt.getOperator() + "";
                    case TYPE_POST:
                        throw new ExpressionException("the operator has type post or both but no right operand found");
                    case TYPE_PRE:
                        switch (left_operator.getType()) {
                            case TYPE_PRE:
                                if (precedence_current <= precedence_left)
                                    return opInt.getOperator() + "";
                            case TYPE_CONSTANT:
                                if (precedence_current <= precedence_left)
                                    return opInt.getOperator() + "";
                            default:
                                return opInt.getOperator() + "";
                        }

                    case TYPE_CONSTANT:
                        switch (left_operator.getType()) {
                            case TYPE_BOTH:
                            case TYPE_POST:
                                if (precedence_left < precedence_current)
                                    return opInt.getOperator() + "";
                            case TYPE_PRE:
                            case TYPE_CONSTANT:
                                return opInt.getOperator() + "";
                            default:
                                return opInt.getOperator() + "";
                        }
                }
            } else {
                // both side is an operation, and I have no idea how will I deal with this.
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
                                builder.append(multiplication_token);
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
                                builder.append(multiplication_token);
                                break;
                        }
                    }
                }
                return builder.toString();
            }
        }
        return opInt.getOperator() + "";
    }

    private DispatchParcel dispatcher(DispatchParcel left, DispatchParcel right,
            operationsInterface whichOperation) {
        DispatchParcel parcel = new DispatchParcel();
        ComplexNumber number = new ComplexNumber(0, 0);

        boolean handle_complex = false;

        if(left == null && right == null){
            whichOperation.function();
        }else if (left == null || right == null) {
            DispatchParcel local_parcel = (left == null) ? right : left;
            switch(local_parcel.type){
                case COMPLEX -> {
                    double real = local_parcel.number.real;
                    double iota = local_parcel.number.iota;
                    if ((real != 0 && iota != 0) || (real == 0 && iota == 0)){
                        whichOperation.function(local_parcel.number);
                        break;
                    }
                    whichOperation.function(real!=0?real:iota, 
                    real!=0?IOTA_FALSE:IOTA_TRUE);
                }
                case SET -> whichOperation.function(local_parcel.set);
                case STRING -> whichOperation.function(local_parcel.string);
                case VARIABLE -> whichOperation.function(local_parcel.var);
                case CONSTANT -> whichOperation.function(local_parcel.con);
                case TERM -> whichOperation.function(local_parcel.term);
            }
        }else {
            top :
            switch(left.type){
                case COMPLEX -> {
                    double real = left.number.real;
                    double iota = left.number.iota;
                    switch(right.type){
                        case COMPLEX -> handle_complex = true;
                        case SET -> {
                            if ((real!=0 && iota!=0) || (real==0 && iota==0)){
                                whichOperation.function(left.number, right.set);
                                break top;
                            }
                            whichOperation.function(iota!=0? iota : real, right.set, 
                                iota!=0? IOTA_TRUE : IOTA_FALSE);
                        }
                        case STRING -> {
                            if ((real!=0 && iota!=0) || (real==0 && iota==0)){
                                whichOperation.function(left.number, right.string);
                                break top;
                            }
                            whichOperation.function(iota!=0? iota : real, right.string, 
                                iota!=0? IOTA_TRUE : IOTA_FALSE);
                        }
                        case VARIABLE -> {
                            if ((real!=0 && iota!=0) || (real==0 && iota==0)){
                                whichOperation.function(left.number, right.var);
                                break top;
                            }
                            whichOperation.function(iota!=0? iota : real, right.var, 
                                iota!=0? IOTA_TRUE : IOTA_FALSE);
                        }
                        case CONSTANT -> {
                            if ((real!=0 && iota!=0) || (real==0 && iota==0)){
                                whichOperation.function(left.number, right.con);
                                break top;
                            }
                            whichOperation.function(iota!=0? iota : real, right.con, 
                                iota!=0? IOTA_TRUE : IOTA_FALSE);
                        }
                        case TERM -> {
                            if ((real!=0 && iota!=0) || (real==0 && iota==0)){
                                whichOperation.function(left.number, right.term);
                                break top;
                            }
                            whichOperation.function(iota!=0? iota : real, right.term, 
                                iota!=0? IOTA_TRUE : IOTA_FALSE);
                        }
                    }
                }
                case SET -> {
                    switch(right.type){
                        case COMPLEX -> {
                            double real = right.number.real;
                            double iota = right.number.iota;
                            if ((real != 0 && iota != 0) || (real == 0 && iota == 0)){
                                whichOperation.function(left.set, right.number);
                                break top;
                            }
                            whichOperation.function(left.set, real!=0?real:iota, 
                                real!=0?IOTA_FALSE:IOTA_TRUE);
                        }
                        case SET -> whichOperation.function(left.set, right.set);
                        case STRING -> whichOperation.function(left.set, right.string);
                        case VARIABLE -> whichOperation.function(left.set, right.var);
                        case CONSTANT -> whichOperation.function(left.set, right.con);
                        case TERM -> whichOperation.function(left.set, right.term);
                    }
                }
                case STRING -> {
                    switch(right.type){
                        case COMPLEX -> {
                            double real = right.number.real;
                            double iota = right.number.iota;
                            if ((real != 0 && iota != 0) || (real == 0 && iota == 0)){
                                whichOperation.function(left.string, right.number);
                                break top;
                            }
                            whichOperation.function(left.string, real!=0?real:iota,
                                real!=0?IOTA_FALSE:IOTA_TRUE);
                        }
                        case SET -> whichOperation.function(left.string, right.set);
                        case STRING -> whichOperation.function(left.string, right.string);
                        case VARIABLE -> whichOperation.function(left.string, right.var);
                        case CONSTANT -> whichOperation.function(left.string, right.con);
                        case TERM -> whichOperation.function(left.string, right.term);
                    }
                }
                case VARIABLE -> {
                    switch(right.type){
                        case COMPLEX -> {
                            double real = right.number.real;
                            double iota = right.number.iota;
                            if ((real != 0 && iota != 0) || (real == 0 && iota == 0)){
                                whichOperation.function(left.var, right.number);
                                break top;
                            }
                            whichOperation.function(left.var, real!=0?real:iota, 
                                real!=0?IOTA_FALSE:IOTA_TRUE);
                        }
                        case SET -> whichOperation.function(left.var, right.set);
                        case STRING -> whichOperation.function(left.var, right.string);
                        case VARIABLE -> whichOperation.function(left.var, right.var);
                        case CONSTANT -> whichOperation.function(left.var, right.con);
                        case TERM -> whichOperation.function(left.var, right.term);
                    }
                }
                case CONSTANT -> {
                    switch(right.type){
                        case COMPLEX -> {
                            double real = right.number.real;
                            double iota = right.number.iota;
                            if ((real != 0 && iota != 0) || (real == 0 && iota == 0)){
                                whichOperation.function(left.con, right.number);
                                break top;
                            }
                            whichOperation.function(left.con, real!=0?real:iota, 
                                real!=0?IOTA_FALSE:IOTA_TRUE);
                        }
                        case SET -> whichOperation.function(left.con, right.set);
                        case STRING -> whichOperation.function(left.con, right.string);
                        case VARIABLE -> whichOperation.function(left.con, right.var);
                        case CONSTANT -> whichOperation.function(left.con, right.con);
                        case TERM -> whichOperation.function(left.con, right.term);
                    }
                }
                case TERM -> {
                    switch(right.type){
                        case COMPLEX -> {
                            double real = right.number.real;
                            double iota = right.number.iota;
                            if ((real != 0 && iota != 0) || (real == 0 && iota == 0)){
                                whichOperation.function(left.term, right.number);
                                break top;
                            }
                            whichOperation.function(left.term, real!=0?real:iota, 
                                real!=0?IOTA_FALSE:IOTA_TRUE);
                        }
                        case SET -> whichOperation.function(left.term, right.set);
                        case STRING -> whichOperation.function(left.term, right.string);
                        case VARIABLE -> whichOperation.function(left.term, right.var);
                        case CONSTANT -> whichOperation.function(left.term, right.con);
                        case TERM -> whichOperation.function(left.term, right.term);
                    }
                }
            }
        }

        // is checking for the types even necessary? like if the type is post,
        // the c1 naturally is going to be null, or if the type is pre, the c2 will be
        // null.
        // but it's a lot of refactoring, so I'll leave it as it is.
        if (handle_complex) {
            ComplexNumber complex_left = left.number, complex_right = right.number;
            double left_real = complex_left.real,
                    left_iota = complex_left.iota,
                    right_real = complex_right.real,
                    right_iota = complex_right.iota;
            switch (whichOperation.getType()) {
                case TYPE_BOTH:
                    // under normal circumstances
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

        switch (result_flag) {
            case REAL -> {
                number.real = whichOperation.getReal();
                parcel.type = COMPLEX;
                parcel.number = number;
            }
            case IOTA -> {
                number.iota = whichOperation.getIota();
                parcel.type = COMPLEX;
                parcel.number = number;
            }
            case COMPLEX -> {
                number = whichOperation.getComplex();
                parcel.type = COMPLEX;
                parcel.number = number;
            }
            case SET -> {
                parcel.type = SET;
                parcel.set = whichOperation.getSet();
            }
            case STRING -> {
                parcel.type = STRING;
                parcel.string = whichOperation.getString();
            }
            case VARIABLE -> {
                parcel.type = VARIABLE;
                parcel.var = whichOperation.getVariable();
            }
            case CONSTANT -> {
                parcel.type = CONSTANT;
                parcel.con = whichOperation.getConstant();
            }
            case TERM -> {
                parcel.type = TERM;
                parcel.term = whichOperation.getTerms();
            }
            default -> {
                break;
            }
        }

        return parcel;
    }

    // the caller should take care if the sub_expression is empty or not, the
    // dispatcher
    // will not resolve any problems.
    private DispatchParcel functionDispatcher(Stack<String> stack, functionsInterface fs) {
        bracketReplacement(stack);

        int[] map = fs.getFunctionMap();
        //i am just gonna use it raw, got a problem? shut up now.
        Stack<Argument> arguments;

        DispatchParcel parcel = new DispatchParcel();

        boolean isArray = false;

        if (map[0] == ARRAY) {
            if (map.length != 2)
                throw new ExpressionException("the functions that expects an array can only accept one type" +
                        " of argument, in this case, the map declaration can only have two types," +
                        " the first declaring it an array and the second being the type," +
                        " whose array is being expected");
            isArray = true;
        }

        arguments = new Stack<>(isArray ? stack.getLength() : map.length);

        int counter = 0;
        while (stack.hasNext()) {
            int id = map[!isArray ? counter : 1];
            String item = stack.pop();
            switch (id) {
                case REAL -> {
                    if (item.equals(pointer_token + "")) {
                        ComplexNumber cn = pointerOutput.peek().number;
                        if (cn == null)
                            throw new ExpressionException("arguments don't match");
                        if (cn.iota == 0) {
                            arguments.push(new Argument(cn.real, REAL));
                            pointerOutput.pop();
                            break;
                        }
                    } else {
                        ComplexNumber cn = convertToComplexNumber(item);
                        if (cn.iota == 0) {
                            arguments.push(new Argument(cn.real, REAL));
                            break;
                        }
                    }
                    throw new IllegalArgumentException("given argument does not match with the map");
                }
                case IOTA -> {
                    if (item.equals(pointer_token + "")) {
                        ComplexNumber cn = pointerOutput.peek().number;
                        if (cn == null)
                            throw new ExpressionException("arguments don't match");
                        if (cn.real == 0) {
                            arguments.push(new Argument(cn.iota, IOTA));
                            pointerOutput.pop();
                            break;
                        }
                    } else {
                        ComplexNumber cn = convertToComplexNumber(item);
                        if (cn.real == 0) {
                            arguments.push(new Argument(cn.iota, IOTA));
                            break;
                        }
                    }
                    throw new IllegalArgumentException("given argument does not match with the map");
                }
                case COMPLEX -> {
                    if (item.equals(pointer_token + "")) {
                        ComplexNumber cn = pointerOutput.peek().number;
                        if (cn == null)
                            throw new ExpressionException("arguments don't match");
                        arguments.push(new Argument(cn, COMPLEX));
                        pointerOutput.pop();
                    } else {
                        ComplexNumber cn = convertToComplexNumber(item);
                        arguments.push(new Argument(cn, COMPLEX));
                    }
                }
                case STRING -> {
                    if (item.equals(pointer_token + "")) {
                        String str = pointerOutput.peek().string;
                        if (str == null)
                            throw new ExpressionException("arguments don't match");
                        arguments.push(new Argument(str, STRING));
                        pointerOutput.pop();
                        break;
                    } else if (item.charAt(0) == string_token) {
                        arguments.push(new Argument(item, STRING));
                        break;
                    }
                    throw new IllegalArgumentException("not a string");
                }
                case SET -> {
                    if (item.equals(pointer_token + "")) {
                        Set set = pointerOutput.peek().set;
                        if (set == null)
                            throw new ExpressionException("arguments don't match");
                        arguments.push(new Argument(set, SET));
                        pointerOutput.pop();
                    } else {
                        Set set = setGiver(stack);
                        arguments.push(new Argument(set, SET));
                    }
                }
                case VARIABLE -> {
                    if (item.equals(pointer_token + "")) {
                        Variable var = pointerOutput.peek().var;
                        if (var == null)
                            throw new ExpressionException("arguments don't match");
                        arguments.push(new Argument(var, VARIABLE));
                        pointerOutput.pop();
                    } else {
                        Variable var = new Variable(item.charAt(1), true);
                        arguments.push(new Argument(var, VARIABLE));
                    }
                }
                case CONSTANT -> {
                    if (item.equals(pointer_token + "")) {
                        Constant con = pointerOutput.peek().con;
                        if (con == null)
                            throw new ExpressionException("arguments don't match");
                        arguments.push(new Argument(con, CONSTANT));
                        pointerOutput.pop();
                    }
                }
                case TERM -> {
                    if (item.equals(pointer_token + "")) {
                        Terms term = pointerOutput.peek().term;
                        if (term == null)
                            throw new ExpressionException("arguments don't match");
                        arguments.push(new Argument(term, CONSTANT));
                        pointerOutput.pop();
                    }
                }
                case ARRAY ->
                    throw new ExpressionException("the array declaration must be the first element");
                default -> throw new IllegalStateException("unknown parameter");
            }
            if (!isArray)
                counter++;
        }

        Argument[] args = new Argument[arguments.getLength()];
        int count = 0;
        arguments = arguments.reverse();
        while (arguments.hasNext()) {
            args[count] = arguments.pop();
            count++;
        }
        fs.function(args, fs.getId());

        int resultFlag = fs.getResultFlag();
        ComplexNumber cn;
        switch (resultFlag) {
            case REAL -> {
                cn = convertToComplexNumber(fs.getReal(), false);
                parcel.type = COMPLEX;
                parcel.number = cn;
            }
            case IOTA -> {
                cn = convertToComplexNumber(fs.getIota(), true);
                parcel.type = COMPLEX;
                parcel.number = cn;
            }
            case COMPLEX -> {
                cn = fs.getComplex();
                parcel.type = COMPLEX;
                parcel.number = cn;
            }
            case SET -> {
                parcel.type = SET;
                parcel.set = fs.getSet();
            }
            case STRING -> {
                parcel.type = STRING;
                parcel.string = fs.getString();
            }
            case VARIABLE -> {
                parcel.type = VARIABLE;
                parcel.var = fs.getVariable();
            }
            case CONSTANT -> {
                parcel.type = CONSTANT;
                parcel.con = fs.getConstant();
            }
            case TERM -> {
                parcel.type = TERM;
                parcel.term = fs.getTerms();
            }
            default -> throw new IllegalArgumentException("unknown type");
        }
        return parcel;
    }

    // sometimes in these comments I feel like I'm talking to myself.

    private operationsInterface getCharAsAnOperator(char c) {
        operationsInterface opInt = null;
        operations.resetLoopAtPointer();
        while (operations.loop()) {
            operationsInterface ops = operations.get();
            if (ops.getOperator() == c) {
                opInt = ops;
                break;
            }
        }
        return opInt;
    }

    //sort result function does not currently support the variables, constants or the terms.
    //this function only exists to make the output pretty
    private String SortResult(Stack<String> result) {
        ComplexNumber cn = null;
        boolean isComplex = false;

        String item = result.pop();
        if (item.equals(pointer_token + "")) {
            DispatchParcel parcel = pointerOutput.pop();
            switch (parcel.type) {
                case COMPLEX:
                    isComplex = true;
                    cn = parcel.number;
                    break;
                case STRING:
                    return parcel.string;
                case SET:
                    return convertSetToString(parcel.set);
            }
        } else if (item.charAt(0) == string_token) {
            item = item.replace(string_token + "", "");

            operationsInterface ops = getCharAsAnOperator(empty_token);
            if (ops != null) {
                ops.function(item);
                int result_flag = ops.getResultFlag();
                switch (result_flag) {
                    case REAL -> {
                        return ops.getReal() + "";
                    }
                    case STRING -> {
                        return ops.getString();
                    }
                    default -> {
                        return item;
                    }
                }
            }
            return item;
        } else if (item.equals("}")) {
            Set set = setGiver(result);
            return convertSetToString(set);
        } else {
            cn = convertToComplexNumber(item);
            isComplex = true;
        }

        if (isComplex) {
            if (cn.real != 0 && cn.iota == 0) {
                if (cn.real % 1 == 0) {
                    // split is always guaranteed
                    String[] parts = (cn.real + "").split("\\.");
                    for (char c : parts[1].toCharArray()) {
                        if (c != '0') {
                            return cn.real + "";
                        }
                    }
                    return parts[0];
                } else
                    return cn.real + "";
            } else if (cn.real == 0 && cn.iota != 0) {
                if (cn.iota % 1 == 0) {
                    String[] parts = (cn.iota + "").split("\\.");
                    for (char c : parts[1].toCharArray()) {
                        if (c != '0') {
                            return cn.iota + "i";
                        }
                    }
                    return parts[0] + "i";
                } else
                    return cn.iota + "i";
            } else if (cn.real == 0) {
                return "0";
            } else {
                String builder = "";
                if (cn.real % 1 == 0) {
                    String[] parts = (cn.real + "").split("\\.");
                    boolean empty_decimals = true;
                    for (char c : parts[1].toCharArray()) {
                        if (c != '0') {
                            empty_decimals = false;
                            break;
                        }
                    }
                    if (empty_decimals)
                        builder += parts[0];
                    else
                        builder += cn.real;
                } else
                    builder += cn.real;
                if (cn.iota > 0)
                    builder += "+";
                if (cn.iota % 1 == 0) {
                    String[] parts = (cn.iota + "").split("\\.");
                    boolean empty_decimals = true;
                    for (char c : parts[1].toCharArray()) {
                        if (c != '0') {
                            empty_decimals = false;
                            break;
                        }
                    }
                    if (empty_decimals)
                        builder += parts[0] + "i";
                    else
                        builder += cn.iota + "i";
                } else
                    builder += cn.iota + "i";
                return builder;
            }
        }
        return "no result and only god knows why";
    }

    private void bracketReplacement(Stack<String> stack) {
        stack.resetLoopAtPointer();
        while (stack.loop()) {
            String str = stack.get();
            if (str.equals("{"))
                stack.replaceInLoop("}");
            else if (str.equals("}"))
                stack.replaceInLoop("{");
        }
    }

    private static final class DispatchParcel {
        private int type = -1;
        private ComplexNumber number;
        private String string;
        private Set set;
        private Variable var;
        private Constant con;
        private Terms term;
    }
}
