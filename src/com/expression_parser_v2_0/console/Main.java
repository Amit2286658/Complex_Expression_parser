package com.expression_parser_v2_0.console;

//no inheritance.
public final class Main {
    //no instance creation.
    private Main(){
        //empty private constructor.
    }
    /*
    *my keyboard got smacked last night because something happened..., never mind, these keys
    *don't work any longer, and I'm keeping it here just so that I don't have to open
    *the character map every time.

    \ -> reverse slash, for line breaking when you're feeling lazy to just add another println.
    | -> latin iota (is that what it's called?), for char "or" condition.
    || -> double latin iota(still in doubt), for string "or" condition.
    */

    //when true, the entered will be converted into the radians since that's the default accepting parameter
    //for the java math functions, when false, no conversion will happen.
    private static boolean use_degree = true;

    public static int getAngleMode() {
        return use_degree ? ANGLE_MODE_DEGREE : ANGLE_MODE_RADIAN;
    }

    public static void setAngleMode(int angleMode) {
        if (angleMode == ANGLE_MODE_DEGREE)
            use_degree = true;
        else if (angleMode == ANGLE_MODE_RADIAN)
            use_degree = false;
        else
            throw new IllegalArgumentException("unknown angle mode");
    }

    private static final char
            complex_token = '$',
            function_token = '@';

    private static Stack<operationsInterface> operations = new Stack<>();
    private static Stack<functionsInterface> functions = new Stack<>();

    public static final int
            IOTA_FIRST = 1,
            IOTA_SECOND = 2,
            IOTA_BOTH = 3,
            IOTA_NONE = 0,
            IOTA_TRUE = 1,
            IOTA_FALSE = 0,
            RESULT_REAL = 1,
            RESULT_IOTA = 2,
            RESULT_COMPLEX = 3,
            ARGUMENT_DOUBLE = 1,
            ARGUMENT_IOTA = 2,
            ARGUMENT_COMPLEX = 3,
            ARGUMENT_STRING = 4,
            ARGUMENT_ARRAY = 5,
            TYPE_PRE = 0,
            TYPE_POST = 1,
            TYPE_BOTH = 2,
            TYPE_CONSTANT = 3,
            ANGLE_MODE_RADIAN = 0,
            ANGLE_MODE_DEGREE = 1,
            PRECEDENCE_LEAST = 1,
            PRECEDENCE_MEDIUM = 100,
            PRECEDENCE_MAX = 500;

    private static final int
            PRECEDENCE_FUNCTION = 1000;

    public interface operationsInterface {
        String[] getOperationNames();
        char getOperator();
        int getPrecedence();
        int getType();
        int getResultFlag();
        double getDoubleResult();
        ComplexNumber getComplexResult();
        void function();
        void function(double d, int iotaStatus);
        void function(ComplexNumber cn);
        void function(double d1, double d2, int iotaStatus);
        void function(ComplexNumber c1, double d2, int iotaStatus);
        void function(double d1, ComplexNumber c2, int iotaStatus);
        void function(ComplexNumber c1, ComplexNumber c2);
    }

    public interface functionsInterface{
        String getFunctionName();
        int[] getFunctionMap();
        int getId();
        int getResultFlag();
        double getDoubleResult();
        ComplexNumber getComplexResult();
        void function(Argument[] arguments, int id);
    }

    public static void registerOperation(operationsInterface opInt){
        operations.push(opInt);
    }

    public static void registerFunction(functionsInterface fs){
        functions.push(fs);
    }

    public static String Evaluate(String expression){
        if (operations.isEmpty() && functions.isEmpty())
            return expression;
        if (expression.length() == 0)
            throw new IllegalArgumentException("given input string is empty");

        expression = expression.replaceAll("\\s+", "");

        expression = GeneralParser(expression);
        expression = implicitSolver(expression);
        expression = functionHandler(expression);
        expression = complexConverter(expression);
        expression = postFixConverter(expression);
        expression = postFixEvaluator(expression);
        return SortResult(expression);
    }

