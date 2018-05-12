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
     * as well as a dotted e.g. expressed as 8.2 to an 8th + 16th.
     */
    public static long convert(double d) {
        long main=(long)Math.floor(d);
        long fractional=Math.round((d-main) * 10);
        main=convert(main);
        return fractional==2
            ?(main * 2) / 3   //triplet
            : main + (main / 2); //dotted
    }

    public static long convert(long d) { //FIXME what a mess but it works
        //System.out.println("unconvert "+d+" / 128 * "+reg128);
        if (d==0) return 0;
        //if (d>128) throw new RuntimeException("The value "+d+" is greater than the limit of 128");
        return 128  * reg128 / d;
    }
    public static long unconvert(long d) {
        System.out.println("unconvert "+d+" * 128 / "+reg128);
        return (d*128)/reg128;
    }

}
