package functions;

import com.expression_parser_v2_0.console.Functions_Implementation;
import com.expression_parser_v2_0.console.Main;

import static com.expression_parser_v2_0.console.Main.*;

public class Sine extends Functions_Implementation {

    int resultFlag;
    double real;
    double iota;
    ComplexNumber cn;

    public Sine() {
        super();
    }

    @Override
    public String[] getFunctionNames() {
        return new String[]{"Sin", "sin", "SIN"};
    }

    @Override
    public int[] getFunctionMap() {
        return new int[]{ARGUMENT_REAL};
    }

    @Override
    public int getId() {
        return 1;
    }

    @Override
    public int getResultFlag() {
        return resultFlag;
    }

    @Override
    public double getReal() {
        return real;
    }

    @Override
    public double getIota() {
        return iota;
    }

    @Override
    public ComplexNumber getComplex() {
        return cn;
    }

    @Override
    public void function(Argument[] arguments, int id) {
        if (id == 1){
            real = Math.sin(Main.getAngleMode() == ANGLE_MODE_RADIAN ? arguments[0].getRealArgument() :
                    Math.toRadians(arguments[0].getRealArgument()));
            resultFlag = RESULT_REAL;
            return;
        }
        super.function(arguments, id);
    }
}