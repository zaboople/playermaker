package org.tmotte.pm;

/**
 * Takes a "remainder" and a count of iterations, and "spreads" the remainder
 * evenly out across those iterations, with gaps.
 */
class Spreader {

    public static boolean[] array(int iterations, int remainder) {
        if (remainder<0)
            throw new IllegalArgumentException("Remainder "+remainder+" should be > 0");
        if (iterations<=remainder)
            throw new IllegalArgumentException("Iterations "+iterations+" < remainder "+remainder);
        final boolean[] array=new boolean[iterations];
        double gap=(double)iterations / (double)(1+remainder);
        double dposition=gap;
        while (dposition<array.length && remainder>0) {
            int index=(int)Math.floor(dposition);
            if (!array[index]) {
                remainder--;
                array[index]=true;
            }
            dposition+=gap;
        }
        if (remainder!=0)
            throw new RuntimeException("Remainder is "+remainder+", should be 0");
        return array;
    }

}