package com.expression_parser_v2_0.console.core.number_name_data_sets;

import java.util.HashMap;

import com.expression_parser_v2_0.console.core.NumberNameDataInterface;
import com.expression_parser_v2_0.console.core.Stack;

public class EnglishNumberName implements NumberNameDataInterface {

    HashMap<String, Integer> add_map = new HashMap<>();
    HashMap<String, Long> multiply_map = new HashMap<>();

    public EnglishNumberName(){
        add_map.put(zero, 0);

        add_map.put(_1.trim(), 1);
        add_map.put(_2.trim(), 2);
        add_map.put(_3.trim(), 3);
        add_map.put(_4.trim(), 4);
        add_map.put(_5.trim(), 5);
        add_map.put(_6.trim(), 6);
        add_map.put(_7.trim(), 7);
        add_map.put(_8.trim(), 8);
        add_map.put(_9.trim(), 9);

        add_map.put(_11.trim(), 11);
        add_map.put(_12.trim(), 12);
        add_map.put(_13.trim(), 13);
        add_map.put(_14.trim(), 14);
        add_map.put(_15.trim(), 15);
        add_map.put(_16.trim(), 16);
        add_map.put(_17.trim(), 17);
        add_map.put(_18.trim(), 18);
        add_map.put(_19.trim(), 19);

        add_map.put(_10.trim(), 10);
        add_map.put(_20.trim(), 20);
        add_map.put(_30.trim(), 30);
        add_map.put(_40.trim(), 40);
        add_map.put(_50.trim(), 50);
        add_map.put(_60.trim(), 60);
        add_map.put(_70.trim(), 70);
        add_map.put(_80.trim(), 80);
        add_map.put(_90.trim(), 90);

        multiply_map.put(h.trim(), 100L);
        multiply_map.put(th.trim(), 1000L);
        multiply_map.put(m.trim(), 1000000L);
        multiply_map.put(b.trim(), 1000000000L);
    }

    //inclusive of spaces
    String point = " point";
    String h = " hundred";
    String th = " thousand";
    String m = " million";
    String b = " billion";
    String zero = "zero";

    String
        _0 = "",
        _1 = " one",
        _2 = " two",
        _3 = " three",
        _4 = " four",
        _5 = " five",
        _6 = " six",
        _7 = " seven",
        _8 = " eight",
        _9 = " nine";
    String
        _00 = "",
        _10 = " ten",
        _20 = " twenty",
        _30 = " thirty",
        _40 = " forty",
        _50 = " fifty",
        _60 = " sixty",
        _70 = " seventy",
        _80 = " eighty",
        _90 = " ninety";
    String
        _11 = " eleven",
        _12 = " twelve",
        _13 = " thirteen",
        _14 = " fourteen",
        _15 = " fifteen",
        _16 = " sixteen",
        _17 = " seventeen",
        _18 = " eighteen",
        _19 = " nineteen";
    String
        _000 = "",
        _100 = _1 + h,
        _200 = _2 + h,
        _300 = _3 + h,
        _400 = _4 + h,
        _500 = _5 + h,
        _600 = _6 + h,
        _700 = _7 + h,
        _800 = _8 + h,
        _900 = _9 + h;

    String[][][] numberNames = new String[][][]{
        {
            {_0,_1,_2,_3,_4,_5,_6,_7,_8,_9},
            {_00,_10,_20,_30,_40,_50,_60,_70,_80,_90},
            {_000,_100,_200,_300,_400,_500,_600,_700,_800,_900}
        },
        {
            {th,_1+th,_2+th,_3+th,_4+th,_5+th,
            _6+th,_7+th,_8+th,_9+th},
            {_00,_10,_20,_30,_40,_50,_60,_70,_80,_90},
            {_000,_100,_200,_300,_400,_500,_600,_700,_800,_900}
        },
        {
            {m,_1+m,_2+m,_3+m,_4+m,_5+m,
            _6+m,_7+m,_8+m,_9+m},
            {_00, _10,_20,_30,_40,_50,_60,_70,_80,_90},
            {_000,_100,_200,_300,_400,_500,_600,_700,_800,_900}
        },
        {
            {b,_1+b,_2+b,_3+b,_4+b,_5+b,
            _6+b,_7+b,_8+b,_9+b},
            {_00,_10,_20,_30,_40,_50,_60,_70,_80,_90},
            {_000,_100,_200,_300,_400,_500,_600,_700,_800,_900}
        }
    };

    @Override
    public int stepChange(int step) {
        return 3;
    }

    @Override
    public String getZero() {
        return zero.trim();
    }

    @Override
    public String getPointName(){
        return "point";
    }

    @Override
    public String getName(int step, int position, int current_number,
        int[] previous_numbers, int[] next_numbers, Stack<String> names) {
        if (step == 0 && position == 1 && current_number == 1){
            int previous_number = previous_numbers[0];
            //pop the first number anyways
            names.pop();
            return switch(previous_number){
                case 0 -> _10;
                case 1 -> _11;
                case 2 -> _12;
                case 3 -> _13;
                case 4 -> _14;
                case 5 -> _15;
                case 6 -> _16;
                case 7 -> _17;
                case 8 -> _18;
                case 9 -> _19;
                default -> 
                    throw new IndexOutOfBoundsException("the flow should never reach this point");
            };
        }
        return numberNames[step][position][current_number];
    }

    @Override
    public String postProcessing(String name) {
        return name;
    }

    @Override
    public String getSimpleName(int current_number) {
        return switch (current_number) {
            case 0 -> zero;
            case 1 -> _1;
            case 2 -> _2;
            case 3 -> _3;
            case 4 -> _4;
            case 5 -> _5;
            case 6 -> _6;
            case 7 -> _7;
            case 8 -> _8;
            case 9 -> _9;
            default -> throw new IndexOutOfBoundsException("the flow isn't supposed to reach here");
        };
    }

    @Override
    public int getAdditiveNumber(String current_term) {
        if (add_map.containsKey(current_term))
            return add_map.get(current_term);
        
        return -1;
    }

    @Override
    public long getMultiplicativeNumber(String current_term) {
        if (multiply_map.containsKey(current_term))
            return multiply_map.get(current_term);
        
        return -1;
    }
}
