package org.tmotte.pm;

/**
 * Takes a "remainder" and a count of iterations, and "spreads" the remainder
 * evenly out across those iterations, with gaps.
 */
public class Spreader {


    public static boolean[] array(int iterations, int remainder) {
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

    public static void main(String[] args) {
        spreadTest(4, 2);
        spreadTest(5, 2);
        spreadTest(10, 1);
        spreadTest(10, 3);
        spreadTest(20, 1);
        spreadTest(20, 2);
        spreadTest(20, 3);
        spreadTest(20, 4);
        spreadTest(20, 5);
        spreadTest(20, 6);
        spreadTest(21, 6);
        spreadTest(100, 14);
        spreadTest(100, 33);
        spreadTest(100, 37);
        spreadTest(100, 72);
        spreadTest(100, 99);
        spreadTest(100, 10);
    }
    private static void spreadTest(int time, int remainder) {
        System.out.println("\nspreadin "+time+" "+remainder);
        boolean[] array=array(time, remainder);
        for (int i=0; i<array.length; i++){
            if (array[i]) remainder--;
            System.out.print(array[i] ?"+" :"_");
        }
        System.out.println();
        if (remainder!=0) throw new RuntimeException("Remainder: "+remainder);
        /*
        Spreader spreader=new Spreader(time, remainder);
        for (int i=0; i<time; i++)
            System.out.print("["+i+" "+spreader.once()+" "+spreader.remainder+"]...");
        System.out.println();
        */
    }


}