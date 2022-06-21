package com.expression_parser_v2_0.console;

import static com.expression_parser_v2_0.console.Main.*;

public enum Functional_Library implements functionsInterface{
    SIN1("sin", new int[]{ARGUMENT_REAL}, 1),
    SIN2("sin", new int[]{ARGUMENT_COMPLEX}, 2),
    SIN3("sin", new int[]{ARGUMENT_IOTA}, 3),
    COS1("cos", new int[]{ARGUMENT_REAL}, 1),
    COS2("cos", new int[]{ARGUMENT_COMPLEX}, 2),
    MIN1("min", new int[]{ARGUMENT_ARRAY, ARGUMENT_REAL}, 1),
    MIN2("min", new int[]{ARGUMENT_ARRAY, ARGUMENT_IOTA}, 2),
    MIN3("min", new int[]{ARGUMENT_ARRAY, ARGUMENT_COMPLEX}, 3);

    String name;
    int[] functionMap;
    int resultFlag;
    int id;
    double doubleResult;
    ComplexNumber complexResult = new ComplexNumber();

    Functional_Library(String name, int[] map, int id){
        this.name = name;
        this.functionMap = map;
        this.id = id;

        registerFunction(this);
    }

    @Override
    public String getFunctionName() {
        return name;
    }

    @Override
    public int[] getFunctionMap() {
        return functionMap;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public int getResultFlag() {
        return resultFlag;
    }

    @Override
    public double getDoubleResult() {
        return doubleResult;
    }

    @Override
    public ComplexNumber getComplexResult() {
        return complexResult;
    }

    @Override
    public void function(Argument[] arguments, int id) {
        if (name.equals("sin")){
            switch (id){
                case 1 :
                    System.out.println("case 1 is called");
                    doubleResult = Math.sin(Main.getAngleMode() == ANGLE_MODE_DEGREE ?
                            Math.toRadians(arguments[0].getDoubleArgument()) :
                            arguments[0].getDoubleArgument());
                    resultFlag = RESULT_REAL;
                    break;
                case 2 :
                    System.out.println("case 2 is called");
                    break;
                case 3 :
                    System.out.println("case 3 is called");
                    break;
            }
        }else if (name.equals("min")){
            switch(id){
                case 1 :
                    double min = Double.MAX_VALUE;
                    for (Argument value : arguments) {
                        if (value.getDoubleArgument() < min)
                            min = value.getDoubleArgument();
                    }
                    resultFlag = RESULT_REAL;
                    doubleResult = min;
                    break;
                case 2 :
                    min = Double.MAX_VALUE;
                    for (Argument argument : arguments) {
                        if (argument.getDoubleArgument() < min)
                            min = argument.getDoubleArgument();
                    }
                    resultFlag = RESULT_IOTA;
                    doubleResult = min;
                    break;
                case 3 :
                    int index = 0;
                    min = Double.MAX_VALUE;
                    for(int i = 0; i < arguments.length; i++){
                        Argument argument = arguments[i];
                        if (argument.getComplexArgument().real + argument.getComplexArgument().iota < min){
                            min = argument.getComplexArgument().real + argument.getComplexArgument().iota;
                            index = i;
                        }
                    }
                    resultFlag = RESULT_COMPLEX;
                    complexResult = arguments[index].getComplexArgument();
                    break;
            }
        }else if (name.equals("test")){
            System.out.println("test is called");
        }
    }
}
