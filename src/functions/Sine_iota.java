package functions;

import com.expression_parser_v2_0.console.Functions_Implementation;
import com.expression_parser_v2_0.console.Main;

import static com.expression_parser_v2_0.console.Main.*;

public class Sine_iota extends Functions_Implementation {

    int resultFlag;
    double real;
    double iota;
    Main.ComplexNumber cn;

    public Sine_iota() {
        super();
    }

    @Override
    public String[] getFunctionNames() {
        return new String[]{"Sin", "sin", "SIN"};
    }

    @Override
    public int[] getFunctionMap() {
        return new int[]{ARGUMENT_IOTA};
    }

    @Override
    public int getId() {
        return 2;
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
    public Main.ComplexNumber getComplex() {
        return cn;
    }

    @Override
    public void function(Main.Argument[] arguments, int id) {
        if (id == 2){
            real = Math.sin(Main.getAngleMode() == ANGLE_MODE_RADIAN ? arguments[0].getRealArgument() :
                    Math.toRadians(arguments[0].getRealArgument()));
            resultFlag = RESULT_REAL;
            return;
        }
        super.function(arguments, id);
    }
}
