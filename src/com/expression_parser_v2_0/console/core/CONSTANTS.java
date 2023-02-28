package com.expression_parser_v2_0.console.core;

public class CONSTANTS {
    public static final int
            IOTA_FIRST = 1,
            IOTA_SECOND = 2,
            IOTA_BOTH = 3,
            IOTA_NONE = 0,
            IOTA_TRUE = 1,
            IOTA_FALSE = 0,
            //type constants
            REAL = 1,
            IOTA = 2,
            COMPLEX = 3,
            STRING = 4,
            SET = 5,
            ARRAY = 6,
            VARIABLE = 7,
            CONSTANT = 8,
            TERM = 9,
            //operators type
            TYPE_PRE = 0,
            TYPE_POST = 1,
            TYPE_BOTH = 2,
            TYPE_CONSTANT = 3,
            //for whatever purpose
            ANGLE_MODE_RADIAN = 0,
            ANGLE_MODE_DEGREE = 1,
            //for operations precedence
            PRECEDENCE_LEAST = 250,
            PRECEDENCE_MEDIUM = 500,
            PRECEDENCE_MAX = 750;

    static final int
            PRECEDENCE_FUNCTION = 1000;

    public static final int 
            ENGLISH_NUMBER_SYSTEM = 1,
            INDIAN_NUMBER_SYSTEM = 2,
            FRENCH_NUMBER_SYSTEM = 3,
            ENGLISH_NUMBER_SYSTEM_PLACE_DIFFERENCE = 3,
            INDIAN_NUMBER_SYSTEM_PLACE_DIFFERENCE = 2;
}
