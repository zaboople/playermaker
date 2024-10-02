package org.tmotte.pm;
import java.util.Map;
import java.util.HashMap;

class Divisions {
    final static int triplet128=2;
    final static int triplet64=triplet128 * 2;
    final static int triplet32=triplet64  * 2;
    final static int triplet16=triplet32  * 2;
    final static int triplet8 =triplet16  * 2;

    final static int reg128=3;
    final static int reg64=reg128 * 2;
    final static int reg32=reg64 * 2;
    final static int reg16=reg32 * 2;
    final static int reg8=reg16 * 2;
    final static int reg4=reg8 * 2;
    final static int reg2=reg4 * 2;
    final static int whole=reg2 * 2;

    private final static Map<Class<?>, ConverterLambda> converters=makeConverterMap();

    /**
     * Convert a triplet e.g. expressed as 8.3 to an 8th of a triplet,
     * or a dotted e.g. expressed as 8.0 to an 8th + 16th.
     * <br>
     * Note that 8.0 can be written as "8.", i.e. without the zero. Nice.
     */
    static long convert(double d) {
        long main=(long)Math.floor(d);
        long fractional=Math.round((d-main) * 10);
        double dd=8.;
        main=convert((int)main);
        if (fractional==3)
            //triplet
            return (main * 2) / 3;
        else
        if (fractional==2 || fractional==1 || fractional==0)
            return main + (main / 2); //dotted
        else
            throw new RuntimeException("Don't know what to do with "+d);
    }

    static long convert(int d) {
        //System.out.println("unconvert "+d+" / 128 * "+reg128);
        if (d==0) return 0;
        //if (d>128) throw new RuntimeException("The value "+d+" is greater than the limit of 128");
        return 128L  * reg128 / ((long)d);
    }

    static long convert(Number number) {
        ConverterLambda c=converters.get(number.getClass());
        if (c==null)
            throw new RuntimeException("Can't handle number: "+number.getClass());
        return c.convert(number);
    }

    static long convert(double... ds) {
        long result=0;
        for (double dd: ds)
            result+=convert(dd);
        return result;
    }
    static long convert(int... ds) {
        long result=0;
        for (int dd: ds)
            result+=convert(dd);
        return result;
    }
    static long convert(Number... ds) {
        long result=0;
        for (Number dd: ds)
            result+=convert(dd);
        return result;
    }

    private static Map<Class<?>, ConverterLambda> makeConverterMap() {
        Map<Class<?>, ConverterLambda> map=new HashMap<>();
        map.put(Long.class, number->
            (Long) number
        );
        map.put(Double.class, number->
            convert(number.doubleValue())
        );
        map.put(java.math.BigDecimal.class, number->
            convert(number.doubleValue())
        );
        map.put(Integer.class, number->
            convert(number.intValue())
        );
        map.put(Float.class, number->
            convert(number.doubleValue())
        );
        map.put(Tie.class, number->
            convert(
                ((Tie)number).durations
            )
        );
        return map;
    }
    private static interface ConverterLambda {
        public long convert(Number number);
    }
}
