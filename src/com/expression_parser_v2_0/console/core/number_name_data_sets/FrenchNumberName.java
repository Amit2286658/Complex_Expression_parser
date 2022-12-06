package com.expression_parser_v2_0.console.core.number_name_data_sets;

import java.util.HashMap;

import com.expression_parser_v2_0.console.core.NumberNameDataInterface;
import com.expression_parser_v2_0.console.core.Stack;

public class FrenchNumberName implements NumberNameDataInterface{
    HashMap<String, Integer> add_map = new HashMap<>();
    HashMap<String, Long> multiply_map = new HashMap<>();

    public FrenchNumberName(){
        add_map.put(_fZero, 0);

        add_map.put(_f1.trim(), 1);
        add_map.put(_f2.trim(), 2);
        add_map.put(_f3.trim(), 3);
        add_map.put(_f4.trim(), 4);
        add_map.put(_f5.trim(), 5);
        add_map.put(_f6.trim(), 6);
        add_map.put(_f7.trim(), 7);
        add_map.put(_f8.trim(), 8);
        add_map.put(_f9.trim(), 9);

        add_map.put(_f11.trim(), 11);
        add_map.put(_f12.trim(), 12);
        add_map.put(_f13.trim(), 13);
        add_map.put(_f14.trim(), 14);
        add_map.put(_f15.trim(), 15);
        add_map.put(_f16.trim(), 16);

        add_map.put(_f10.trim(), 10);
        add_map.put(_f20.trim(), 20);
        add_map.put(_f30.trim(), 30);
        add_map.put(_f40.trim(), 40);
        add_map.put(_f50.trim(), 50);
        add_map.put(_f60.trim(), 60);
        add_map.put(_f70.trim(), 70);
        add_map.put(_f80.trim(), 80);
        add_map.put(_f90.trim(), 90);

        multiply_map.put(_fh.trim(), 100L);
        multiply_map.put(_fh.trim()+"s", 100L);
        multiply_map.put(_fth.trim(), 1000L);
        multiply_map.put(_fm.trim(), 1000000L);
        multiply_map.put(_fb.trim(), 1000000000L);
        multiply_map.put(_fm.trim()+"s", 1000000L);
        multiply_map.put(_fb.trim()+"s", 1000000000L);
    }

    String 
        _fh = " cent",
        _fth = " mille",
        _fm = " million",
        _fb = " milliard",
        h = "-",
        _f1_compliment = " et",
        _fZero = "zéro";
             
    String
        _f0 = "",
        _f1 = " un",
        _f2 = " deux",
        _f3 = " trois",
        _f4 = " quatre",
        _f5 = " cinq",
        _f6 = " six",
        _f7 = " sept",
        _f8 = " huit",
        _f9 = " neuf";

    String 
        _f11 = " onze",
        _f12 = " douze",
        _f13 = " treize",
        _f14 = " quatorze",
        _f15 = " quinze",
        _f16 = " seize";

    String
        _f00 = "",
        _f10 = " dix",
        _f20 = " vingt",
        _f30 = " trente",
        _f40 = " quarante",
        _f50 = " cinquante",
        _f60 = " soixante",
        _f70 = " soixante-dix",
        _f80 = " quatre-vingt",
        _f90 = " quatre-vingt-dix";

    String 
        _f000 = "",
        _f100 = _fh,
        _f200 = _f2 + _fh,
        _f300 = _f3 + _fh,
        _f400 = _f4 + _fh,
        _f500 = _f5 + _fh,
        _f600 = _f6 + _fh,
        _f700 = _f7 + _fh,
        _f800 = _f8 + _fh,
        _f900 = _f9 + _fh;

    String[][][] frenchNumberNames = new String[][][]{
        {
            {_f0,_f1_compliment+_f1,h+_f2,h+_f3,h+_f4,h+_f5,
            h+_f6,h+_f7,h+_f8,h+_f9},
            {_f00,_f10,_f20,_f30,_f40,_f50,_f60,_f70,_f80,_f90},
            {_f000,_f100,_f200,_f300,_f400,_f500,_f600,_f700,_f800,_f900}
        },
        {
            {_fth,_f1_compliment+_f1+_fth,h+_f2+_fth,h+_f3+_fth,h+_f4+_fth,h+_f5+_fth,
                h+_f6+_fth,h+_f7+_fth,h+_f8+_fth,h+_f9+_fth},
            {_f00,_f10,_f20,_f30,_f40,_f50,_f60,_f70,_f80,_f90},
            {_f000,_f100,_f200,_f300,_f400,_f500,_f600,_f700,_f800,_f900}
        },
        {
            {_fm,_f1_compliment+_f1+_fm,h+_f2+_fm,h+_f3+_fm,h+_f4+_fm,h+_f5+_fm,
                h+_f6+_fm,h+_f7+_fm,h+_f8+_fm,h+_f9+_fm},
            {_f00,_f10,_f20,_f30,_f40,_f50,_f60,_f70,_f80,_f90},
            {_f000,_f100,_f200,_f300,_f400,_f500,_f600,_f700,_f800,_f900}
        },
        {
            {_fb,_f1_compliment+_f1+_fb,h+_f2+_fb,h+_f3+_fb,h+_f4+_fb,h+_f5+_fb,
                h+_f6+_fb,h+_f7+_fb,h+_f8+_fb,h+_f9+_fb},
            {_f00,_f10,_f20,_f30,_f40,_f50,_f60,_f70,_f80,_f90},
            {_f000,_f100,_f200,_f300,_f400,_f500,_f600,_f700,_f800,_f900}
        }
    };

