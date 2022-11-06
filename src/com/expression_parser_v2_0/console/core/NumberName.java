package com.expression_parser_v2_0.console.core;

import static com.expression_parser_v2_0.console.core.constants.*;

import com.expression_parser_v2_0.console.core.NumberNameDataSets.EnglishNumberName;
import com.expression_parser_v2_0.console.core.NumberNameDataSets.FrenchNumberName;
import com.expression_parser_v2_0.console.core.NumberNameDataSets.IndianNumberName;

public class NumberName {
    private static NumberNameDataInterface currentNumberNameInterface = new EnglishNumberName();

    IndianNumberName indianInterface = new IndianNumberName();
    FrenchNumberName frenchInterface = new FrenchNumberName();
    EnglishNumberName englishInterface = new EnglishNumberName();

    //standard point notation meaning after point,
    //each number will be spelt exactly as they are irrespective of their place.
    private boolean standardPointNotation = true;

    public void setNumberSystem(int type){
        currentNumberNameInterface = switch(type){
            case INDIAN_NUMBER_SYSTEM -> indianInterface;
            case FRENCH_NUMBER_SYSTEM -> frenchInterface;
            default -> englishInterface;
        };
    }

    public void setStandardPointNotation(boolean value){
        standardPointNotation = value;
    }

    public String convertToNumberName(String number){
        //the grouping of numbers,
        //such as 1 000 000 000 where each group contains 3 digits,
        //or 1 00 00 00 000 for indian number system,
        //where each group contains 2 digits except the first group which has 3.
        //may differ from one number system to another.
        int place_difference = currentNumberNameInterface.getInitialGroupDifference();

        String[] split = number.split("\\.");
        String num1, num2;
        num1 = split[0];
        num2 = split.length > 1 ? split[1] : null;

        int place_counter = 0;
        int step = 0;
        Stack<String> numberNameBuilder = new Stack<>();
        StringBuilder builder = new StringBuilder();
        builder.append(num1);
        num1 = (builder.reverse()).toString();
        builder.setLength(0);

        int counter = -1;

        boolean isZero = true;

        for(int i = 0; i < num1.length(); i++){
            int c = Integer.parseInt(num1.charAt(i) + "");

            counter++;
            place_counter = counter % place_difference;
            if (place_counter == 0){
                step++;

                //ignore the first step as it's the beginning anyways.
                if (step != 1)
                    if (isZero)
                        //discard the full group
                        for(int k = 0; k < place_difference; k++){
                            numberNameBuilder.pop();
                        }

                isZero = true;

                //step listener
                counter = 0;
                place_difference = currentNumberNameInterface.stepChange(step - 1);
            }

            if (isZero)
                isZero = c == 0;

            int[] previous_numbers = new int[place_counter];
            int[] next_numbers = new int[place_difference - (place_counter + 1)];

            for(int k = 0; k < previous_numbers.length; k++){
                int prev_count = i - (1 + k);
                previous_numbers[k] = prev_count >= 0 ? Integer.parseInt(
                    num1.charAt(prev_count) + "") : -1;
            }

            for(int k = 0; k < next_numbers.length; k++){
                int next_count = i + (1 + k);
                next_numbers[k] = next_count < num1.length() ? Integer.parseInt(
                    num1.charAt(next_count) + "") : -1;
            }

            String name = currentNumberNameInterface.getName(step-1, place_counter, c,
                previous_numbers.length == 0 ? null : previous_numbers,
                next_numbers.length == 0 ? null : next_numbers, numberNameBuilder);

            numberNameBuilder.push(name);
        }

        if (isZero)
            for(int i = 0; i <= place_counter; i++)
                numberNameBuilder.pop();

        if (numberNameBuilder.isEmpty()){
            numberNameBuilder.push(currentNumberNameInterface.getZero());
        }

        while(numberNameBuilder.hasNext()){
            String item = numberNameBuilder.pop();
            builder.append(item);
        }

        String str = builder.toString();

        if (num2 != null){
            if (standardPointNotation){
                builder.setLength(0);
                builder.append(num2);
                num2 = builder.reverse().toString();
                builder.setLength(0);

                //decimal conversion here
                for(int i = 0; i < num2.length(); i++){
                    int c = Integer.parseInt(num2.charAt(i) + "");
                    
                    String name = currentNumberNameInterface.getSimpleName(c);
                    numberNameBuilder.push(name);
                }

                while(numberNameBuilder.hasNext()){
                    builder.append(numberNameBuilder.pop());
                }

                str += " " + currentNumberNameInterface.getPointName();
                str += builder.toString();
            }else {
                str += " " + currentNumberNameInterface.getPointName() + " ";
                str += convertToNumberName(num2);
            }
        }

        //post conversion processing
        str = str.trim();
        str = currentNumberNameInterface.postProcessing(str);
        return str;
    }

    //the string should be properly formatted with a space in between each term.
    //conversion from string to a number is inclusive of all types of number systems.
    //update 06-11-2022 from string to number is no longer inclusive of all number systems
    //all thanks to french number system forcing me to use a different approach.
    public double convertNameToNumber(String name){
        String[] items = name.split(" ");
        Stack<Double> accumulatorStack = new Stack<>();
        double accumulator = 0;

        StringBuilder pointBuilder = new StringBuilder();
        boolean beyondPoint = false;
        for(int i = 0; i < items.length; i++){
            if (items[i].equals(currentNumberNameInterface.getPointName())){
                accumulatorStack.push(accumulator);
                accumulator = 0;
                beyondPoint = true;
                continue;
            }

            if (!beyondPoint){
                if (currentNumberNameInterface.getAdditiveNumber(items[i]) != -1){
                    double value = currentNumberNameInterface.getAdditiveNumber(items[i]);
                    accumulator += value;
                }else {
                    if (currentNumberNameInterface.getMultiplicativeNumber(items[i]) != -1){
                        double value = currentNumberNameInterface.getMultiplicativeNumber(items[i]);
                        if (accumulator != 0){
                            accumulator *= value;
                            accumulatorStack.push(accumulator);
                            accumulator = 0;
                        } else {
                            //accumulator is already zero,
                            //which means there's already something in the stack,
                            //that needs to be multiplied with the current value.
                            //get the most current one.
                            double newValue = accumulatorStack.pop();
                            accumulatorStack.push(value * newValue);
                        }
                    }else {
                        throw new IndexOutOfBoundsException("given term : " + items[i] + 
                        " is not present in the data set");
                    }
                }
            }else {
                if (standardPointNotation){
                    if (currentNumberNameInterface.getAdditiveNumber(items[i]) != -1){
                        //to get rid of the unnecessary leading point and zero
                        double value = currentNumberNameInterface.getAdditiveNumber(items[i]);
                        int value1 = (int)value;
                        pointBuilder.append(value1);
                    }else 
                        throw new IndexOutOfBoundsException("given term : " + items[i] +
                    " is not present in the data set");
                }else {
                    pointBuilder.setLength(0);
                    for(int k = i; k < items.length; k++){
                        pointBuilder.append(items[k]).append(" ");
                    }
                    double d = convertNameToNumber(pointBuilder.toString());
                    int d1 = (int)d;
                    pointBuilder.setLength(0);
                    pointBuilder.append(d1);
                    break;
                }
            }
        }
        
        while(accumulatorStack.hasNext()){
            accumulator += accumulatorStack.pop();
        }

        double d1 = Double.parseDouble(pointBuilder.toString().length() == 0 ? "0"
                                             : pointBuilder.toString());
        d1 = d1/Math.pow(10, pointBuilder.length());
        accumulator += d1;
        
        return accumulator;
    }
}