    //general parser to resolve minor errors in a string.
    private static String GeneralParser(String expression){
        expression = expression.trim();
        expression = expression.replaceAll("\\)\\(", "\\)*\\(");
        expression = expression.replaceAll("\\+\\+", "\\+");
        expression = expression.replaceAll("\\+-", "-");
        expression = expression.replaceAll("-\\+", "-");
        expression = expression.replaceAll("--", "\\+");
        expression = expression.replaceAll("i\\(", "i*\\(");

        Stack<String> fn_names = new Stack<>();
        functions.reset();
        while (functions.hasNextItem()){
            functionsInterface fnInt = functions.get();
            String name = fnInt.getFunctionName();
            if (!fn_names.contains(name)){
                expression = expression.replace(name, function_token + name);
                fn_names.push(name);
            }
        }

        //create name array
        StringBuilder nameBuilder = new StringBuilder();
        operations.reset();
        while (operations.hasNextItem()){
            operationsInterface opInt = operations.get();
            if (opInt.getOperationNames() != null) {
                for (String name : opInt.getOperationNames()){
                    if (!name.isEmpty()) {
                        nameBuilder.append(name);
                        nameBuilder.append(".");
                        nameBuilder.append(opInt.getOperator());
                        nameBuilder.append("?");
                    }
                }
            }
        }
        //sort name array according to their size in descending order, using bubble sort? the name array,
        //cannot be that big to cause any significant performance drop.
        String[] list = nameBuilder.toString().split("\\?");
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
        //name replacement
        for (String str : list){
            String[] contents = str.split("\\.");
            expression = expression.replace(contents[0], contents[1]);
        }

        StringBuilder builder = new StringBuilder();
        //only solve for the bracket related implicit multiplication
        for (int i = 0; i < expression.length(); i++){
            char c = expression.charAt(i);

            char p = 0;
            if (i != 0)
                p = expression.charAt(i - 1);
            char a = 0;
            if (i != expression.length() - 1)
                a = expression.charAt(i + 1);

            if (Character.isDigit(c)) {
                if (p == ')') {
                    builder.append('*');
                }
                builder.append(c);
                if (a == '(' || a == function_token) {
                    builder.append('*');
                }
            }else if(c == function_token){
                if (p == ')')
                    builder.append('*');
                builder.append(c);
            }else{
                builder.append(c);
            }
        }
        expression = builder.toString();
        return expression;
    }