    @Override
    public int stepChange(int step) {
        return 3;
    }

    @Override
    public String getZero() {
        return _fZero;
    }

    @Override
    public String getPointName(){
        return "point";
    }

    @Override
    public String getName(int step, int position, int current_number,
        int[] previous_numbers, int[] next_numbers, Stack<String> names) {
        String mod_name = null;
        if (position == 1){
            int previous_number = previous_numbers[0];
            if (current_number == 1){
                switch(previous_number){
                    case 0 -> {names.pop(); mod_name = _f10;}
                    case 1 -> {names.pop(); mod_name = _f11;}
                    case 2 -> {names.pop(); mod_name = _f12;}
                    case 3 -> {names.pop(); mod_name = _f13;}
                    case 4 -> {names.pop(); mod_name = _f14;}
                    case 5 -> {names.pop(); mod_name = _f15;}
                    case 6 -> {names.pop(); mod_name = _f16;}
                }
            } else if (current_number == 7){
                switch(previous_number){
                    case 1 -> {names.pop(); mod_name = _f60+h+_f11;}
                    case 2 -> {names.pop(); mod_name = _f60+h+_f12;}
                    case 3 -> {names.pop(); mod_name = _f60+h+_f13;}
                    case 4 -> {names.pop(); mod_name = _f60+h+_f14;}
                    case 5 -> {names.pop(); mod_name = _f60+h+_f15;}
                    case 6 -> {names.pop(); mod_name = _f60+h+_f16;}
                }
            } else if (current_number == 8){
                if (previous_number == 1) {
                    names.pop();
                    mod_name = _f80+h+_f1;
                }
            } else if (current_number == 9){
                switch(previous_number){
                    case 1 -> {names.pop(); mod_name = _f80+h+_f11;}
                    case 2 -> {names.pop(); mod_name = _f80+h+_f12;}
                    case 3 -> {names.pop(); mod_name = _f80+h+_f13;}
                    case 4 -> {names.pop(); mod_name = _f80+h+_f14;}
                    case 5 -> {names.pop(); mod_name = _f80+h+_f15;}
                    case 6 -> {names.pop(); mod_name = _f80+h+_f16;}
                }
            }
        }
        
        if (position == 0){
            String str = frenchNumberNames[step][position][current_number];
            if (current_number > 1){
                str = str.replace(_fm, _fm+"s");
                str = str.replace(_fh, _fh+"s");
                str = str.replace(_fb, _fb+"s");
            }
            if ((next_numbers[0] == 0 || next_numbers[0] == -1) &&
                (next_numbers[1] == 0 || next_numbers[1] == -1)){
                if (current_number == 1)
                    str = str.replace(_f1_compliment + _f1, _f1);
                if (str.length() > 0 && current_number > 1)
                    str = str.substring(1);

                return str;
            }
            return str;
        }

        if (mod_name != null){
            return switch(step){
                case 1 -> mod_name + _fh+"s";
                case 2 -> mod_name + _fm+"s";
                case 3 -> mod_name + _fb+"s";
                default -> mod_name;
            };
        }
        return frenchNumberNames[step][position][current_number];
    }

    @Override
    public String postProcessing(String name) {
        StringBuilder nameBuilder = new StringBuilder();
        int indexToIgnore = -1;
        for(int i = 0; i < name.length(); i++){
            if (i == indexToIgnore){
                indexToIgnore = -1;
                continue;
            }
            char c = name.charAt(i);
            if (c == h.charAt(0)){
                if (name.charAt(i + 1) == ' ')
                    indexToIgnore = i + 1;
            } else if (c == ' '){
                if (name.charAt(i + 1) == h.charAt(0))
                    continue;
            }
            nameBuilder.append(c);
        }

        return nameBuilder.toString();
    }

    @Override
    public String getSimpleName(int current_number) {
        return switch (current_number) {
            case 0 -> _fZero;
            case 1 -> _f1;
            case 2 -> _f2;
            case 3 -> _f3;
            case 4 -> _f4;
            case 5 -> _f5;
            case 6 -> _f6;
            case 7 -> _f7;
            case 8 -> _f8;
            case 9 -> _f9;
            default -> throw new IndexOutOfBoundsException("the flow isn't supposed to reach here");
        };
    }

    @Override
    public int getAdditiveNumber(String current_term) {
        int additiveUnion = 0;
        //finish with the 80th number first
        if (current_term.contains(_f80.trim())){
            additiveUnion += 80;
            current_term = current_term.replace(_f80.trim(), "");
        }

        String[] terms = current_term.split(h);

        if (terms.length == 1){
            if (add_map.containsKey(current_term))
                return add_map.get(current_term);
        }else {
            int counter = 0;
            while(counter < terms.length){
                if (counter == 0 && additiveUnion == 80){
                    counter++;
                    continue;
                }
                if (add_map.containsKey(terms[counter])){
                    additiveUnion += add_map.get(terms[counter]);
                }
                counter++;
            }
            return additiveUnion;
        }

        return -1;
    }

    @Override
    public long getMultiplicativeNumber(String current_term) {
        if (multiply_map.containsKey(current_term))
            return multiply_map.get(current_term);
        
        return -1;
    }
}
