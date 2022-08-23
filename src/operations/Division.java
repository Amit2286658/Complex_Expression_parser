package operations;

import com.expression_parser_v2_0.console.Operations_Implementation;

import static com.expression_parser_v2_0.console.Main.*;

public class Division extends Operations_Implementation {

    int resultFlag;
    ComplexNumber complexResult;
    double realResult;
    double iotaResult;


    public Division() {
        super();
    }

    @Override
    public String[] getOperationNames() {
        return new String[]{"by", "By", "By", "÷"};
    }

    @Override
    public char getOperator() {
        return '/';
    }

    @Override
    public int getPrecedence() {
        return PRECEDENCE_MEDIUM;
    }

    @Override
    public int getType() {
        return TYPE_BOTH;
    }

    @Override
    public int getResultFlag() {
        return resultFlag;
    }

    @Override
    public double getReal() {
        return realResult;
    }

    @Override
    public double getIota() {
        return iotaResult;
    }

    @Override
    public ComplexNumber getComplex() {
        return complexResult;
    }

    @Override
    public void function(double d1, double d2, int iotaStatus) {
        switch(iotaStatus){
            case IOTA_BOTH :
            case IOTA_NONE :
                realResult = d1 / d2;
                resultFlag = RESULT_REAL;
                break;
            case IOTA_FIRST :
                iotaResult = d1 / d2;
                resultFlag = RESULT_IOTA;
                break;
            case IOTA_SECOND :
                iotaResult = (d1 / d2) * -1;
                resultFlag = RESULT_IOTA;
                break;
        }
    }

    @Override
    public void function(ComplexNumber c1, double d2, int iotaStatus) {
        switch(iotaStatus){
            case IOTA_TRUE :
                double c1r = c1.real, c1i = c1.iota;
                c1.real = c1i / d2;
                c1.iota = c1r / d2 * -1;
                complexResult = c1;
                resultFlag = RESULT_COMPLEX;
                break;
            case IOTA_FALSE :
                c1.real /= d2;
                c1.iota /= d2;
                complexResult = c1;
                resultFlag = RESULT_COMPLEX;
                break;
        }
    }

    @Override
    public void function(double d1, ComplexNumber c2, int iotaStatus) {
        double c2r = c2.real, c2i = c2.iota;
        switch(iotaStatus){
            case IOTA_TRUE :
                c2.real = d1 / c2i;
                c2.iota = d1 / c2r;
                complexResult = c2;
                resultFlag = RESULT_COMPLEX;
                break;
            case IOTA_FALSE :
                c2.real = d1 / c2r;
                c2.iota = d1 / c2i * -1;
                complexResult = c2;
                resultFlag = RESULT_COMPLEX;
                break;
        }
    }

    @Override
    public void function(ComplexNumber c1, ComplexNumber c2) {
        double c1r = c1.real, c1i = c1.iota, c2r = c2.real, c2i = c2.iota;
        double real_sol = ((c1r * c2r) + (c1i * c2i)) / ((c2r * c2r) + (c2i * c2i));
        double iota_sol = ((c1r * c2i * -1) + (c2r * c1i)) / ((c2r * c2r) + (c2i * c2i));
        c2.real = real_sol;
        c2.iota = iota_sol;
        complexResult = c2;
        resultFlag = RESULT_COMPLEX;
    }
}
