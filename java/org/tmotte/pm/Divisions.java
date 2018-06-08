package org.tmotte.pm;
public class Divisions {
    public final static int triplet128=2;
    public final static int triplet64=triplet128 * 2;
    public final static int triplet32=triplet64  * 2;
    public final static int triplet16=triplet32  * 2;
    public final static int triplet8 =triplet16  * 2;

    public final static int reg128=3;
    public final static int reg64=reg128 * 2;
    public final static int reg32=reg64 * 2;
    public final static int reg16=reg32 * 2;
    public final static int reg8=reg16 * 2;
    public final static int reg4=reg8 * 2;
    public final static int reg2=reg4 * 2;
    public final static int whole=reg2 * 2;

    /**
     * Convert a triplet e.g. expressed as 8.3 to an 8th of a triplet.
     * as well as a dotted e.g. expressed as 8.0 to an 8th + 16th
     * - note that 8.0 can be written as "8.", i.e. without the zero. Nice.
     */
    public static long convert(double d) {
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

    public static long convert(int d) { //FIXME what a mess but it works
        //System.out.println("unconvert "+d+" / 128 * "+reg128);
        if (d==0) return 0;
        //if (d>128) throw new RuntimeException("The value "+d+" is greater than the limit of 128");
        return 128L  * reg128 / ((long)d);
    }

    public static long convert(Number number) {
        if (number instanceof Long)
            return (Long) number;
        else
        if (number instanceof Double)
            return convert(number.doubleValue());
        else
        if (number instanceof Integer)
            return convert(number.intValue());
        else
        if (number instanceof Float)
            return convert(((Float)number).doubleValue());
        else
            throw new RuntimeException("Can't handle number: "+number);
    }

}
