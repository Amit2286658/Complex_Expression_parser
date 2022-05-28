package com.expression_parser_v2_0.console;

import java.util.ArrayList;
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

    private static char
            complex_token = '$',
            function_token = '@',
            neutral_token = '#';

    private static ArrayList<operationsInterface> operations = new ArrayList<>();
    private static ArrayList<functionsInterface> functions = new ArrayList<>();

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
            ANGLE_MODE_DEGREE = 1;

    private static final int
            NEUTRAL_IDENTITY = 1;


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
        operations.add(opInt);
    }

    public static void registerFunction(functionsInterface fs){
        functions.add(fs);
    }

    public static String Evaluate(String expression){
        if (operations.isEmpty() && functions.isEmpty())
            return expression;
        if (expression.length() == 0)
            throw new IllegalArgumentException("given input string is empty");

        expression = EvaluateFunction(expression);
        expression = GeneralParser(expression);
        expression = implicitSolver(expression);
        //System.out.println("implicitly solved : " + expression);
        expression = converter(expression);
        //System.out.println("complex converted expression : " + expression);
        expression = BracketsResolver(expression);
        expression = EvaluateOperation(expression);
        return SortResult(expression);
    }

    private static String EvaluateFunction(String expression){
        if(functions.isEmpty())
            return expression;

        StringBuilder builder = new StringBuilder();
        for (functionsInterface fs : functions){
            expression = convertFunctionString(expression, fs.getFunctionName());
            boolean isInsideFunction = false;
            int bracketCounter = 0;
            StringBuilder sub_string = new StringBuilder();
            for (int i = 0; i < expression.length(); i++){
                char c = expression.charAt(i);
                if (c == function_token){
                    isInsideFunction = true;
                    continue;
                }

                if (!isInsideFunction)
                    builder.append(c);

                if (isInsideFunction){
                    if (c == '('){
                        if (bracketCounter != 0)
                            sub_string.append(c);
                        bracketCounter++;
                        continue;
                    }
                    if (c == ')'){
                        bracketCounter--;
                        if (bracketCounter != 0)
                            sub_string.append(c);
                        else{
                            isInsideFunction = false;
                            if (!sub_string.toString().isEmpty()) {
                                try {
                                    builder.append(functionDispatcher(sub_string.toString(), fs));
                                }catch(IllegalArgumentException e){
                                    //catch and throw, it means the function map didn't match the given arguments,
                                    //build the expression back and loop ahead to see if it has any overload,
                                    //then I'll repeat the same process again.
                                    //but what if, none of the map matched with the given arguments
                                    //the code will throw a NullPointerException as it should.
                                    //I can do a check before continuing ahead to see
                                    //if there's any remaining function name
                                    //if there are as there shouldn't be, that means the given arguments,
                                    //didn't match with any map of any overload,
                                    //and I should throw a custom exception there.
                                    //but I'm not going to bother doing another check at the end of the method,
                                    //just display an error and leave.
                                    builder.append(fs.getFunctionName())
                                            .append('(').append(sub_string.toString()).append(')');
                                }
                            }
                            sub_string.setLength(0);
                        }
                        continue;
                    }
                    sub_string.append(c);
                }
            }
            expression = builder.toString();
            builder.setLength(0);
        }
        return expression;

    }

    //it only deals with the properly formatted string, it does not resolve any errors in the string.
    private static String EvaluateOperation(String expression){
        if (operations.isEmpty())
            return expression;

        Stack<ComplexNumber> operand = new Stack<>(2);
        Stack<operationsInterface> ops = new Stack<>(1);

        boolean tokenCounter = false;
        StringBuilder complexString = new StringBuilder();

        @SuppressWarnings("UnusedAssignment")
        ComplexNumber complex_number = new ComplexNumber(0, 0);

        StringBuilder builder = new StringBuilder();

        outer_loop :
        for (int i = 0; i < expression.length(); i++){
            char c = expression.charAt(i);
            if (c == complex_token){
                if (!tokenCounter){
                    tokenCounter = true;
                }else{
                    tokenCounter = false;
                    complex_number = convertToComplexNumber(complexString.toString());
                    operand.push(complex_number);
                    complexString.setLength(0);
                }
            } else if (tokenCounter){
                complexString.append(c);
            } else {
                operationsInterface whichOperator = null;
                for (operationsInterface opInt : operations) {
                    if (opInt.getOperator() == c) {
                        if (ops.pullFirst() == null) {
                            ops.push(opInt);
                            continue outer_loop;
                        } else
                            whichOperator = opInt;
                        break;
                    }
                }
                //the second occurrence of an operator cannot be null, it can be null
                //only if it's the first occurrence of an operator but even then, the flow cannot reach this point
                //if it's the first occurrence of an operator.
                assert(whichOperator != null) : "this assertion must always return true.";
                int precedence1 = ops.pullFirst().getPrecedence();
                int precedence2 = whichOperator.getPrecedence();

                if (precedence1 >= precedence2 && (ops.pullFirst().getType() != TYPE_POST &&
                        whichOperator.getType() != TYPE_POST)){
                    ComplexNumber cn = dispatcher(operand.pull(0), operand.pull(1), ops.pullFirst());
                    operand.popAll();
                    ops.popAll();

                    operand.push(cn);
                    ops.push(whichOperator);
                }else{
                    ComplexNumber cn = operand.pull(1);
                    String str = convertComplexToString(operand.pullFirst());
                    builder.append(str);
                    builder.append(ops.pullFirst().getOperator());

                    operand.popAll();
                    ops.popAll();

                    operand.push(cn);
                    ops.push(whichOperator);
                }
            }
        }
        //the check is to return the first operand in case the expression
        //only contains one complexNumber(for whatever reason).
        if (operand.pull(1) == null && ops.pullFirst() == null)
            return convertComplexToString(operand.pullFirst());

        //when a proper string is given, this statement is guaranteed to run,
        //so no need to do any additional checks here.
        builder.append(convertComplexToString(dispatcher(operand.pull(0), operand.pull(1),
                ops.pullFirst())));

        operand.popAll();
        ops.popAll();

        expression = builder.toString();

        boolean containsOperations = checkOperatorPresence(expression);

        if (containsOperations)
            expression = EvaluateOperation(expression);

        return expression;
    }

    //general parser to resolve minor errors in a string.
    private static String GeneralParser(String expression){
        expression = expression.trim();
        expression = expression.replaceAll("\\s+", "");
        expression = expression.replaceAll("\\)\\(", "\\)*\\(");
        expression = expression.replaceAll("\\+\\+", "\\+");
        expression = expression.replaceAll("\\+-", "-");
        expression = expression.replaceAll("-\\+", "-");
        expression = expression.replaceAll("--", "\\+");
        expression = expression.replaceAll("i\\(", "i*\\(");

        //create name array
        StringBuilder nameBuilder = new StringBuilder();
        for (operationsInterface opInt : operations){
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
                if (a == '(') {
                    builder.append('*');
                }
            } else{
                builder.append(c);
            }
        }
        expression = builder.toString();
        return expression;
    }

    private static String BracketsResolver(String expression){
        if (!expression.contains("(") && !expression.contains(")"))
            return expression;

        StringBuilder builder = new StringBuilder();
        StringBuilder sub_string = new StringBuilder();
        int bracketCounter = 0;
        boolean isInsideBracket = false;

        for (int i = 0; i < expression.length(); i++){
            char c = expression.charAt(i);
            if (c == '('){
                bracketCounter++;
                if (!isInsideBracket)
                    isInsideBracket = true;
                else
                    sub_string.append(c);
            } else if (c == ')'){
                bracketCounter--;
                if (bracketCounter == 0) {
                    isInsideBracket = false;
                    if (sub_string.toString().contains("(") && sub_string.toString().contains(")")) {
                        String sub = BracketsResolver(sub_string.toString());
                        sub_string.setLength(0);
                        sub_string.append(sub);
                    }
                    //why evaluate for only the operation? since, the function evaluator already solves
                    //every single function, therefore no need to do any additional stuff here.
                    builder.append(EvaluateOperation(sub_string.toString()));
                    sub_string.setLength(0);
                } else
                    sub_string.append(c);
            } else{
                if (isInsideBracket){
                    sub_string.append(c);
                } else{
                    builder.append(c);
                }
            }
        }

        expression = builder.toString();
        return expression;
    }

    private static String converter(String expression){
        StringBuilder builder = new StringBuilder();
        StringBuilder currentStep = new StringBuilder();

        //convert each operator into a binary operator.
        operationsInterface last_ops = null;
        for (int i = 0; i < expression.length(); i++){
            char c = expression.charAt(i);

            char a = 0;
            if (i != expression.length() - 1)
                a = expression.charAt(i + 1);
            char p = 0;
            if (i != 0)
                p = expression.charAt(i - 1);

            operationsInterface opInt = getCharAsAnOperator(c);
            if (opInt != null){
                if (last_ops != null){
                    if (opInt.getOperator() == '+' || opInt.getOperator() == '-'){
                        switch(last_ops.getType()){
                            case TYPE_PRE :
                            case TYPE_CONSTANT :
                                builder.append(NEUTRAL_IDENTITY).append(neutral_token);
                        }
                    }else{
                        builder.append(NEUTRAL_IDENTITY).append(neutral_token);
                    }
                }else{
                    last_ops = opInt;
                }
                if ((p == '(' || p == 0) && (opInt.getOperator() != '+' && opInt.getOperator() != '-'))
                    builder.append(NEUTRAL_IDENTITY).append(neutral_token);
                builder.append(c);
                if (a == ')' || a == 0)
                    builder.append(NEUTRAL_IDENTITY).append(neutral_token);
            }else{
                last_ops = null;
                builder.append(c);
            }
        }
        expression = builder.toString();
        builder.setLength(0);

        outer_loop :
        for (int i = 0; i < expression.length(); i++){
            String c = String.valueOf(expression.charAt(i));
            if (c.matches("[1234567890.#iIeE]")){
                currentStep.append(c);
            }else{
                if (c.matches("[+-]")){
                    char p;
                    if (i != 0) {
                        p = expression.charAt(i - 1);
                        for (operationsInterface opInt : operations) {
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
                    }else if (currentStep.toString().contains("#")){
                        builder.append(convertToComplexString(currentStep.toString()));
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
            }else if (currentStep.toString().contains("#")){
                builder.append(convertToComplexString(currentStep.toString()));
            }else{
                builder.append(convertToComplexString(Double.parseDouble(currentStep.toString()), false));
            }
        }
        return builder.toString();
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

            for (operationsInterface opInt : operations){
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
        boolean r = (right + "").matches("[1234567890(]");

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
                        return opInt.getOperator() + "" + NEUTRAL_IDENTITY + "" + neutral_token;
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
                        return "" + opInt.getOperator();
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
                                            " less than or equal to the precedence of the left operator which has type " +
                                            "post");
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
                                throw new ExpressionException("the right operator is of type both and the left operator" +
                                        " is of type post, an operand in between is required regardless of the precedence");
                            case TYPE_POST :
                                if (precedence_current > precedence_right)
                                    throw new ExpressionException("the left and right operator, both is of type post, " +
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

    private static ComplexNumber dispatcher(ComplexNumber c1, ComplexNumber c2, operationsInterface whichOperation){
        ComplexNumber number = new ComplexNumber(0, 0);
        double
                c1r = c1.real,
                c1i = c1.iota,
                c2r = c2.real,
                c2i = c2.iota;
        switch(whichOperation.getType()) {
            case TYPE_BOTH:
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
    private static String functionDispatcher(String sub_expression, functionsInterface fs){
        int[] map = fs.getFunctionMap();
        Argument[] arguments;

        String[] items = sub_expression.split(",");

        boolean isArray = false;

        if (map[0] == ARGUMENT_ARRAY){
            if (map.length != 2)
                throw new ExpressionException("the functions that expects an array can only accept one type" +
                        " of argument, in this case, the map declaration can only have two types," +
                        " the first declaring it an array and the second being the type," +
                        " whose array is being expected");
            isArray = true;
        }

        arguments = new Argument[isArray ? items.length : map.length];

        if (!isArray) {
            if (map.length != items.length)
                throw new IllegalArgumentException("the given arguments does not match the method parameter map");
            for (int i = 0; i < map.length; i++) {
                int id = map[i];
                switch (id) {
                    case ARGUMENT_DOUBLE:
                        try {
                            double real = Double.parseDouble(Evaluate(items[i]));
                            arguments[i] = new Argument(real, null, null);
                        } catch (NumberFormatException e) {
                            throw new IllegalArgumentException("the given double is not parsable");
                        }
                        break;
                    case ARGUMENT_IOTA:
                        try {
                            String imag = Evaluate(items[i]);
                            double iota;
                            if (imag.contains("i"))
                                iota = Double.parseDouble(imag.replaceAll("i", ""));
                            else
                                throw new NumberFormatException();
                            arguments[i] = new Argument(iota, null, null);
                        } catch (NumberFormatException e) {
                            throw new IllegalArgumentException("the given double is not parsable");
                        }
                        break;
                    case ARGUMENT_COMPLEX:
                        try {
                            ComplexNumber cn = convertToComplexNumber(Evaluate(items[i]));
                            if (cn.real == 0 || cn.iota == 0)
                                throw new NumberFormatException("the given complex number is in fact a number");
                            arguments[i] = new Argument(0, convertToComplexNumber(Evaluate(items[i])), null);
                        } catch (Exception e) {
                            throw new IllegalArgumentException("the given complex number is not correct");
                        }
                        break;
                    case ARGUMENT_STRING:
                        arguments[i] = new Argument(0, null, items[i]);
                        break;
                    case ARGUMENT_ARRAY :
                        throw new ExpressionException("the array declaration must be the first element");
                    default:
                        break;
                }
            }
        }else {
            int type = map[1];
            for (int i = 0; i < items.length; i++){
                switch(type){
                    case ARGUMENT_DOUBLE:
                        try {
                            double real = Double.parseDouble(Evaluate(items[i]));
                            arguments[i] = new Argument(real, null, null);
                        } catch (NumberFormatException e) {
                            throw new IllegalArgumentException("the given double is not parsable");
                        }
                        break;
                    case ARGUMENT_IOTA:
                        try {
                            String imag = Evaluate(items[i]);
                            double iota;
                            if (imag.contains("i"))
                                iota = Double.parseDouble(imag.replaceAll("i", ""));
                            else
                                throw new NumberFormatException();
                            arguments[i] = new Argument(iota, null, null);
                        } catch (NumberFormatException e) {
                            throw new IllegalArgumentException("the given double is not parsable");
                        }
                        break;
                    case ARGUMENT_COMPLEX:
                        try {
                            ComplexNumber cn = convertToComplexNumber(Evaluate(items[i]));
                            if (cn.real == 0 || cn.iota == 0)
                                throw new NumberFormatException("the given complex number is in fact a number");
                            arguments[i] = new Argument(0, convertToComplexNumber(Evaluate(items[i])), null);
                        } catch (Exception e) {
                            throw new IllegalArgumentException("the given double is not parsable");
                        }
                        break;
                    case ARGUMENT_STRING:
                        arguments[i] = new Argument(0, null, items[i]);
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
                sub_expression = String.valueOf(fs.getDoubleResult());
                break;
            case RESULT_IOTA :
                sub_expression = fs.getDoubleResult() + "i";
                break;
            case RESULT_COMPLEX :
                sub_expression = "(" + convertComplexToString(fs.getComplexResult()).
                        replace(String.valueOf(complex_token), "") + ")";
                break;
        }

        return sub_expression;
    }

    private static operationsInterface getCharAsAnOperator(char c){
        operationsInterface opInt = null;
        for (operationsInterface ops : operations){
            if (ops.getOperator() == c)
                opInt = ops;
        }
        return opInt;
    }

    private static String convertFunctionString(String expression, String name){
        ArrayList<String> chunks_main = new ArrayList<>();
        ArrayList<String> chunks_sub = new ArrayList<>();
        StringBuilder builder_main = new StringBuilder();
        StringBuilder builder_sub = new StringBuilder();
        StringBuilder builder = new StringBuilder();
        boolean reverse = false;
        int bracketCounter = 0;

        for (int i = 0; i < expression.length(); i++){
            char c = expression.charAt(i);
            if (i == 0 && c == '(')
                reverse = true;

            if (c == '('){
                if (bracketCounter == 0 && i != 0) {
                    chunks_main.add(builder_main.toString());
                    builder_main.setLength(0);
                }
                builder_sub.append(c);
                bracketCounter++;
                continue;
            }
            if (c == ')'){
                bracketCounter--;
                builder_sub.append(c);
                if (bracketCounter == 0){
                    if (i != expression.length() - 1 &&
                            (expression.charAt(i + 1) + "").matches("[1234567890]"))
                        builder_main.append('*');
                    chunks_sub.add(builder_sub.toString());
                    builder_sub.setLength(0);
                }
                continue;
            }
            if (bracketCounter != 0){
                builder_sub.append(c);
            }else{
                builder_main.append(c);
            }
        }

        if (!builder_main.toString().isEmpty())
            chunks_main.add(builder_main.toString());

        int size1 = chunks_main.size();
        int size2 = chunks_sub.size();

        int n = 0;
        for (int i = 0; i < size1+size2; i++){
            if (i % 2 == 0){
                n = i / 2;
                if (!reverse) {
                    if (n < size1) {
                        String chunk_cutOff = chunks_main.get(n).replace(name, String.valueOf(function_token));
                        builder.append(chunk_cutOff);
                    }
                }else{
                    if (n < size2) {
                        String chunk_cutOff = chunks_sub.get(n);
                        if (chunk_cutOff.contains(name) && chunks_main.size() == 0){
                            chunk_cutOff = chunk_cutOff.replaceAll(name, String.valueOf(function_token));
                            builder.append(chunk_cutOff);
                        }else if (chunk_cutOff.contains(name) && n != 0 && !chunks_main.get(n-1).contains(name)) {
                            chunk_cutOff = chunk_cutOff.replaceAll(name, String.valueOf(function_token));
                            builder.append(chunk_cutOff);
                        }else
                            builder.append(chunks_sub.get(n));
                    }
                }
            }else{
                if (!reverse) {
                    if (n < size2) {
                        String chunk_cutOff = chunks_sub.get(n);
                        if (chunk_cutOff.contains(name) && !chunks_main.get(n).contains(name)) {
                            chunk_cutOff = chunk_cutOff.replaceAll(name, String.valueOf(function_token));
                            builder.append(chunk_cutOff);
                        }else
                            builder.append(chunks_sub.get(n));
                    }
                }else{
                    if (n < size1) {
                        String chunk_cutOff = chunks_main.get(n).replace(name, String.valueOf(function_token));
                        builder.append(chunk_cutOff);
                    }
                }
            }
        }
        expression = builder.toString();
        builder.setLength(0);
        for (int i = 0; i < expression.length(); i++){
            char c = expression.charAt(i);
            char p = 0;
            if (i != 0)
                p = expression.charAt(i - 1);

            if (c == function_token) {
                if ((p + "").matches("[1234567890i]"))
                    builder.append('*');
            }
            builder.append(c);
        }
        expression = builder.toString();
        return expression;
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

    private static boolean checkOperatorPresence(String expression){
        boolean isInsideToken = false;

        for (int i = 0; i < expression.length(); i++){
            char c = expression.charAt(i);
            if (c == complex_token){
                isInsideToken = !isInsideToken;
            }
            if (!Character.isDigit(c) && !isInsideToken) {
                for (operationsInterface opInt : operations) {
                    if (opInt.getOperator() == c)
                        return true;
                }
            }
        }
        return false;
    }

    private static ComplexNumber convertToComplexNumber(String complexString){
        ComplexNumber number = new ComplexNumber();
        StringBuilder currentStep = new StringBuilder();

        if (complexString.contains(String.valueOf(neutral_token))){
            complexString = complexString.replace(String.valueOf(neutral_token), "");
            number.is_neutral = true;
        }

        for (int i = 0; i < complexString.length(); i++){
            char c = complexString.charAt(i);
            if (c == '+' || c == '-') {
                if (i != 0) {
                    number.real = Double.parseDouble(!number.is_neutral ? currentStep.toString() :
                            currentStep.toString().replaceAll(String.valueOf(neutral_token), ""));
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

    private static String convertToComplexString(String neutral){
        return complex_token + (neutral + "+" + 0 + "i") + complex_token;
    }

    private static String convertComplexToString(ComplexNumber cn){
        return complex_token + "" + cn.real + (cn.is_neutral ? String.valueOf(neutral_token) : "") +
                (cn.iota >= 0 ? "+" : "") + cn.iota + "i" + complex_token;
    }

    static class ComplexNumber {
        public double real = 0, iota = 0;
        private boolean is_neutral = false;

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

    static class Stack<E> {

        private final Object[] items;
        private final int[] positionCounter;

        private final int size;

        @SuppressWarnings("Unchecked")
        public Stack(int size){
            items = new Object[size];//(E[]) Array.newInstance(clazz, size);

            positionCounter = new int[size];

            this.size = size;

            for(int i = 0; i < this.size; i++){
                positionCounter[i] = 0;
            }
        }

        public void push(E item){
            int freeIndex = -1;
            for (int i = 0; i < size; i++){
                if (positionCounter[i] == 0){
                    freeIndex = i;
                    positionCounter[i] = 1;
                    break;
                }
            }
            if (freeIndex == -1)
                throw new IllegalStateException("the Stack is full, cannot add any more value");
            items[freeIndex] = item;
        }

        public void push(E item, int index){
            items[index] = item;
            positionCounter[index] = 1;
        }

        @SuppressWarnings("unchecked")
        public E pull(int index){
            if (index > size - 1 || index < 0){
                throw new IllegalStateException("the provided index is beyond the Stack size");
            }
            return (E)items[index];
        }

        public E pullFirst(){
            return pull(0);
        }

        public void popFirst(){
            pop(0);
        }

        public void pop(int index){
            if (index > size - 1 || index < 0){
                throw new IllegalStateException("the provided index is beyond the Stack size");
            }
            items[index] = null;
            positionCounter[index] = 0;
            for (int i = index; i < size - 1; i++){
                items[i] = items[i+1];
                items[i+1] = null;
                positionCounter[i] = positionCounter[i+1];
                positionCounter[i+1] = 0;
            }
        }

        public int getSize(){
            return this.size;
        }

        public void popAll(){
            for (int i = 0; i < size; i++){
                items[i] = null;
                positionCounter[i] = 0;
            }
        }

        public boolean isEmpty(){
            for(int i = 0; i < size; i++){
                if (items[i] != null) {
                    return false;
                }
            }
            return true;
        }
    }
}