    private static String complexConverter(String expression){
        StringBuilder builder = new StringBuilder();
        StringBuilder currentStep = new StringBuilder();
        outer_loop :
        for (int i = 0; i < expression.length(); i++){
            String c = String.valueOf(expression.charAt(i));
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
                if (c.matches("[+-]")){
                    char p;
                    if (i != 0) {
                        p = expression.charAt(i - 1);
                        operations.reset();
                        while (operations.hasNextItem()) {
                            operationsInterface opInt = operations.get();
                            if (opInt.getOperator() == p) {
                                if (opInt.getType() == TYPE_POST || opInt.getType() == TYPE_BOTH) {
                                    currentStep.append(c);
                                    continue outer_loop;
                                }
                                break;
                            }
                        }
                        if (p == '('){
                            currentStep.append(c);
                            continue;
                        }
                    }else {
                        currentStep.append(c);
                        continue;
                    }
                }
                if (!currentStep.toString().equals("")){
                    if (currentStep.toString().contains("i")){
                        builder.append(convertToComplexString(Double.parseDouble(
                                currentStep.toString().replaceAll("i", "")), true));
                    }else{
                        builder.append(convertToComplexString(Double.parseDouble(
                                currentStep.toString()), false));
                    }
                    currentStep = new StringBuilder();
                }
                builder.append(c);
            }
        }
        if (!currentStep.toString().equals("")){
            if (currentStep.toString().contains("i")){
                builder.append(convertToComplexString(Double.parseDouble(
                        currentStep.toString().replaceAll("i", "")), true));
            }else{
                builder.append(convertToComplexString(Double.parseDouble(currentStep.toString()), false));
            }
        }
        return builder.toString();
    }

    //does not resolve any error, provide a valid string.
    private static String postFixConverter(String expression){
        StringBuilder complexString = new StringBuilder();
        StringBuilder functionName = new StringBuilder();

        boolean tokenCounter = false;
        boolean isInFunction = false;

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
                    output.push(complexString.toString());
                    complexString.setLength(0);
                }
            } else if (tokenCounter){
                complexString.append(c);
            } else if (c == function_token){
                if (!isInFunction){
                    isInFunction = true;
                    functionName.append(c);
                }
            } else if (isInFunction){
                char a = expression.charAt(i + 1);
                functionName.append(c);
                if (a == '(') {
                    isInFunction = false;
                    operators.push(functionName.toString());
                    functionName.setLength(0);
                }
            }
            else {
                if (c == ')'){
                    while(operators.hasNext()){
                        char p = operators.peek().charAt(0);
                        if (p != '(') {
                            if (p == ',') {
                                operators.pop();
                                continue;
                            }
                            output.push(operators.pop() + "");
                        } else {
                            operators.pop();
                            break;
                        }
                    }
                }else if (c == '(')
                    operators.push(c + "");
                else{
                    if (c == ',') {
                        while (operators.hasNext()){
                            char t = operators.peek().charAt(0);
                            if (t == ',' || t == '('){
                                if (t == ',') {
                                    operators.pop();
                                    operators.push(c + "");
                                }
                                break;
                            } else {
                                output.push(operators.pop());
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
                        if (p != '(') {
                            boolean isFun = false;
                            if (p == function_token)
                                isFun = true;
                            operationsInterface previous_ops = null;
                            if (!isFun)
                                previous_ops = getCharAsAnOperator(p);
                            if (current_ops.getPrecedence() <= (!isFun ? previous_ops.getPrecedence() :
                                    PRECEDENCE_FUNCTION)){
                                output.push(operators.pop() + "");
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
        while(operators.hasNext())
            output.push(operators.pop());

        Stack<String> temp = new Stack<>();

        while(output.hasNext())
            temp.push(output.pop());

        while(temp.hasNext())
            complexString.append(temp.pop()).append(',');

        System.out.println("post fix string => " + complexString.toString());

        return complexString.toString();
    }

    //does not resolve any error.
    private static String postFixEvaluator(String post_fix){
        String[] list = post_fix.split(",");
        Stack<String> exp = new Stack<>();

        outer_loop :
        for (String s : list) {
            if (s.charAt(0) == function_token) {
                String name = s.replace(function_token + "", "");
                //loop has finished, which means
                //the parameters didn't match with any overload, throw an exception.
                //if everything goes well, the current iteration of the outer loop will be skipped.
                //therefore, there's absolutely no need to put the items back onto the stack.
                String[] params = new String[(int) convertToComplexNumber(exp.pop()).real];
                for (int j = params.length - 1; j >= 0; j--) {
                    params[j] = exp.pop();
                }
                functions.reset();
                while (functions.hasNextItem()) {
                    functionsInterface fn = functions.get();
                    if (fn.getFunctionName().equals(name)) {
                        try {
                            ComplexNumber result = functionDispatcher(params, fn);
                            exp.push(convertComplexToString(result, false));
                            continue outer_loop;
                        } catch (IllegalArgumentException e) {
                            //empty
                        }
                    }
                }
                throw new ExpressionException("the function parameters didn't match with any overload");
            } else if (s.length() == 1) {
                operationsInterface ops = getCharAsAnOperator(s.charAt(0));
                switch (ops.getType()) {
                    case TYPE_CONSTANT:
                        ComplexNumber cn_constant = dispatcher(null, null, ops);
                        exp.push(convertComplexToString(cn_constant, false));
                        break;
                    case TYPE_BOTH:
                        ComplexNumber right = convertToComplexNumber(exp.pop());
                        ComplexNumber left = convertToComplexNumber(exp.pop());
                        ComplexNumber cn_both = dispatcher(left, right, ops);
                        exp.push(convertComplexToString(cn_both, false));
                        break;
                    case TYPE_PRE:
                        ComplexNumber left_1 = convertToComplexNumber(exp.pop());
                        ComplexNumber cn_pre = dispatcher(left_1, null, ops);
                        exp.push(convertComplexToString(cn_pre, false));
                        break;
                    case TYPE_POST:
                        ComplexNumber right_1 = convertToComplexNumber(exp.pop());
                        ComplexNumber cn_post = dispatcher(null, right_1, ops);
                        exp.push(convertComplexToString(cn_post, false));
                        break;
                    default:
                        break;
                }
            } else {
                exp.push(s);
            }
        }
        return exp.pop();
    }

    private static String functionHandler(String expression){
        expression = functionUpdater(expression);

        Stack<String> fn_names = new Stack<>();
        functions.reset();
        while (functions.hasNextItem()){
            functionsInterface fnInt = functions.get();
            String name = fnInt.getFunctionName();
            if (!fn_names.contains(name)){
                expression = expression.replace(name, function_token + name);
                fn_names.push(name);
            }
        }
        return expression;
    }

    private static String functionUpdater(String expression){
        StringBuilder builder = new StringBuilder();

        boolean param_scan = false;
        int bracket_counter = 0;
        int param_count = 0;
        boolean isInFunction = false;

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
                else if (c ==',' && bracket_counter == 1)
                    param_count++;
            }

        }
        expression = builder.toString();

        if (builder.toString().contains(function_token + ""))
            expression = functionUpdater(expression);

        System.out.println(expression);
        return expression;
    }

    private static String implicitSolver(String expression){
        StringBuilder builder = new StringBuilder();

        outer_loop :
        for (int i = 0; i < expression.length(); i++){
            char c = expression.charAt(i);

            char p = 0;
            if (i != 0)
                p = expression.charAt(i - 1);
            char a = 0;
            if (i != expression.length() - 1)
                a = expression.charAt(i + 1);

            operations.reset();
            while (operations.hasNextItem()){
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

    private static String getImplicitExp(operationsInterface opInt, char left, char right){
        //no need to check for either a dot '.', or exponent ';', in each case,
        //they must end with a number, or else they'll be invalid.
        //only left operand can have an iota 'i'.
        boolean l = (left + "").matches("[1234567890)i]");
        boolean r = (right + "").matches("[1234567890(@]");

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
            switch(opInt.getType()){
                case TYPE_BOTH :
                    if (right_ops == null)
                        throw new ExpressionException("the operator type is both, however the right operand is" +
                                " a closing bracket");

                    switch(right_ops.getType()){
                        case TYPE_BOTH :
                            if (right_ops.getOperator() == '+' || right_ops.getOperator() == '-')
                                return opInt.getOperator() + "";
                        case TYPE_PRE :
                            throw new ExpressionException("the right operand is of type pre or" +
                                    " both while the left operator" +
                                    " is of type both, they both need an operand in between to work.");
                        case TYPE_POST :
                        case TYPE_CONSTANT :
                            if (precedence_current >= precedence_right)
                                throw new ExpressionException("the right operator is of type post or constant" +
                                        " while the left operator " +
                                        "is of type both, but the left operator's precedence " +
                                        "is greater than or equal to " +
                                        "so the left operator will require an operand to work.");
                            else
                                return opInt.getOperator() + "";
                        default : throw new ExpressionException("unknown type");
                    }
                case TYPE_PRE :
                    if (right_ops == null)
                        return opInt.getOperator() + "";
                    switch(right_ops.getType()){
                        case TYPE_BOTH :
                        case TYPE_PRE :
                            if (precedence_right > precedence_current)
                                throw new ExpressionException("the right operator is of type both or pre" +
                                        " and has a greater precedence than the left operator.");
                            else
                                return opInt.getOperator() + "";
                        case TYPE_POST :
                        case TYPE_CONSTANT :
                            return opInt.getOperator() + "*";
                        default :
                            throw new ExpressionException("unknown type");
                    }
                case TYPE_POST :
                    if (right_ops == null)
                        throw new ExpressionException("the type is post, however the right operand is a " +
                                "closing bracket.");
                    switch(right_ops.getType()){
                        case TYPE_BOTH :
                            if (right_ops.getOperator() == '+' || right_ops.getOperator() == '-')
                                return opInt.getOperator() + "";
                        case TYPE_PRE :
                            throw new ExpressionException("the right operator is of type both and the left operator" +
                                    " is of type post, an operand in between is required regardless of the precedence");
                        case TYPE_POST :
                            if (precedence_current > precedence_right)
                                throw new ExpressionException("the left and right operator, both is of type post, " +
                                        "but the left operator has a greater precedence.");
                            else
                                return "*" + opInt.getOperator();
                        case TYPE_CONSTANT :
                            if (precedence_current >= precedence_right)
                                throw new ExpressionException("the left and right operator, both is of type " +
                                        "constant, but the left operator has a greater precedence.");
                            else
                                return "*" + opInt.getOperator();
                        default :
                            throw new ExpressionException("unknown type");
                    }
                case TYPE_CONSTANT :
                    if (right_ops == null)
                        return "*" + opInt.getOperator() + "";
                    switch(right_ops.getType()){
                        case TYPE_BOTH :
                        case TYPE_PRE :
                            if (precedence_right > precedence_current)
                                throw new ExpressionException("the right operator is either of type pre or both " +
                                        "and has a greater precedence than the left one.");
                            else
                                return "*" + opInt.getOperator();
                        case TYPE_POST :
                        case TYPE_CONSTANT :
                            return "*" + opInt.getOperator() +  "*";
                        default :
                            throw new ExpressionException("unknown type");
                    }
            }
        }else if (r) {
            //the left char is guaranteed to be an operator or (an opening bracket or empty).
            operationsInterface left_ops = getCharAsAnOperator(left);
            int precedence_current = opInt.getPrecedence();
            int precedence_left = left_ops != null ? left_ops.getPrecedence() : -1;
            switch (opInt.getType()){
                case TYPE_BOTH :
                    if (opInt.getOperator() == '+' || opInt.getOperator() == '-')
                        return opInt.getOperator() + "";
                    if (left_ops == null)
                        if (opInt.getOperator() != '+' || opInt.getOperator() != '-')
                            throw new ExpressionException("the operator is of type both but the left operand" +
                                    " is an opening bracket");
                        else
                            return opInt.getOperator() + "";
                    switch(left_ops.getType()){
                        case TYPE_PRE :
                        case TYPE_CONSTANT :
                            if (precedence_current <= precedence_left)
                                return opInt.getOperator() + "";
                        default :
                            return opInt.getOperator() + "";
                    }
                case TYPE_PRE :
                    if (left_ops == null)
                        throw new ExpressionException("the operator is of type pre but the left operand" +
                                " is an opening bracket");
                    switch(left_ops.getType()){
                        case TYPE_PRE :
                            if (precedence_current <= precedence_left)
                                return opInt.getOperator() + "*";
                        case TYPE_CONSTANT :
                            if (precedence_current <= precedence_left)
                                return opInt.getOperator() + "";
                        default :
                            return opInt.getOperator() + "*";
                    }
                case TYPE_POST :
                    if (left_ops == null)
                        return "" + opInt.getOperator();
                    switch(left_ops.getType()){
                        case TYPE_BOTH :
                            if (precedence_left < precedence_current)
                                return opInt.getOperator() + "";
                        case TYPE_POST :
                            if (precedence_left <= precedence_current)
                                return opInt.getOperator() + "";
                        case TYPE_CONSTANT :
                            return opInt.getOperator() + "";
                        default :
                            return opInt.getOperator() + "";
                    }
                case TYPE_CONSTANT :
                    if (left_ops == null)
                        return "" + opInt.getOperator() + "*";
                    switch(left_ops.getType()){
                        case TYPE_BOTH :
                        case TYPE_POST :
                            if (precedence_left < precedence_current)
                                return opInt.getOperator() + "*";
                        case TYPE_PRE :
                        case TYPE_CONSTANT :
                            return opInt.getOperator() + "*";
                        default :
                            return opInt.getOperator() + "*";
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
                                    throw new ExpressionException("the right operator has type pre or both both has " +
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
                switch(opInt.getType()){
                    case TYPE_BOTH :
                        builder.append(opInt.getOperator());
                        switch(right_operator.getType()){
                            case TYPE_BOTH :
                                if (right_operator.getOperator() == '+' || right_operator.getOperator() == '-')
                                    return opInt.getOperator() + "";
                            case TYPE_PRE :
                                throw new ExpressionException("the right operand is of type pre or both while" +
                                        " the left operator" +
                                        " is of type both, they both need an operand in between to work.");
                            case TYPE_POST :
                            case TYPE_CONSTANT :
                                if (precedence_current >= precedence_right)
                                    throw new ExpressionException("the right operator is of type post or constant" +
                                            " while the left operator " +
                                            "is of type both, but the left operator's precedence " +
                                            "is greater than or equal to " +
                                            "so the left operator will require an operand to work.");
                                break;
                        }
                        break;
                    case TYPE_PRE :
                        builder.append(opInt.getOperator());
                        switch(right_operator.getType()){
                            case TYPE_BOTH :
                            case TYPE_PRE :
                                if (precedence_right > precedence_current)
                                    throw new ExpressionException("the right operator is of type both or pre" +
                                            " and has a greater precedence than the left operator.");
                                break;
                            case TYPE_POST :
                            case TYPE_CONSTANT :
                                builder.append("*");
                                break;
                        }
                        break;
                    case TYPE_POST :
                        builder.append(opInt.getOperator());
                        switch(right_operator.getType()){
                            case TYPE_BOTH :
                                if (right_operator.getOperator() == '+' || right_operator.getOperator() == '-')
                                    return opInt.getOperator() + "";
                            case TYPE_PRE :
                                throw new ExpressionException("the right operator is of" +
                                        " type both and the left operator" +
                                        " is of type post, an operand in between is" +
                                        " required regardless of the precedence");
                            case TYPE_POST :
                                if (precedence_current > precedence_right)
                                    throw new ExpressionException("the left and right operator, " +
                                            "both is of type post, " +
                                            "but the left operator has a greater precedence.");
                                break;
                            case TYPE_CONSTANT :
                                if (precedence_current >= precedence_right)
                                    throw new ExpressionException("the left and right operator, both is of type " +
                                            "constant, but the left operator has a greater precedence.");
                                break;
                        }
                        break;
                    case TYPE_CONSTANT :
                        builder.append(opInt.getOperator());
                        switch(right_operator.getType()){
                            case TYPE_BOTH :
                            case TYPE_PRE :
                                if (precedence_right > precedence_current)
                                    throw new ExpressionException("the right operator is either of type pre or both " +
                                            "and has a greater precedence than the left one.");
                                break;
                            case TYPE_POST :
                            case TYPE_CONSTANT :
                                builder.append("*");
                                break;
                        }
                        break;
                }
                return builder.toString();
            }
        }
        return opInt.getOperator() + "";
    }

    private static ComplexNumber dispatcher(ComplexNumber c1, ComplexNumber c2,
                                            operationsInterface whichOperation){
        ComplexNumber number = new ComplexNumber(0, 0);
        double
                c1r = c1 != null ? c1.real : 0,
                c1i = c1 != null ? c1.iota : 0,
                c2r = c2 != null ? c2.real : 0,
                c2i = c2 != null ? c2.iota : 0;
        switch(whichOperation.getType()) {
            case TYPE_BOTH:
                if ((whichOperation.getOperator() + "").matches("[-+]") && c1 == null){
                    if (c2r != 0 && c2i != 0)
                        whichOperation.function(c2);
                    else if(c2r != 0)
                        whichOperation.function(c2r, IOTA_FALSE);
                    else if(c2i != 0)
                        whichOperation.function(c2i, IOTA_TRUE);
                    else
                        whichOperation.function(c2);
                    break;
                }
                //under normal circumstances
                if (c1r != 0 && c1i == 0 && c2r != 0 && c2i == 0)
                    whichOperation.function(c1r, c2r, IOTA_NONE);
                else if (c1r == 0 && c1i != 0 && c2r == 0 && c2i != 0)
                    whichOperation.function(c1i, c2i, IOTA_BOTH);
                else if (c1r != 0 && c1i == 0 && c2r == 0 && c2i != 0)
                    whichOperation.function(c1r, c2i, IOTA_SECOND);
                else if (c1r == 0 && c1i != 0 && c2r != 0 && c2i == 0)
                    whichOperation.function(c1i, c2r, IOTA_FIRST);
                else if (c1r != 0 && c1i != 0 && c2r != 0 && c2i == 0)
                    whichOperation.function(c1, c2r, IOTA_FALSE);
                else if (c1r != 0 && c1i != 0 && c2r == 0 && c2i != 0)
                    whichOperation.function(c1, c2i, IOTA_TRUE);
                else if (c1r != 0 && c1i == 0 && c2r != 0 && c2i != 0)
                    whichOperation.function(c1r, c2, IOTA_FALSE);
                else if (c1r == 0 && c1i != 0 && c2r != 0 && c2i != 0)
                    whichOperation.function(c1i, c2, IOTA_TRUE);
                else if (c1r != 0 && c1i != 0 && c2r != 0 && c2i != 0)
                    whichOperation.function(c1, c2);
                else if (c1r == 0 && c1i == 0 && c2r == 0 && c2i == 0)
                    whichOperation.function(c1r, c2r, IOTA_NONE);
                else if (c1r != 0 && c1i == 0 && c2r == 0 && c2i == 0)
                    whichOperation.function(c1r, c2r, IOTA_NONE);
                else if (c1r == 0 && c1i == 0 && c2r != 0 && c2i == 0)
                    whichOperation.function(c1r, c2r, IOTA_NONE);
                else if (c1r == 0 && c1i != 0 && c2r == 0 && c2i == 0)
                    whichOperation.function(c1i, c2r, IOTA_FIRST);
                else if (c1r == 0 && c1i == 0 && c2r == 0 && c2i != 0)
                    whichOperation.function(c1r, c2i, IOTA_SECOND);
                else if (c1r == 0 && c1i == 0 && c2r != 0 && c2i != 0)
                    whichOperation.function(c1, c2);
                else if (c1r != 0 && c1i != 0 && c2r == 0 && c2i == 0)
                    whichOperation.function(c1, c2);
                else
                    throw new IllegalStateException("Unexpected condition in dispatcher");
                break;
            case TYPE_PRE :
                if (c1r != 0 && c1i != 0)
                    whichOperation.function(c1);
                else if(c1r != 0)
                    whichOperation.function(c1r, IOTA_FALSE);
                else if(c1i != 0)
                    whichOperation.function(c1i, IOTA_TRUE);
                else
                    whichOperation.function(c1);
                break;
            case TYPE_POST :
                if (c2r != 0 && c2i != 0)
                    whichOperation.function(c2);
                else if(c2r != 0)
                    whichOperation.function(c2r, IOTA_FALSE);
                else if(c2i != 0)
                    whichOperation.function(c2i, IOTA_TRUE);
                else
                    whichOperation.function(c2);
                break;
            case TYPE_CONSTANT :
                whichOperation.function();
                break;
        }

        int result_flag = whichOperation.getResultFlag();

        switch (result_flag){
            case RESULT_REAL :
                number.real = whichOperation.getDoubleResult();
                break;
            case RESULT_IOTA :
                number.iota = whichOperation.getDoubleResult();
                break;
            case RESULT_COMPLEX :
                number = whichOperation.getComplexResult();
                break;
            default: break;
        }

        return number;
    }

    //the caller should take care if the sub_expression is empty or not, the dispatcher
    // will not resolve any problems.
    private static ComplexNumber functionDispatcher(String[] sub_expression, functionsInterface fs){
        int[] map = fs.getFunctionMap();
        Argument[] arguments;

        boolean isArray = false;

        if (map[0] == ARGUMENT_ARRAY){
            if (map.length != 2)
                throw new ExpressionException("the functions that expects an array can only accept one type" +
                        " of argument, in this case, the map declaration can only have two types," +
                        " the first declaring it an array and the second being the type," +
                        " whose array is being expected");
            isArray = true;
        }

        arguments = new Argument[isArray ? sub_expression.length : map.length];

        if (!isArray) {
            if (map.length != sub_expression.length)
                throw new IllegalArgumentException("the given arguments does not match the method parameter map");
            for (int i = 0; i < map.length; i++) {
                int id = map[i];
                switch (id) {
                    case ARGUMENT_DOUBLE:
                        try {
                            ComplexNumber cn = convertToComplexNumber(sub_expression[i]);
                            if (cn.real != 0 && cn.iota != 0)
                                throw new NumberFormatException("the number is a complex number");
                            else if (cn.iota != 0)
                                throw new NumberFormatException("the given number is an iota");
                            double real = cn.real;
                            arguments[i] = new Argument(real, null, null);
                        } catch (NumberFormatException e) {
                            throw new IllegalArgumentException(e.getMessage());
                        } catch (Exception e) {
                            throw new IllegalArgumentException("the given double is not parsable");
                        }
                        break;
                    case ARGUMENT_IOTA:
                        try {
                            ComplexNumber cn = convertToComplexNumber(sub_expression[i]);
                            if (cn.real != 0 && cn.iota != 0)
                                throw new NumberFormatException("the number is a complex number");
                            else if (cn.real != 0)
                                throw new NumberFormatException("the given number is real");
                            double iota = cn.iota;
                            arguments[i] = new Argument(iota, null, null);
                        } catch (NumberFormatException e) {
                            throw new IllegalArgumentException(e.getMessage());
                        } catch (Exception e) {
                            throw new IllegalArgumentException("the given double is not parsable");
                        }
                        break;
                    case ARGUMENT_COMPLEX:
                        try {
                            ComplexNumber cn = convertToComplexNumber(sub_expression[i]);
                            if (cn.real == 0 || cn.iota == 0)
                                throw new NumberFormatException("the given complex number is in fact a number");
                            arguments[i] = new Argument(0, cn, null);
                        } catch (NumberFormatException e) {
                            throw new IllegalArgumentException(e.getMessage());
                        } catch (Exception e) {
                            throw new IllegalArgumentException("the given complex number is not correct");
                        }
                        break;
                    case ARGUMENT_STRING:
                        arguments[i] = new Argument(0, null, sub_expression[i]);
                        break;
                    case ARGUMENT_ARRAY :
                        throw new ExpressionException("the array declaration must be the first element");
                    default:
                        break;
                }
            }
        }else {
            int type = map[1];
            for (int i = 0; i < sub_expression.length; i++){
                switch(type){
                    case ARGUMENT_DOUBLE:
                        try {
                            ComplexNumber cn = convertToComplexNumber(sub_expression[i]);
                            if (cn.real != 0 && cn.iota != 0)
                                throw new NumberFormatException("the number is a complex number");
                            else if (cn.iota != 0)
                                throw new NumberFormatException("the given number is an iota");
                            double real = cn.real;
                            arguments[i] = new Argument(real, null, null);
                        } catch (NumberFormatException e) {
                            throw new IllegalArgumentException(e.getMessage());
                        } catch (Exception e) {
                            throw new IllegalArgumentException("the given double is not parsable");
                        }
                        break;
                    case ARGUMENT_IOTA:
                        try {
                            ComplexNumber cn = convertToComplexNumber(sub_expression[i]);
                            if (cn.real != 0 && cn.iota != 0)
                                throw new NumberFormatException("the number is a complex number");
                            else if (cn.real != 0)
                                throw new NumberFormatException("the given number is real");

                            double iota = cn.iota;
                            arguments[i] = new Argument(iota, null, null);
                        } catch (NumberFormatException e) {
                            throw new IllegalArgumentException(e.getMessage());
                        } catch (Exception e) {
                            throw new IllegalArgumentException("the given double is not parsable");
                        }
                        break;
                    case ARGUMENT_COMPLEX:
                        try {
                            ComplexNumber cn = convertToComplexNumber(sub_expression[i]);
                            if (cn.real == 0 || cn.iota == 0)
                                throw new NumberFormatException("the given complex number is in fact a number");
                            arguments[i] = new Argument(0, cn, null);
                        } catch (NumberFormatException e) {
                            throw new IllegalArgumentException(e.getMessage());
                        } catch (Exception e) {
                            throw new IllegalArgumentException("the given complex number is not correct");
                        }
                        break;
                    case ARGUMENT_STRING:
                        arguments[i] = new Argument(0, null, sub_expression[i]);
                        break;
                    case ARGUMENT_ARRAY :
                        throw new ExpressionException("the array declaration must be the first" +
                                " and only element of its kind");
                    default:
                        break;
                }
            }
        }

        fs.function(arguments, fs.getId());

        int resultFlag = fs.getResultFlag();

        switch(resultFlag){
            case RESULT_REAL :
                return convertToComplexNumber(fs.getDoubleResult(), false);
            case RESULT_IOTA :
                return convertToComplexNumber(fs.getDoubleResult(), true);
            case RESULT_COMPLEX :
                return fs.getComplexResult();
            default :
                return new ComplexNumber();
        }
    }

    private static operationsInterface getCharAsAnOperator(char c){
        operationsInterface opInt = null;
        operations.reset();
        while (operations.hasNextItem()){
            operationsInterface ops = operations.get();
            if (ops.getOperator() == c)
                opInt = ops;
        }
        return opInt;
    }

    private static String SortResult(String result){
        result = result.replace(String.valueOf(complex_token), "");
        ComplexNumber cn = convertToComplexNumber(result);

        if (cn.real != 0 && cn.iota == 0){
            if (cn.real % 1 == 0)
                return String.valueOf((int)cn.real);
            else
                return String.valueOf(cn.real);
        }else if (cn.real == 0 && cn.iota != 0){
            if (cn.iota % 1 == 0)
                return (int) cn.iota + "i";
            else
                return cn.iota + "i";
        }else if (cn.real == 0 && cn.iota == 0){
            return "0";
        }else if (cn.real != 0 && cn.iota != 0){
            String builder = "";
            if (cn.real % 1 == 0)
                builder += (int)(cn.real);
            else
                builder += cn.real;
            if (cn.iota > 0)
                builder += "+";
            if (cn.iota % 1 == 0)
                builder += (int) (cn.iota) + "i";
            else
                builder += cn.iota + "i";
            return builder;
        }
        throw new IllegalStateException("unexpected condition in the SortResult function");
    }

    private static ComplexNumber convertToComplexNumber(String complexString){
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

    private static String convertComplexToString(ComplexNumber cn, boolean c_t){
        return (c_t ? complex_token : "") + "" + cn.real +
                (cn.iota >= 0 ? "+" : "") + cn.iota + "i" + (c_t ? complex_token : "");
    }

    static class ComplexNumber {
        public double real = 0, iota = 0;

        public ComplexNumber(){
            //empty
        }

        public ComplexNumber(double real, double iota){
            this.real = real;
            this.iota = iota;
        }
    }

    static class Argument{
        private double d;
        private ComplexNumber cn;
        private String str;

        Argument(double d, ComplexNumber cn, String str){
            this.d = d;
            this.cn = cn;
            this.str = str;
        }

        public double getDoubleArgument() {
            return d;
        }

        public ComplexNumber getComplexArgument() {
            return cn;
        }

        public String getStringArgument() {
            return str;
        }
    }

    static class ExpressionException extends RuntimeException {
        ExpressionException(String message){
            super(message);
        }
    }

    //LIFO Structure
    static class Stack<T> {
        private Object[] items;
        //internal Stack pointer;
        private int pointer = -1;

        //for iteration but no actual data modification.
        private int pseudo_counter = -1;

        Stack(int size) {
            items = new Object[size];
        }

        Stack(){
            this(10);
        }

        private void push(T item){
            //increment the pointer
            pointer++;

            if (pointer <= items.length - 1) {
                items[pointer] = item;
                return;
            }
            //no index is free, allocate a new array with increased size;
            var items1 = new Object[items.length + 10];
            //copy the previous array over to the new one.
            System.arraycopy(items, 0, items1, 0, items.length);
            //reassignment
            items = items1;

            items[pointer] = item;
        }

        @SuppressWarnings("unchecked")
        private T pop(){
            if (pointer < 0)
                throw new IndexOutOfBoundsException("pointer is out of bounds");

            T item;
            item = (T) items[pointer];
            //no need to remove the data at the current pointer
            //since the pointer will be decremented, and the next time the push function will be called,
            //the pointer will be incremented and any data at the current pointer location will be replaced.

            //decrement the pointer
            pointer--;

            return item;
        }

        private T peek(){
            if (pointer >= 0)
                //noinspection unchecked
                return (T) items[pointer];
            throw new IndexOutOfBoundsException("pointer is out of bounds");
        }

        private boolean hasNext(){
            //pointer is only incremented when an item is pushed onto the stack, therefore providing
            //null safety by default, meaning wherever the pointer is located, it's guaranteed to be occupied,
            //by a certain object.
            return pointer >= 0;
        }

        //no pointer modification.
        @SuppressWarnings("BooleanMethodIsAlwaysInverted")
        private boolean contains(T item){
            for(Object op : items){
                if (op != null && op.equals(item)){
                    return true;
                }
            }
            return false;
        }

        private boolean isEmpty(){
            return getLength() == 0;
        }
        private int getLength(){
            return items.length;
        }

        private int getPointerLocation() {
            return pointer;
        }

        //iteration purpose functions.
        public boolean hasNextItem(){
            return pseudo_counter >= 0;
        }

        @SuppressWarnings("unchecked")
        public T get(){
            T item = (T) items[pseudo_counter];
            pseudo_counter--;
            return item;
        }

        public void reset(){
            pseudo_counter = pointer;
        }
    }
}
