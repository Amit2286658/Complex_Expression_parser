package com.expression_parser_v2_0.console.core;

public class Global {
    private static final Stack<operationsInterface> operations;
    private static final Stack<functionsInterface> functions;

    static {
        operations = new Stack<>();
        functions = new Stack<>();
    }

    @SuppressWarnings("unused")
    public static void registerOperation(operationsInterface opInt){
        registerOperation(opInt, false);
    }

    public static void registerOperation(operationsInterface opInt, boolean override){
        if (operations.isEmpty()) {
            operations.push(opInt);
            return;
        }
        operations.reset();
        while(operations.loop()){
            operationsInterface op = operations.get();
            if (op.getOperator() == opInt.getOperator()){
                if (!override){
                    throw new ExpressionException("An operation already exists, " +
                            "with the same operator : " + op.getOperator());
                }else{
                    operations.replace(opInt);
                    return;
                }
            }
        }
        operations.push(opInt);
    }

    @SuppressWarnings("unused")
    public static void registerFunction(functionsInterface fs){
        registerFunction(fs, false);
    }

    public static void registerFunction(functionsInterface fs, boolean override){
        if (functions.isEmpty()) {
            functions.push(fs);
            return;
        }
        functions.reset();
        outer_loop :
        while(functions.loop()){
            functionsInterface fsInt = functions.get();
            boolean match = false;

            function_in_list :
            for(String str : fsInt.getFunctionNames())
                for(String str1 : fs.getFunctionNames())
                    if (str1.equals(str)){
                        match = true;
                        break function_in_list;
                    }

            if (match) {
                int[] map1 = fsInt.getFunctionMap();
                int[] map2 = fs.getFunctionMap();
                int len1 = map1.length;
                int len2 = map2.length;
                //if the lengths don't match, just cut of the loop,
                //and push the function onto the stack, it will be treated as an overload.
                if (len1 == len2) {
                    //if the lengths match, but certain parameter type don't, then cut out of the loop,
                    //and push the function onto the stack, it will be treated as an overload.
                    for (int i = 0; i < len1; i++) {
                        if (map1[i] != map2[i])
                            break outer_loop;
                    }
                    if (!override)
                        throw new ExpressionException("A function with the same name : " +
                                fs.getFunctionNames()[0] + ", and same map already exists");
                    else
                        functions.replace(fs);
                    return;
                }
            }
        }
        functions.push(fs);
    }

    protected static boolean isOperationsEmpty(){
        return operations.isEmpty();
    }

    protected static boolean isFunctionsEmpty(){
        return functions.isEmpty();
    }

    protected static Stack<operationsInterface> getOperations(){
        return operations;
    }

    protected static Stack<functionsInterface> getFunctions(){
        return functions;
    }
}
